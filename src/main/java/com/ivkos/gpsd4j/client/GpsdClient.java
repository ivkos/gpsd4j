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

import com.ivkos.gpsd4j.messages.GpsdCommandMessage;
import com.ivkos.gpsd4j.messages.GpsdMessage;
import com.ivkos.gpsd4j.messages.WatchMessage;
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
      this(serverHost, serverPort, GpsdClientOptions.builder().build());
   }

   /**
    * Starts the client.
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

   public GpsdClient sendCommand(String rawCommand)
   {
      if (!isRunning()) throw new IllegalStateException("Client is not running");

      this.clientSocket.write(rawCommand);
      log.debug("Wrote: {}", rawCommand);

      return this;
   }

   public GpsdClient sendCommand(GpsdCommandMessage cmd)
   {
      return this.sendCommand(format(
            "?%s=%s",
            cmd.getGpsdClass(), serialize(cmd)
      ));
   }

   @SuppressWarnings("unchecked")
   public <T extends GpsdCommandMessage> GpsdClient sendCommand(T cmd, Consumer<T> consumer)
   {
      this.addHandler((Class<T>) cmd.getClass(), new Consumer<T>()
      {
         @Override
         public void accept(T t)
         {
            consumer.accept(t);
            GpsdClient.this.removeHandler(this);
         }
      });

      return this.sendCommand(cmd);
   }

   public GpsdClient watch(boolean enable, boolean dumpJson)
   {
      WatchMessage watch = new WatchMessage();
      watch.setEnable(enable);
      watch.setJson(dumpJson);

      return this.sendCommand(watch);
   }

   public GpsdClient watch()
   {
      return this.watch(true, true);
   }

   @SuppressWarnings("unchecked")
   public <T extends GpsdMessage> GpsdClient addHandler(Class<T> responseClass, Consumer<T> consumer)
   {
      List<Consumer<GpsdMessage>> list = this.handlers.getOrDefault(responseClass, synchronizedList(new ArrayList<>()));
      list.add((Consumer<GpsdMessage>) consumer);
      this.handlers.putIfAbsent(responseClass, list);

      return this;
   }

   /**
    * @param responseClass the response class the handler is associated with
    * @param consumer      the handler to remove
    *
    * @return true if the handler has been removed, or false if was not registered before
    */
   public <T extends GpsdMessage> boolean removeHandler(Class<T> responseClass, Consumer<T> consumer)
   {
      return this.handlers.getOrDefault(responseClass, emptyList()).remove(consumer);
   }

   /**
    * Remove the handler from all types of objects it was registered for.
    *
    * @param consumer the handler to remove
    *
    * @return true if the handler has been removed, or false if was not registered before
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

      if (options.isReconnectOnDisconnect()) {
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
