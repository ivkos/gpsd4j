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

package com.ivkos.gpsd4j.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivkos.gpsd4j.messages.enums.DeviceParity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class DeviceMessage extends GpsdCommandMessage
{
   public static final String CLASS = "DEVICE";

   @Setter
   private String path;

   private LocalDateTime activated;

   private String driver;

   @Setter
   private Integer bps;
   private DeviceParity parity;
   private Integer stopbits;

   @Setter
   @JsonProperty("native")
   private Integer _native;

   @Setter
   private Double cycle;

   private Double mincycle;

   @Override
   public String getGpsdClass()
   {
      return CLASS;
   }
}
