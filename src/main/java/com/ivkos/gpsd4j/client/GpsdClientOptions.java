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

public class GpsdClientOptions
{
   public static final boolean DEFAULT_RECONNECT_ON_DISCONNECT = true;
   public static final int DEFAULT_CONNECT_TIMEOUT = 3000;
   public static final int DEFAULT_IDLE_TIMEOUT = 120;
   public static final int DEFAULT_RECONNECT_ATTEMPTS = Integer.MAX_VALUE;
   public static final int DEFAULT_RECONNECT_INTERVAL = 3000;

   private boolean reconnectOnDisconnect = DEFAULT_RECONNECT_ON_DISCONNECT;
   private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
   private int idleTimeout = DEFAULT_IDLE_TIMEOUT;
   private int reconnectAttempts = DEFAULT_RECONNECT_ATTEMPTS;
   private int reconnectInterval = DEFAULT_RECONNECT_INTERVAL;

   /**
    * @return Whether to reconnect when the connection to gpsd is lost.
    */
   public boolean getReconnectOnDisconnect()
   {
      return reconnectOnDisconnect;
   }

   /**
    * @param reconnectOnDisconnect Whether to reconnect when the connection to gpsd is lost.
    *
    * @return a reference to this, so the API can be used fluently
    */
   public GpsdClientOptions setReconnectOnDisconnect(boolean reconnectOnDisconnect)
   {
      this.reconnectOnDisconnect = reconnectOnDisconnect;
      return this;
   }

   /**
    * @return The connect timeout, in ms. How long to wait when connecting before giving up.
    */
   public int getConnectTimeout()
   {
      return connectTimeout;
   }

   /**
    * @param connectTimeout The connect timeout, in ms. How long to wait when connecting before giving up.
    *
    * @return a reference to this, so the API can be used fluently
    */
   public GpsdClientOptions setConnectTimeout(int connectTimeout)
   {
      this.connectTimeout = connectTimeout;
      return this;
   }

   /**
    * @return Idle timeout, in seconds. Zero means don't timeout. This determines if the connection will timeout and be
    * closed if no data is received within the timeout.
    */
   public int getIdleTimeout()
   {
      return idleTimeout;
   }

   /**
    * @param idleTimeout Idle timeout, in seconds. Zero means don't timeout. This determines if the connection will
    *                    timeout and be closed if no data is received within the timeout.
    *
    * @return a reference to this, so the API can be used fluently
    */
   public GpsdClientOptions setIdleTimeout(int idleTimeout)
   {
      this.idleTimeout = idleTimeout;
      return this;
   }

   /**
    * @return The maximum number of reconnect attempts.
    */
   public int getReconnectAttempts()
   {
      return reconnectAttempts;
   }

   /**
    * @param reconnectAttempts The maximum number of reconnect attempts.
    *
    * @return a reference to this, so the API can be used fluently
    */
   public GpsdClientOptions setReconnectAttempts(int reconnectAttempts)
   {
      this.reconnectAttempts = reconnectAttempts;
      return this;
   }

   /**
    * @return The reconnect interval, in ms.
    */
   public int getReconnectInterval()
   {
      return reconnectInterval;
   }

   /**
    * @param reconnectInterval The reconnect interval, in ms.
    *
    * @return a reference to this, so the API can be used fluently
    */
   public GpsdClientOptions setReconnectInterval(int reconnectInterval)
   {
      this.reconnectInterval = reconnectInterval;
      return this;
   }
}
