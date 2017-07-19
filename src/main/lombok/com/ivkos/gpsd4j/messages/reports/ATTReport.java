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

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class ATTReport extends GpsdMessage
{
   public static final String CLASS = "ATT";

   private String device;

   private LocalDateTime time;

   private Double heading, pitch, yaw, roll, dip;
   private String mag_st, pitch_st, yaw_st, roll_st;

   private Double mag_len, mag_x, mag_y, mag_z;
   private Double acc_len, acc_x, acc_y, acc_z;
   private Double gyro_x, gyro_y;

   private Double depth;
   private Double temperature;

   @Override
   public String getGpsdClass()
   {
      return CLASS;
   }
}
