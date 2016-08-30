package org.rabix.bindings.sb.helper;

import java.util.Map;

public class SBBeanHelper {

  public static <T> T getValue(String key, Object raw) {
    return getValue(key, raw, null);
  }

  @SuppressWarnings("unchecked")
  public static <T> T getValue(String key, Object raw, T defaultValue) {
    if (raw == null) {
      return null;
    }
    if (raw instanceof Map<?, ?>) {
      T value = (T) ((Map<?, ?>) raw).get(key);
      if (value != null) {
        return value;
      }
    }
    return defaultValue;
  }

  @SuppressWarnings("unchecked")
  public static void setValue(String key, Object value, Object raw) {
    if (raw == null) {
      return;
    }
    if (raw instanceof Map<?, ?>) {
      ((Map<String, Object>) raw).put(key, value);
    }
  }
  
}
