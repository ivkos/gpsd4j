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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnitConverterTest
{
   private static final double DISTANCE = 1; // m
   private static final double SPEED = 1; // m/s
   private static final double DELTA = 1e-5;

   @Test
   public void metersPerSecondToKilometersPerHour() throws Exception
   {
      assertEquals(3.6, UnitConverter.metersPerSecondToKilometersPerHour(SPEED), DELTA);
   }

   @Test
   public void metersPerSecondToMilesPerHour() throws Exception
   {
      assertEquals(2.23693629, UnitConverter.metersPerSecondToMilesPerHour(SPEED), DELTA);
   }

   @Test
   public void metersPerSecondToKnots() throws Exception
   {
      assertEquals(1.94384449, UnitConverter.metersPerSecondToKnots(SPEED), DELTA);
   }

   @Test
   public void metersPerSecondToFeetPerMinute() throws Exception
   {
      assertEquals(196.850394, UnitConverter.metersPerSecondToFeetPerMinute(SPEED), DELTA);
   }

   @Test
   public void metersToNauticalMiles() throws Exception
   {
      assertEquals(0.000539956803, UnitConverter.metersToNauticalMiles(DISTANCE), DELTA);
   }

   @Test
   public void metersToFeet() throws Exception
   {
      assertEquals(3.2808399, UnitConverter.metersToFeet(DISTANCE), DELTA);
   }
}
