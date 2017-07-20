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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivkos.gpsd4j.messages.GpsdMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This message is emitted on each cycle and reports the offset between the host's clock time and the GPS time at top of
 * second (actually, when the first data for the reporting cycle is received).
 * <p>
 * This message exactly mirrors the PPS message except for two details.
 * <p>
 * TOFF emits no NTP precision, this is assumed to be -2. See the NTP documentation for their definition of precision.
 * <p>
 * The TOFF message reports the GPS time as derived from the GPS serial data stream. The PPS message reports the GPS
 * time as derived from the GPS PPS pulse.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class TOFFReport extends GpsdMessage
{
   public static final String CLASS = "TOFF";

   /**
    * @return Name of originating device
    */
   private String device;

   /**
    * @return seconds from the GPS clock
    */
   @JsonProperty("real_sec")
   private Double gpsClockSeconds;

   /**
    * @return nanoseconds from the GPS clock
    */
   @JsonProperty("real_nsec")
   private Double gpsClockNanoSeconds;

   /**
    * @return seconds from the system clock
    */
   @JsonProperty("clock_sec")
   private Double systemClockSeconds;

   /**
    * @return nanoseconds from the system clock
    */
   @JsonProperty("clock_nsec")
   private Double systemClockNanoSeconds;

   @Override
   public String getGpsdClass()
   {
      return CLASS;
   }
}
