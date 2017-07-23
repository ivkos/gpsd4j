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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ivkos.gpsd4j.messages.GpsdMessage;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;

public class SerializationHelper
{
   private static final String JSON_CLASS_KEY = "class";
   private static final String GPSDMESSAGE_CLASS_FIELD_NAME = "CLASS";

   private static final Map<String, Class<? extends GpsdMessage>> gpsdClassNameToClassMap;
   private static final Map<Class<?>, List<Class<?>>> classToClassHierarchyListMap = new HashMap<>();

   static {
      Reflections reflections = new Reflections(GpsdMessage.class.getPackage().getName());
      Set<Class<? extends GpsdMessage>> objectClasses = reflections.getSubTypesOf(GpsdMessage.class);

      gpsdClassNameToClassMap = unmodifiableMap(objectClasses.stream()
            .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
            .collect(toMap(
                  SerializationHelper::getGpsdClassNameByClass,
                  Function.identity()
            )));
   }

   // Configure vertx's backing ObjectMapper
   static {
      // Support for deserialization into LocalDateTime
      JavaTimeModule javaTimeModule = new JavaTimeModule();

      for (ObjectMapper mapper : Arrays.asList(Json.mapper, Json.prettyMapper)) {
         mapper.registerModule(javaTimeModule);

         // use only fields for de/serialization
         mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
               .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
               .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
               .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
               .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
               .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
         );
      }
   }

   /**
    * Deserializes a JSON string representing a message received from gpsd into a type-safe object representation,
    * subtype of {@link GpsdMessage}.
    *
    * @param json the JSON string
    * @param <T>  the type of message
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

      if (obj.getMap() == null) {
         throw new GpsdParseException("Could not parse JSON: %s", json);
      }

      String className = obj.getString(JSON_CLASS_KEY);
      if (className == null) throw new GpsdParseException("Could not parse JSON: missing '%s' key in JSON: %s",
            JSON_CLASS_KEY, json);

      Class<T> clazz = (Class<T>) gpsdClassNameToClassMap.get(className);
      if (clazz == null) throw new GpsdParseException("Could not parse JSON: unknown class '%s' in JSON: %s",
            className, json);

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

   /**
    * Returns a list consisting of the class of the object and its superclasses, excluding {@link Object}
    *
    * @param obj the object
    *
    * @return a list of classes
    */
   public static List<Class<?>> getClassHierarchy(Object obj)
   {
      Class objClass = obj.getClass();

      List<Class<?>> classes = classToClassHierarchyListMap.get(objClass);
      if (classes != null) return classes;

      List<Class<?>> result = new LinkedList<>();

      while (objClass != null && objClass != Object.class) {
         result.add(objClass);
         objClass = objClass.getSuperclass();
      }

      classToClassHierarchyListMap.put(objClass, result);

      return result;
   }
}
