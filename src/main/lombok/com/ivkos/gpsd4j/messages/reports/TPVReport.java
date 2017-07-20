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
import com.ivkos.gpsd4j.messages.enums.NMEAMode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * A TPV object is a time-position-velocity report. The "class" and "mode" fields will reliably be present. The "mode"
 * field will be emitted before optional fields that may be absent when there is no fix. Error estimates will be emitted
 * after the fix components they're associated with. Others may be reported or not depending on the fix quality.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class TPVReport extends GpsdMessage
{
   public static final String CLASS = "TPV";

   /**
    * @return Name of originating device.
    */
   private String device;

   /**
    * @return NMEA mode
    */
   private NMEAMode mode;

   /**
    * @return Time/date stamp in ISO8601 format, UTC. May have a fractional part of up to .001sec precision. May be
    * absent if mode is not 2D or 3D
    */
   private LocalDateTime time;

   /**
    * @return Estimated timestamp error (seconds, 95% confidence). Present if time is present.
    */
   @JsonProperty("ept")
   private Double timeError;

   /**
    * @return Latitude in degrees: +/- signifies North/South. Present when mode is 2D or 3D.
    */
   @JsonProperty("lat")
   private Double latitude;

   /**
    * @return Longitude in degrees: +/- signifies East/West. Present when mode is 2D or 3D.
    */
   @JsonProperty("lon")
   private Double longitude;

   /**
    * @return Altitude in meters. Present if mode is 3D.
    */
   @JsonProperty("alt")
   private Double altitude;

   /**
    * @return Longitude error estimate in meters, 95% confidence. Present if mode is 2D or 3D and DOPs can be calculated
    * from the satellite view.
    */
   @JsonProperty("epx")
   private Double longitudeError;

   /**
    * @return Latitude error estimate in meters, 95% confidence. Present if mode is 2D or 3D and DOPs can be calculated
    * from the satellite view.
    */
   @JsonProperty("epy")
   private Double latitudeError;

   /**
    * @return Estimated vertical error in meters, 95% confidence. Present if mode is 3D and DOPs can be calculated from
    * the satellite view.
    */
   @JsonProperty("epv")
   private Double altitudeError;

   /**
    * @return Course over ground, degrees from true north.
    */
   @JsonProperty("track")
   private Double course;

   /**
    * @return Speed over ground, meters per second.
    */
   @JsonProperty("speed")
   private Double speed;

   /**
    * @return Climb (positive) or sink (negative) rate, meters per second.
    */
   @JsonProperty("climb")
   private Double climbRate;

   /**
    * @return Direction error estimate in degrees, 95% confidence.
    */
   @JsonProperty("epd")
   private Double courseError;

   /**
    * @return Speed error estinmate in meters/sec, 95% confidence.
    */
   @JsonProperty("eps")
   private Double speedError;

   /**
    * @return Climb/sink error estimate in meters/sec, 95% confidence.
    */
   @JsonProperty("epc")
   private Double climbRateError;

   @Override
   public String getGpsdClass()
   {
      return CLASS;
   }
}
