package org.tom.customcodegen.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import org.tom.customcodegen.builder.ClassBuilder;
import org.tom.customcodegen.utils.InputHandler;

public class CreateMicrotypeAction extends AnAction {
  private Project project;
  private PsiDirectory directory;

  @Override
  public void actionPerformed(AnActionEvent e) {
    this.project = e.getRequiredData(CommonDataKeys.PROJECT);
    this.directory = (PsiDirectory) e.getRequiredData(CommonDataKeys.PSI_ELEMENT);
    InputHandler inputHandler = new InputHandler(project);

    var className = inputHandler.getClassName(directory, "Enter the microtype name: ", "Microtype Name");
    var classType = inputHandler.getClassType();

    createMicrotype(className, classType);
    createNoMicrotype(className, classType);
  }

  private void createMicrotype(String className, String classType) {
    var microtypeBuilder = new ClassBuilder(project);

    microtypeBuilder.startOfFile();
    microtypeBuilder.imports("lombok.Value");
    microtypeBuilder.imports("lombok.experimental.NonFinal");
    microtypeBuilder.blankLine();
    microtypeBuilder.newLine("@Value");
    microtypeBuilder.newLine("@NonFinal");
    microtypeBuilder.defineClass(className);
    microtypeBuilder.newLine("%s value;", 1, classType);
    microtypeBuilder.newLine("public %s(%s value) {", 1, className, classType);
    microtypeBuilder.newLine("this.value = value;", 2);
    microtypeBuilder.closeCurly(1);
    microtypeBuilder.blankLine();
    microtypeBuilder.newLine("public boolean isPresent() {", 1);
    microtypeBuilder.newLine("return true;", 2);
    microtypeBuilder.closeCurly(1);
    microtypeBuilder.closeCurly();

    microtypeBuilder.build(className, directory);
  }

  private void createNoMicrotype(String className, String classType) {
    var microtypeBuilder = new ClassBuilder(project);

    var superValue = getSuperValue(classType);

    var noClassName = "No" + className;

    microtypeBuilder.startOfFile();
    microtypeBuilder.imports("lombok.EqualsAndHashCode");
    microtypeBuilder.imports("lombok.Value");
    microtypeBuilder.blankLine();
    microtypeBuilder.newLine("@Value");
    microtypeBuilder.newLine("@EqualsAndHashCode(callSuper = true)");
    microtypeBuilder.defineClassExtends(noClassName, className);
    microtypeBuilder.newLine("private static final %s INSTANCE = new %s();", 1, noClassName, noClassName);
    microtypeBuilder.blankLine();
    microtypeBuilder.newLine("public %s() {", 1, noClassName, classType);
    microtypeBuilder.newLine("super(%s);", 2, superValue);
    microtypeBuilder.closeCurly(1);
    microtypeBuilder.blankLine();
    microtypeBuilder.newLine("public %s create() {", 1, noClassName);
    microtypeBuilder.newLine("return INSTANCE;", 2);
    microtypeBuilder.closeCurly(1);
    microtypeBuilder.blankLine();
    microtypeBuilder.newLine("@Override", 1);
    microtypeBuilder.newLine("public boolean isPresent() {", 1);
    microtypeBuilder.newLine("return false;", 2);
    microtypeBuilder.closeCurly(1);
    microtypeBuilder.closeCurly();

    microtypeBuilder.buildAndOpenFile(noClassName, directory, 9, 10);
  }

  private String getSuperValue(String classType) {
    return switch (classType.toLowerCase()) {
      case "string" -> "\"\"";
      case "integer", "int" -> "Integer.MIN_VALUE";
      case "long" -> "Long.MIN_VALUE";
      case "double" -> "Double.MIN_VALUE";
      default -> "/* Enter default value for super */";
    };
  }
}
