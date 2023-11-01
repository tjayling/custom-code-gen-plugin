package org.tom.customcodegen.utils;

public class StringUtils {
  private StringUtils() {
  }

  public static String capitaliseFirstLetter(String s) {
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  public static String lowercaseFirstLetter(String s) {
    return s.substring(0, 1).toLowerCase() + s.substring(1);
  }

}
