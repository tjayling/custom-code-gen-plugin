package org.tom.customcodegen.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import java.util.Arrays;
import java.util.Objects;
import org.tom.customcodegen.validator.ClassNameInputValidator;
import org.tom.customcodegen.validator.StandardInputValidator;

public class InputHandler {
  private final Project project;

  public InputHandler(Project project) {
    this.project = project;
  }

  public String getClassName(PsiDirectory directory, String message, String title) {
    var existingFiles = directory.getFiles();

    while (true) {
      var className = Objects.requireNonNull(Messages.showInputDialog(message, title, Messages.getQuestionIcon(), "", new ClassNameInputValidator()));

      if (Arrays.stream(existingFiles).anyMatch(f -> f.getName().equals(className + ".java"))) {
        Messages.showMessageDialog(project, "A class with that name already exists.", "Cannot Create Class", Messages.getErrorIcon());
      } else {
        return InternalStringUtils.capitaliseFirstLetter(className);
      }
    }
  }

  public String getClassType() {
    return Objects.requireNonNull(Messages.showInputDialog(project, "Enter class the type: ", "Microtype Type", Messages.getQuestionIcon(), "", new StandardInputValidator())).strip();
  }
}
