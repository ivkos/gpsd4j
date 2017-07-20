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

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class VersionMessage extends GpsdCommandMessage
{
   public static final String CLASS = "VERSION";

   private String release;
   private String rev;
   private Integer proto_major;
   private Integer proto_minor;

   @Override
   public String getGpsdClass()
   {
      return CLASS;
   }

   /**
    * @return Public release level
    */
   public String getRelease()
   {
      return release;
   }

   /**
    * @return Internal revision-control level
    */
   public String getRevision()
   {
      return rev;
   }

   /**
    * @return API major revision level
    */
   public Integer getProtocolMajor()
   {
      return proto_major;
   }

   /**
    * @return API minor revision level
    */
   public Integer getProtocolMinor()
   {
      return proto_minor;
   }
}
