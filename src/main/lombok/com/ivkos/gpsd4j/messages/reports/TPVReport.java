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

package com.ivkos.gpsd4j.messages.reports;

import com.ivkos.gpsd4j.messages.GpsdMessage;
import com.ivkos.gpsd4j.messages.enums.NMEAMode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class TPVReport extends GpsdMessage
{
   public static final String CLASS = "TPV";

   private String device;
   private NMEAMode mode;

   private LocalDateTime time;
   private Double ept;

   private Double lat, lon, alt;
   private Double epx, epy, epv;

   private Double track, speed, climb;
   private Double epd, eps, epc;

   @Override
   public String getGpsdClass()
   {
      return CLASS;
   }
}
