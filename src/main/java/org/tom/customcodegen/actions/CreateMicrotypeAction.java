package org.tom.customcodegen.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import org.tom.customcodegen.builder.ClassBuilder;
import org.tom.customcodegen.utils.InputHandler;
import org.tom.customcodegen.utils.PackageUtils;

public class CreateMicrotypeAction extends AnAction {
  private Project project;
  private PsiDirectory directory;
  String packageName;

  @Override
  public void actionPerformed(AnActionEvent e) {
    this.project = e.getRequiredData(CommonDataKeys.PROJECT);
    this.directory = (PsiDirectory) e.getRequiredData(CommonDataKeys.PSI_ELEMENT);
    this.packageName = PackageUtils.getPackage(directory);

    InputHandler inputHandler = new InputHandler(project);

    var className = inputHandler.getClassName(directory, "Enter the microtype name: ", "Microtype Name");
    var classType = inputHandler.getClassType();

    createMicrotype(className, classType);
  }

  @Override
  public void update(AnActionEvent e) {
    var selectedDirectory = e.getData(CommonDataKeys.PSI_ELEMENT);
    e.getPresentation().setEnabledAndVisible(PackageUtils.isValidPackage(selectedDirectory));
  }

  private void createMicrotype(String className, String classType) {
    var microtypeBuilder = new ClassBuilder(project);

    populateMicrotype(microtypeBuilder, className, classType);

    microtypeBuilder.buildAndOpenFile(className, directory);
  }

  private void populateMicrotype(ClassBuilder microtypeBuilder, String className, String classType) {
    var classNameValue = className + "Value";
    var noClassName = "No" + className;

    microtypeBuilder.startOfFile();
    microtypeBuilder.imports(packageName + '.' + className + '.' + classNameValue);
    microtypeBuilder.imports(packageName + '.' + className + '.' + noClassName);
    microtypeBuilder.blankLine();
    microtypeBuilder.defineSealedInterface(className, classNameValue, noClassName);
    microtypeBuilder.newLine("static %s create(%s value) {", className, classType);
    microtypeBuilder.incrementIndent();
    microtypeBuilder.newLine("return value != null ? new %s(value) : new %s();", classNameValue, noClassName);
    microtypeBuilder.closeCurly();
    microtypeBuilder.blankLine();
    microtypeBuilder.newLine("boolean isPresent();");
    microtypeBuilder.blankLine();
    microtypeBuilder.defineRecord(classNameValue, classType, className);
    microtypeBuilder.newLine("@Override");
    microtypeBuilder.newLine("public boolean isPresent() {");
    microtypeBuilder.incrementIndent();
    microtypeBuilder.newLine("return true;");
    microtypeBuilder.closeCurly();
    microtypeBuilder.closeCurly();
    microtypeBuilder.blankLine();
    microtypeBuilder.defineRecordNoValue(noClassName, className);
    microtypeBuilder.newLine("@Override");
    microtypeBuilder.newLine("public boolean isPresent() {");
    microtypeBuilder.incrementIndent();
    microtypeBuilder.newLine("return false;");
    microtypeBuilder.closeCurly();
    microtypeBuilder.closeCurly();
    microtypeBuilder.closeCurly();
  }
}
