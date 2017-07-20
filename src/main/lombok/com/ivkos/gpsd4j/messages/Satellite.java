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
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Satellite
{
   /**
    * PRN ID of the satellite. 1-63 are GNSS satellites, 64-96 are GLONASS satellites, 100-164 are SBAS satellites
    *
    * @return PRN id
    */
   @JsonProperty("PRN")
   private Integer PRN;

   /**
    * @return Azimuth, degrees from true north.
    */
   @JsonProperty("az")
   private Double azimuth;

   /**
    * @return Elevation in degrees.
    */
   @JsonProperty("el")
   private Double elevation;

   /**
    * @return Signal strength in dB.
    */
   @JsonProperty("ss")
   private Double signalStrength;

   /**
    * Used in current solution? (SBAS/WAAS/EGNOS satellites may be flagged used if the solution has corrections from
    * them, but not all drivers make this information available.)
    *
    * @return Used in current solution?
    */
   @JsonProperty("used")
   private boolean used;
}
