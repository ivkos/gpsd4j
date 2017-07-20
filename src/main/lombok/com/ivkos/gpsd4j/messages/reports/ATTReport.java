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

import java.time.LocalDateTime;

/**
 * An ATT object is a vehicle-attitude report. It is returned by digital-compass and gyroscope sensors; depending on
 * device, it may include: heading, pitch, roll, yaw, gyroscope, and magnetic-field readings. Because such sensors are
 * often bundled as part of marine-navigation systems, the ATT response may also include water depth.
 * <p>
 * The "class" and "mode" fields will reliably be present. Others may be reported or not depending on the specific
 * device type.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class ATTReport extends GpsdMessage
{
   public static final String CLASS = "ATT";

   /**
    * @return Name of originating device
    */
   private String device;

   private LocalDateTime time;

   /**
    * @return Heading, degrees from true north.
    */
   private Double heading;

   /**
    * @return Pitch in degrees.
    */
   private Double pitch;

   /**
    * @return Yaw in degrees
    */
   private Double yaw;

   /**
    * @return Roll in degrees.
    */
   private Double roll;

   /**
    * @return Local magnetic inclination, degrees, positive when the magnetic field points downward (into the Earth).
    */
   private Double dip;


   @JsonProperty("mag_st")
   private String magnetometerStatus;

   @JsonProperty("pitch_st")
   private Double pitchSensorStatus;

   @JsonProperty("yaw_st")
   private Double yawSensorStatus;

   @JsonProperty("roll_st")
   private Double rollSensorStatus;

   /**
    * @return Scalar magnetic field strength.
    */
   @JsonProperty("mag_len")
   private Double magneticFieldStrengthScalar;

   /**
    * @return X component of magnetic field strength.
    */
   @JsonProperty("mag_x")
   private Double magneticFieldStrengthX;

   /**
    * @return Y component of magnetic field strength.
    */
   @JsonProperty("mag_y")
   private Double magneticFieldStrengthY;

   /**
    * @return Z component of magnetic field strength.
    */
   @JsonProperty("mag_z")
   private Double magneticFieldStrengthZ;

   /**
    * @return Scalar acceleration.
    */
   @JsonProperty("acc_len")
   private Double accelerationScalar;

   /**
    * @return X component of acceleration.
    */
   @JsonProperty("acc_x")
   private Double accelerationX;

   /**
    * @return Y component of acceleration.
    */
   @JsonProperty("acc_y")
   private Double accelerationY;

   /**
    * @return Z component of acceleration.
    */
   @JsonProperty("acc_z")
   private Double accelerationZ;

   /**
    * @return X component of acceleration.
    */
   @JsonProperty("gyro_x")
   private Double gyroX;

   /**
    * @return Y component of acceleration.
    */
   @JsonProperty("gyro_y")
   private Double gyroY;

   /**
    * @return Water depth in meters.
    */
   @JsonProperty("depth")
   private Double waterDepth;

   /**
    * @return Temperature at sensor, degrees centigrade.
    */
   private Double temperature;

   @Override
   public String getGpsdClass()
   {
      return CLASS;
   }
}
