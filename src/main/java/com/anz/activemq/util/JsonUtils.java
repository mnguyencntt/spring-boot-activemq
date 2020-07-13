package com.anz.activemq.util;

import java.io.IOException;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtils {
  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
  }

  public static String toJson(Object object) {
    return toJson(object, mapper);
  }

  public static <T> T toObject(String json, Class<T> clazz) {
    return toObject(json, clazz, mapper);
  }

  public static <T> T toObject(String json, TypeReference<T> typeReference) {
    return toObject(json, typeReference, mapper);
  }

  public static <T> T convertValue(Map<Object, Object> linkedHashMap, Class<T> clazz) {
    return convertValue(linkedHashMap, clazz, mapper);
  }

  public static <T> T convertValue(Map<Object, Object> linkedHashMap, TypeReference<T> typeReference) {
    return convertValue(linkedHashMap, typeReference, mapper);
  }

  private static String toJson(Object object, ObjectMapper mapper) {
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private static <T> T toObject(String json, Class<T> clazz, ObjectMapper mapper) {
    try {
      return mapper.readValue(json, clazz);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private static <T> T toObject(String json, TypeReference<T> typeReference, ObjectMapper mapper) {
    try {
      return mapper.readValue(json, typeReference);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private static <T> T convertValue(Map<Object, Object> linkedHashMap, Class<T> clazz, ObjectMapper mapper) {
    try {
      return mapper.convertValue(linkedHashMap, clazz);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private static <T> T convertValue(Map<Object, Object> linkedHashMap, TypeReference<T> typeReference, ObjectMapper mapper) {
    try {
      return mapper.convertValue(linkedHashMap, typeReference);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
