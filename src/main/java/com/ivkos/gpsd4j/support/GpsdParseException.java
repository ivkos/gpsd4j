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

package com.ivkos.gpsd4j.support;

/**
 * Thrown to indicate a failure to parse data received from gpsd
 */
public class GpsdParseException extends RuntimeException
{
   public GpsdParseException()
   {
      super();
   }

   public GpsdParseException(String message)
   {
      super(message);
   }

   public GpsdParseException(String format, Object... args)
   {
      super(String.format(format, args));
   }

   public GpsdParseException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public GpsdParseException(Throwable cause)
   {
      super(cause);
   }
}
