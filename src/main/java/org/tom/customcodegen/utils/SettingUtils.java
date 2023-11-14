package org.tom.customcodegen.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;

public class SettingUtils {
  private SettingUtils() {
  }

  public static String getDefaultTabCharacter(Project project) {
    CodeStyleSettings settings = CodeStyleSettingsManager.getInstance(project).getMainProjectCodeStyle();

    assert settings != null;
    boolean useSoftTabs = !settings.getIndentOptions().USE_TAB_CHARACTER;
    int tabSize = settings.getTabSize(null);

    if (useSoftTabs) {
      return " ".repeat(tabSize);
    } else {
      return "\t";
    }
  }
}
