package org.tom.customcodegen.utils;

import org.apache.commons.lang.StringUtils;

public class InternalStringUtils {
  private InternalStringUtils() {
  }

  public static String capitaliseFirstLetter(String s) {
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  public static String lowercaseFirstLetter(String s) {
    return s.substring(0, 1).toLowerCase() + s.substring(1);
  }

  public static boolean isBlankOrEmpty(String s) {
    return StringUtils.isBlank(s) || StringUtils.isEmpty(s);
  }

  public static boolean isNotBlankOrEmpty(String s) {
    return !(StringUtils.isBlank(s) || StringUtils.isEmpty(s));
  }

}
