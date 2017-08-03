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

package com.ivkos.gpsd4j.tools;

public class UnitConverter
{
   public static final double KILOMETERS_IN_MILE = 1.609344;
   public static final double KILOMETERS_IN_NAUTICAL_MILE = 1.852;
   public static final double METERS_IN_FOOT = 0.3048;
   public static final double KILOMETERS_PER_HOUR_IN_METER_PER_SECOND = 3.6;
   public static final int SECONDS_IN_MINUTE = 60;
   public static final int METERS_IN_KILOMETER = 1000;

   private UnitConverter() {}

   public static double metersPerSecondToKilometersPerHour(double metersPerSecond)
   {
      return metersPerSecond * KILOMETERS_PER_HOUR_IN_METER_PER_SECOND;
   }

   public static double metersPerSecondToMilesPerHour(double metersPerSecond)
   {
      return metersPerSecondToKilometersPerHour(metersPerSecond) / KILOMETERS_IN_MILE;
   }

   public static double metersPerSecondToKnots(double metersPerSecond)
   {
      return metersPerSecondToKilometersPerHour(metersPerSecond) / KILOMETERS_IN_NAUTICAL_MILE;
   }

   public static double metersPerSecondToFeetPerMinute(double metersPerSecond)
   {
      return (metersPerSecond * SECONDS_IN_MINUTE) / METERS_IN_FOOT;
   }

   public static double metersToNauticalMiles(double meters)
   {
      return (meters / METERS_IN_KILOMETER) / KILOMETERS_IN_NAUTICAL_MILE;
   }

   public static double metersToFeet(double meters)
   {
      return meters / METERS_IN_FOOT;
   }
}
