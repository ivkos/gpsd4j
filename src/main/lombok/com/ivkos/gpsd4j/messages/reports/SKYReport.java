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
import com.ivkos.gpsd4j.messages.Satellite;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static lombok.AccessLevel.NONE;

/**
 * A SKY object reports a sky view of the GPS satellite positions. If there is no GPS device available, or no skyview
 * has been reported yet, only the "class" field will reliably be present.
 * <p>
 * Many devices compute dilution of precision factors but do not include them in their reports. Many that do report DOPs
 * report only HDOP, two-dimensional circular error. gpsd always passes through whatever the device actually reports,
 * then attempts to fill in other DOPs by calculating the appropriate determinants in a covariance matrix based on the
 * satellite view. DOPs may be missing if some of these determinants are singular. It can even happen that the device
 * reports an error estimate in meters when the corresponding DOP is unavailable; some devices use more sophisticated
 * error modeling than the covariance calculation.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class SKYReport extends GpsdMessage
{
   public static final String CLASS = "SKY";

   /**
    * @return Name of originating device
    */
   private String device;

   private LocalDateTime time;

   /**
    * @return Time dilution of precision, a dimensionless factor which should be multiplied by a base UERE to get an
    * error estimate.
    */
   @JsonProperty("tdop")
   private Double timeDOP;

   @Getter(NONE)
   private List<Satellite> satellites;

   /**
    * @return Longitudinal dilution of precision, a dimensionless factor which should be multiplied by a base UERE to
    * get an error estimate.
    */
   @JsonProperty("xdop")
   private Double longitudeDOP;

   /**
    * @return Latitudinal dilution of precision, a dimensionless factor which should be multiplied by a base UERE to get
    * an error estimate.
    */
   @JsonProperty("ydop")
   private Double latitudeDOP;

   /**
    * @return Altitude dilution of precision, a dimensionless factor which should be multiplied by a base UERE to get an
    * error estimate.
    */
   @JsonProperty("vdop")
   private Double altitudeDOP;

   /**
    * @return Horizontal dilution of precision, a dimensionless factor which should be multiplied by a base UERE to get
    * a circular error estimate.
    */
   @JsonProperty("hdop")
   private Double horizontalDOP;

   /**
    * @return Spherical dilution of precision, a dimensionless factor which should be multiplied by a base UERE to get
    * an error estimate.
    */
   @JsonProperty("pdop")
   private Double sphericalDOP;

   /**
    * @return Hyperspherical dilution of precision, a dimensionless factor which should be multiplied by a base UERE to
    * get an error estimate.
    */
   @JsonProperty("gdop")
   private Double hypersphericalDOP;

   /**
    * @return List of satellite objects in skyview
    */
   public List<Satellite> getSatellites()
   {
      return satellites != null ? unmodifiableList(satellites) : emptyList();
   }

   @Override
   public String getGpsdClass()
   {
      return CLASS;
   }
}
