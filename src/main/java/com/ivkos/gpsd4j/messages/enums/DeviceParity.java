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

package com.ivkos.gpsd4j.messages.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DeviceParity
{
   NO("N"),
   ODD("O"),
   EVEN("E");

   private final String code;

   DeviceParity(String code)
   {
      this.code = code;
   }

   @JsonValue
   public String getCode()
   {
      return code;
   }

   @JsonCreator
   public static DeviceParity forCode(String code)
   {
      for (DeviceParity value : DeviceParity.values()) {
         if (value.getCode().equals(code)) {
            return value;
         }
      }

      return null;
   }
}
