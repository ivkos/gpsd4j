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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class WatchMessage extends GpsdCommandMessage
{
   public static final String CLASS = "WATCH";

   /**
    * Enable (true) or disable (false) watcher mode. Default is true.
    *
    * @return watch mode
    * @param enabled watcher mode
    */
   @Setter
   @JsonProperty("enable")
   private boolean enabled = true;

   /**
    * Enable (true) or disable (false) dumping of JSON reports. Default is false.
    *
    * @return dumping of JSON reports
    * @param dumpJson dumping of JSON reports
    */
   @Setter
   @JsonProperty("json")
   private Boolean dumpJson = false;

   /**
    * Enable (true) or disable (false) dumping of binary packets as pseudo-NMEA. Default is false.
    *
    * @return pseudo-NMEA
    */
   private boolean nmea = false;

   /**
    * Controls 'raw' mode. When this attribute is set to 1 for a channel, gpsd reports the unprocessed NMEA or AIVDM
    * data stream from whatever device is attached. Binary GPS packets are hex-dumped. RTCM2 and RTCM3 packets are not
    * dumped in raw mode. When this attribute is set to 2 for a channel that processes binary data, gpsd reports the
    * received data verbatim without hex-dumping.
    *
    * @return raw mode
    */
   private Integer raw;

   /**
    * If true, apply scaling divisors to output before dumping; default is false.
    *
    * @return apply scaling divisors
    * @param scaled apply scaling divisors
    */
   @Setter
   private boolean scaled = false;

   /**
    * If true, aggregate AIS type24 sentence parts. If false, report each part as a separate JSON object, leaving the
    * client to match MMSIs and aggregate. Default is false. Applies only to AIS reports.
    *
    * @return aggregate AIS type24 sentence parts
    */
   private Boolean split24 = false;

   /**
    * If true, emit the TOFF JSON message on each cycle and a PPS JSON message when the device issues 1PPS. Default is
    * false.
    *
    * @return emit TOFF and PPS JSON messages
    * @param pps emit TOFF and PPS JSON messages
    */
   @Setter
   private Boolean pps = false;

   /**
    * If present, enable watching only of the specified device rather than all devices. Useful with raw and NMEA modes
    * in which device responses aren't tagged. Has no effect when used with enable:false.
    *
    * @return If present, enable watching only of the specified device
    * @param device If present, enable watching only of the specified device
    */
   @Setter
   private String device;

   @Override
   public String getGpsdClass()
   {
      return CLASS;
   }
}
