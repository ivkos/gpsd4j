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

import lombok.*;

@Getter
@Setter
@Builder
public class GpsdClientOptions
{
   public static final boolean DEFAULT_RECONNECT_ON_DISCONNECT = true;
   public static final int DEFAULT_CONNECT_TIMEOUT = 3000;
   public static final int DEFAULT_IDLE_TIMEOUT = 120;
   public static final int DEFAULT_RECONNECT_ATTEMPTS = Integer.MAX_VALUE;
   public static final int DEFAULT_RECONNECT_INTERVAL = 3000;

   /**
    * Whether to reconnect when the connection to gpsd is lost.
    */
   @Builder.Default
   private boolean reconnectOnDisconnect = DEFAULT_RECONNECT_ON_DISCONNECT;

   /**
    * The connect timeout, in ms. How long to wait when connecting before giving up.
    */
   @Builder.Default
   private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

   /**
    * Idle timeout, in seconds. Zero means don't timeout. This determines if the connection will timeout and be
    * closed if no data is received within the timeout.
    */
   @Builder.Default
   private int idleTimeout = DEFAULT_IDLE_TIMEOUT;

   /**
    * The maximum number of reconnect attempts.
    */
   @Builder.Default
   private int reconnectAttempts = DEFAULT_RECONNECT_ATTEMPTS;

   /**
    * The reconnect interval, in ms.
    */
   @Builder.Default
   private int reconnectInterval = DEFAULT_RECONNECT_INTERVAL;
}
