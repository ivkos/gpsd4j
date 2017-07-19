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

package com.ivkos.gpsd4j.support;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ivkos.gpsd4j.messages.GpsdMessage;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;

public class SerializationHelper
{
   private static final String JSON_CLASS_KEY = "class";
   private static final String GPSDMESSAGE_CLASS_FIELD_NAME = "CLASS";

   private static final Map<String, Class<? extends GpsdMessage>> gpsdClassNameToClassMap;

   static {
      Reflections reflections = new Reflections(GpsdMessage.class.getPackage().getName());
      Set<Class<? extends GpsdMessage>> objectClasses = reflections.getSubTypesOf(GpsdMessage.class);

      gpsdClassNameToClassMap = unmodifiableMap(objectClasses.stream().collect(toMap(
            SerializationHelper::getGpsdClassNameByClass,
            Function.identity()
      )));
   }

   // Support for deserialization into LocalDateTime
   static {
      JavaTimeModule javaTimeModule = new JavaTimeModule();
      Json.mapper.registerModule(javaTimeModule);
      Json.prettyMapper.registerModule(javaTimeModule);
   }

   /**
    * Deserializes a JSON string representing a message received from gpsd into a type-safe object representation,
    * subtype of {@link GpsdMessage}.
    *
    * @param json the JSON string
    *
    * @return the corresponding object, a subtype of {@link GpsdMessage}
    */
   @SuppressWarnings("unchecked")
   public static <T extends GpsdMessage> T deserialize(String json)
   {
      JsonObject obj;
      try {
         obj = new JsonObject(json);
      } catch (DecodeException e) {
         throw new GpsdParseException("Could not parse JSON", e);
      }

      String className = obj.getString(JSON_CLASS_KEY);
      if (className == null) throw new GpsdParseException("Could not parse JSON: missing '%s' key", JSON_CLASS_KEY);

      Class<T> clazz = (Class<T>) gpsdClassNameToClassMap.get(className);
      if (clazz == null) throw new GpsdParseException("Could not parse JSON: unknown class '%s'", className);

      try {
         return Json.decodeValue(json, clazz);
      } catch (DecodeException e) {
         throw new GpsdParseException("Could not parse JSON", e);
      }
   }

   /**
    * Serializes an object to a JSON string.
    *
    * @param obj the object
    *
    * @return a JSON string
    */
   public static String serialize(Object obj)
   {
      return Json.encode(obj);
   }

   /**
    * Returns the gpsd-specific class name for a given {@link GpsdMessage} type
    *
    * @param clazz the class
    *
    * @return the gpsd-specific class name
    */
   public static String getGpsdClassNameByClass(Class<? extends GpsdMessage> clazz)
   {
      String gpsdClassName;

      try {
         Field field = clazz.getDeclaredField(GPSDMESSAGE_CLASS_FIELD_NAME);

         gpsdClassName = Optional.ofNullable((String) field.get(null))
               .orElseThrow(() -> new RuntimeException("Field is null"));
      } catch (NoSuchFieldException | IllegalAccessException | RuntimeException e) {
         throw new RuntimeException(format(
               "Class %s must declare a 'public static final String %s = ...;' that is not null",
               clazz.getName(), GPSDMESSAGE_CLASS_FIELD_NAME
         ));
      }

      return gpsdClassName;
   }
}
