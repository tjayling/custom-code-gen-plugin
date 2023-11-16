package org.tom.customcodegen.utils;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.project.Project;

public class SettingUtils {
  private SettingUtils() {
  }

  public static String getDefaultTabCharacter(Project project) {
    var useTabCharacter = CodeStyle.getSettings(project).getIndentOptions(FileUtils.getJavaFileType()).USE_TAB_CHARACTER;
    return useTabCharacter ? "\t" : " ".repeat(CodeStyle.getSettings(project).getIndentOptions(FileUtils.getJavaFileType()).TAB_SIZE);
  }
}
