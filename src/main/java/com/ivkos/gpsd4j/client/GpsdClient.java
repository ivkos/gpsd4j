/*
 * Copyright 2017 Ivaylo Stoyanov <me@ivkos.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivkos.gpsd4j.client;

import com.ivkos.gpsd4j.messages.*;
import com.ivkos.gpsd4j.support.GpsdParseException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;

import static com.ivkos.gpsd4j.support.SerializationHelper.*;
import static java.lang.String.format;
import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

public class GpsdClient
{
   private static final Logger log = LoggerFactory.getLogger(GpsdClient.class);

   private static final int BUFFER_SIZE = 4 * 1024;

   private final Map<Class<? extends GpsdMessage>, List<Consumer<GpsdMessage>>> handlers =
         synchronizedMap(new HashMap<>());

   private final StampedLock startingLock = new StampedLock();
   private final StampedLock stoppingLock = new StampedLock();

   private final String serverHost;
   private final int serverPort;
   private final GpsdClientOptions options;

   private boolean running = false;
   private boolean stopping = false;
   private long startingLockStamp;

   private Vertx vertx;
   private NetClient netClient;
   private NetSocket clientSocket;

   /**
    * Creates a new instance
    *
    * @param serverHost the gpsd server's hostname
    * @param serverPort the gpsd server's port
    * @param options    options for the client
    *
    * @throws NullPointerException if options is null
    */
   public GpsdClient(String serverHost, int serverPort, GpsdClientOptions options)
   {
      this.serverHost = serverHost;
      this.serverPort = serverPort;
      this.options = requireNonNull(options, "options must not be null");
   }

   /**
    * Creates a new instance
    *
    * @param serverHost the gpsd server's hostname
    * @param serverPort the gpsd server's port
    */
   public GpsdClient(String serverHost, int serverPort)
   {
      this(serverHost, serverPort, new GpsdClientOptions());
   }

   /**
    * Starts the client.
    *
    * @return a reference to this, so the API can be used fluently
    *
    * @throws IllegalStateException if start() is called and the client is already running
    */
   public GpsdClient start()
   {
      if (isRunning()) throw new IllegalStateException("Client is already running");

      createVertx();
      createNetClient();

      connectToGpsd();

      return this;
   }

   /**
    * Sends a raw command to the gpsd server in the form of a string.
    *
    * @param rawCommand the command
    *
    * @return a reference to this, so the API can be used fluently
    *
    * @throws IllegalStateException if the client has not been started or it was stopped
    */
   public GpsdClient sendCommand(String rawCommand)
   {
      if (!isRunning()) throw new IllegalStateException("Client is not running");

      this.clientSocket.write(rawCommand);
      log.debug("Wrote: {}", rawCommand);

      return this;
   }

   /**
    * Sends a command to the server.
    *
    * @param command the command to send
    *
    * @return a reference to this, so the API can be used fluently
    *
    * @throws IllegalStateException if the client has not been started or it was stopped
    */
   public GpsdClient sendCommand(GpsdCommandMessage command)
   {
      return this.sendCommand(format(
            "?%s=%s",
            command.getGpsdClass(), serialize(command)
      ));
   }

   /**
    * Sends a command to the gpsd server and binds a handler that will be executed exactly once when the server
    * responds to the command.
    *
    * @param command         the command to send
    * @param responseHandler the handler for the server's response to the command
    * @param <T>             the type of the command and the response
    *
    * @return a reference to this, so the API can be used fluently
    *
    * @throws IllegalStateException if the client has not been started or it was stopped
    */
   @SuppressWarnings("unchecked")
   public <T extends GpsdCommandMessage> GpsdClient sendCommand(T command, Consumer<T> responseHandler)
   {
      this.addHandler((Class<T>) command.getClass(), new Consumer<T>()
      {
         // ensures this consumer doesn't get executed more than once
         private volatile boolean done = false;

         @Override
         public void accept(T t)
         {
            if (done) return;

            done = true;
            GpsdClient.this.removeHandler(this);

            responseHandler.accept(t);
         }
      });

      return this.sendCommand(command);
   }

   /**
    * Sends a WATCH command to the gpsd server to enable/disable watch mode and enable/disable reporting of messages.
    *
    * @param enable         whether to enable watch mode
    * @param reportMessages whether to report
    *
    * @return a reference to this, so the API can be used fluently
    *
    * @throws IllegalStateException if the client has not been started or it was stopped
    */
   public GpsdClient watch(boolean enable, boolean reportMessages)
   {
      WatchMessage watch = new WatchMessage();
      watch.setEnabled(enable);
      watch.setDumpJson(reportMessages);

      return this.sendCommand(watch);
   }

   /**
    * Sends a WATCH command to the gpsd server to enable watch mode and start reporting messages.
    * <p>
    * The effect of this call is equivalent to that of calling {@link #watch(boolean, boolean) watch(true, true)}.
    *
    * @return a reference to this, so the API can be used fluently
    *
    * @throws IllegalStateException if the client has not been started or it was stopped
    */
   public GpsdClient watch()
   {
      return this.watch(true, true);
   }

   /**
    * Adds a handler for a type of messages. The handler is executed upon
    * receiving that type of message and gets passed the message object itself.
    * <p>
    * The type of the message can be a concrete one (e.g. {@link VersionMessage}), or a more abstract one like {@link
    * GpsdCommandMessage}. In the latter case, any received message that is of subtype of {@link GpsdCommandMessage}
    * will be handled with this handler, as well as other handlers registered for its concrete type. The order of
    * execution of handlers is from most concrete first to most abstract last.
    *
    * @param messageType the type of the messages to register the handler for
    * @param handler     the handler that gets passed the message object
    * @param <T>         the type of the message
    *
    * @return a reference to this, so the API can be used fluently
    */
   @SuppressWarnings("unchecked")
   public <T extends GpsdMessage> GpsdClient addHandler(Class<T> messageType, Consumer<T> handler)
   {
      List<Consumer<GpsdMessage>> list = this.handlers.getOrDefault(messageType, synchronizedList(new ArrayList<>()));
      list.add((Consumer<GpsdMessage>) handler);
      this.handlers.putIfAbsent(messageType, list);

      return this;
   }

   /**
    * Adds a generic handler that handles all types of gpsd messages, including ERRORs.
    * <p>
    * The effect of this call is equivalent to that of calling
    * {@link #addHandler(Class, Consumer) addHandler(GpsdMessage.class, handler)}.
    *
    * @param handler the handler that gets passed an object of subtype of {@link GpsdMessage}
    *
    * @return a reference to this, so the API can be used fluently
    */
   public GpsdClient addHandler(Consumer<GpsdMessage> handler)
   {
      return this.addHandler(GpsdMessage.class, handler);
   }

   /**
    * Adds a handler that handles gpsd ERROR messages ({@link ErrorMessage}).
    * <p>
    * The effect of this call is equivalent to that of calling
    * {@link #addHandler(Class, Consumer) addHandler(ErrorMessage.class, handler)}.
    *
    * @param handler the handler that gets passed an {@link ErrorMessage} object
    *
    * @return a reference to this, so the API can be used fluently
    */
   public GpsdClient addErrorHandler(Consumer<ErrorMessage> handler)
   {
      return this.addHandler(ErrorMessage.class, handler);
   }

   /**
    * Removes the handler from the message type (a subtype of {@link GpsdMessage}) it was registered for.
    *
    * @param messageType the message type the handler was registered for
    * @param handler     the handler to remove
    *
    * @return <tt>true</tt> if the handler was removed, or <tt>false</tt> if it has not been registered for this message
    * type before
    */
   public <T extends GpsdMessage> boolean removeHandler(Class<T> messageType, Consumer<T> handler)
   {
      return this.handlers.getOrDefault(messageType, emptyList()).remove(handler);
   }

   /**
    * Removes the handler from all types of messages it was registered for.
    *
    * @param consumer the handler to remove
    *
    * @return <tt>true</tt> if the handler was removed, or <tt>false</tt> if it has not been registered before
    */
   public <T extends GpsdMessage> boolean removeHandler(Consumer<T> consumer)
   {
      final boolean[] removed = { false };

      this.handlers.forEach((key, consumersList) -> {
         if (consumersList.remove(consumer)) {
            removed[0] = true;
         }
      });

      return removed[0];
   }

   /**
    * Shuts down the client.
    */
   public void stop()
   {
      {
         long stamp = this.stoppingLock.writeLock();
         this.stopping = true;
         this.stoppingLock.unlockWrite(stamp);
      }

      long startingStamp = this.startingLock.writeLock();

      if (netClient != null) {
         netClient.close();
         netClient = null;
      }

      if (vertx != null) {
         vertx.close(__ -> {
            this.running = false;
            this.startingLock.unlockWrite(startingStamp);

            {
               long stoppingStamp = this.stoppingLock.writeLock();
               this.stopping = false;
               this.stoppingLock.unlockWrite(stoppingStamp);
            }
         });
         vertx = null;
      } else {
         this.running = false;
         this.startingLock.unlockWrite(startingStamp);
      }
   }

   private void connectToGpsd()
   {
      log.info("Connecting to gpsd server {}:{}...", serverHost, serverPort);

      this.startingLockStamp = this.startingLock.writeLock();

      this.netClient.connect(this.serverPort, this.serverHost, this::handleConnectResult);
   }

   private void handleConnectResult(AsyncResult<NetSocket> res)
   {
      if (res.failed()) {
         log.error(format("Connection to gpsd server %s:%d failed", serverHost, serverPort), res.cause());

         this.startingLock.unlockWrite(this.startingLockStamp);
         this.stop();

         return;
      }

      this.clientSocket = res.result()
            .closeHandler(__ -> handleClose())
            .handler(buf -> {
               // Split new lines in case the buffer contains multiple JSON objects
               String[] split = buf.toString().split("(\\r\\n|\\r|\\n)+");
               for (String s : split) handleJsonString(s);
            });

      this.running = true;
      this.startingLock.unlockWrite(this.startingLockStamp);

      log.info("Successfully connected to gpsd server {}:{}", serverHost, serverPort);
   }

   private void handleClose()
   {
      long stamp = this.stoppingLock.tryOptimisticRead();
      boolean currentStopping = this.stopping;

      if (!stoppingLock.validate(stamp)) {
         stamp = this.stoppingLock.readLock();
         try {
            currentStopping = this.stopping;
         } finally {
            this.stoppingLock.unlockRead(stamp);
         }
      }

      if (currentStopping) {
         log.info("Client is shutting down...");
         return;
      }

      if (options.getReconnectOnDisconnect()) {
         log.warn("Disconnected from gpsd server {}:{}. Will now try to reconnect...", serverHost, serverPort);
         this.connectToGpsd();
      } else {
         log.info("Disconnected from gpsd server {}:{}", serverHost, serverPort);
         this.stop();
      }

   }

   private void handleJsonString(String jsonString)
   {
      GpsdMessage obj;
      try {
         obj = deserialize(jsonString);
      } catch (GpsdParseException e) {
         log.warn("Cannot parse JSON", e);
         return;
      }

      getClassHierarchy(obj).forEach(clazz -> {
         handlers.getOrDefault(clazz, emptyList()).forEach(handler -> vertx.executeBlocking(f -> {
            try {
               handler.andThen(__ -> f.complete()).accept(obj);
            } catch (Throwable t) {
               f.fail(t);
            }
         }, true, __ -> {}));
      });
   }

   private boolean isRunning()
   {
      long stamp = this.startingLock.tryOptimisticRead();

      boolean currentRunning = this.running;

      if (!this.startingLock.validate(stamp)) {
         stamp = this.startingLock.readLock();
         try {
            currentRunning = this.running;
         } finally {
            this.startingLock.unlockRead(stamp);
         }
      }

      return currentRunning;
   }

   private void createVertx()
   {
      this.vertx = Vertx.vertx();
   }

   private void createNetClient()
   {
      NetClientOptions netClientOptions = new NetClientOptions()
            .setConnectTimeout(options.getConnectTimeout())
            .setIdleTimeout(options.getIdleTimeout())
            .setReconnectAttempts(options.getReconnectAttempts())
            .setReconnectInterval(options.getReconnectInterval())
            .setReceiveBufferSize(BUFFER_SIZE);

      this.netClient = this.vertx.createNetClient(netClientOptions);
   }
}
