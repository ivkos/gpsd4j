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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * A GST object is a pseudorange noise report.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class GSTReport extends GpsdMessage
{
   public static final String CLASS = "GST";

   /**
    * @return Name of originating device
    */
   private String device;

   /**
    * @return Time since the Unix epoch, UTC. May have a fractional part of up to .001sec precision.
    */
   private LocalDateTime time;

   /**
    * @return Value of the standard deviation of the range inputs to the navigation process (range inputs include
    * pseudoranges and DGPS corrections).
    */
   private Double rms;

   /**
    * @return Standard deviation of semi-major axis of error ellipse, in meters.
    */
   private Double major;

   /**
    * @return Standard deviation of semi-minor axis of error ellipse, in meters.
    */
   private Double minor;

   /**
    * @return Orientation of semi-major axis of error ellipse, in degrees from true north.
    */
   private Double orient;

   /**
    * @return Standard deviation of latitude error, in meters.
    */
   private Double lat;

   /**
    * @return Standard deviation of longitude error, in meters.
    */
   private Double lon;

   /**
    * @return Standard deviation of altitude error, in meters.
    */
   private Double alt;

   @Override
   public String getGpsdClass()
   {
      return CLASS;
   }
}
