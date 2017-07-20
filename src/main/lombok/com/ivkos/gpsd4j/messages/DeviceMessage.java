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

@ToString
@EqualsAndHashCode(callSuper = false)
public class DeviceMessage extends GpsdCommandMessage
{
   public static final String CLASS = "DEVICE";

   /**
    * Name the device for which the control bits are being reported, or for which they are to be applied. This attribute
    * may be omitted only when there is exactly one subscribed channel.
    *
    * @return name of the device
    * @param path name of the device
    */
   @Getter
   @Setter
   private String path;

   /**
    * @return Time the device was activated
    */
   @Getter
   @JsonProperty("activated")
   private LocalDateTime timeActivated;

   /**
    * GPSD's name for the device driver type. Won't be reported before gpsd has seen identifiable packets from the
    * device.
    *
    * @return device driver type
    */
   @Getter
   private String driver;

   /**
    * @return Device speed in bits per second.
    * @param bps Device speed in bits per second.
    */
   @Getter
   @Setter
   private Integer bps;

   /**
    * @return parity of the serial connection
    * @param parity parity of the serial connection
    */
   @Getter
   @Setter
   private DeviceParity parity;

   /**
    * @return Stop bits (1 or 2).
    * @param stopBits Stop bits (1 or 2).
    */
   @Getter
   @Setter
   @JsonProperty("stopbits")
   private Integer stopBits;

   @JsonProperty("native")
   private Integer _native;

   /**
    * @return Device cycle time in seconds.
    * @param cycle Device cycle time in seconds.
    */
   @Getter
   @Setter
   private Double cycle;

   /**
    * @return Device minimum cycle time in seconds.
    */
   @Getter
   @JsonProperty("mincycle")
   private Double minimumCycle;

   /**
    * @return true for alternate mode (binary if it has one, for SiRF and Evermore chipsets in particular), false for
    * NMEA mode, null for unknown
    */
   public Boolean isNative()
   {
      if (_native == 1) return true;
      if (_native == 0) return false;
      return null;
   }

   /**
    * @param nativeMode true for alternate mode (binary if it has one, for SiRF and Evermore chipsets in particular),
    *                   false for NMEA mode
    */
   public void setNative(boolean nativeMode)
   {
      this._native = nativeMode ? 1 : 0;
   }

   @Override
   public String getGpsdClass()
   {
      return CLASS;
   }
}
