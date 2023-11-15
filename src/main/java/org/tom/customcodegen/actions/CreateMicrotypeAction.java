package org.tom.customcodegen.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import io.netty.util.internal.StringUtil;
import org.tom.customcodegen.builder.ClassBuilder;
import org.tom.customcodegen.utils.StringUtils;

public class CreateMicrotypeAction extends AnAction {
  @Override
  public void actionPerformed(AnActionEvent e) {
    var project = e.getRequiredData(CommonDataKeys.PROJECT);
    var selectedDirectory = (PsiDirectory) e.getRequiredData(CommonDataKeys.PSI_ELEMENT);

    var className = Messages.showInputDialog("Enter the microtype name: ", "Microtype Name", Messages.getQuestionIcon());
    var classType = Messages.showInputDialog("Enter the type: ", "Microtype Type", Messages.getQuestionIcon());

    if (StringUtil.isNullOrEmpty(className) || StringUtil.isNullOrEmpty(classType)) {
      return;
    }

    className = StringUtils.capitaliseFirstLetter(className);

    createMicrotype(project, selectedDirectory, className, classType);
    createNoMicrotype(project, selectedDirectory, className, classType);
  }

  private void createMicrotype(Project project, PsiDirectory directory, String className, String classType) {
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

  private void createNoMicrotype(Project project, PsiDirectory directory, String className, String classType) {
    var microtypeBuilder = new ClassBuilder(project);

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
    microtypeBuilder.newLine("super();", 2);
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

}
