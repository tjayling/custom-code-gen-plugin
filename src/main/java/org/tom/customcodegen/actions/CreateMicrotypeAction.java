package org.tom.customcodegen.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import io.netty.util.internal.StringUtil;
import org.tom.customcodegen.utils.FileUtils;
import org.tom.customcodegen.utils.StringUtils;

public class CreateMicrotypeAction extends AnAction {
  @Override
  public void actionPerformed(AnActionEvent e) {
    var project = e.getRequiredData(CommonDataKeys.PROJECT);
    var selectedDirectory = (PsiDirectory) e.getRequiredData(CommonDataKeys.PSI_ELEMENT);

    var className = Messages.showInputDialog("Enter the class name for the microtype: ", "Microtype Name", Messages.getQuestionIcon());
    var classType = Messages.showInputDialog("Enter the encapsulated type: ", "Microtype Type", Messages.getQuestionIcon());

    if (StringUtil.isNullOrEmpty(className) || StringUtil.isNullOrEmpty(classType)) {
      return;
    }

    className = StringUtils.capitaliseFirstLetter(className);

    var noClassName = "No" + className;

    var typeContent = String.format("%n%nimport lombok.Value;%nimport lombok.experimental.NonFinal;%n%n@Value%n@NonFinal%npublic class %s {%n  %s value;%n%n  public %s(%s value) {%n    this.value = value;%n  }%n%n  public boolean isPresent() {%n    return true;%n  }%n}%n", className, classType, className, classType);
    var noTypeContent = String.format("%n%npublic class %s extends %s {%n  private static final %s INSTANCE = new %s();%n%n  private %s() {%n    super();%n  }%n%n  public static %s create() {%n    return INSTANCE;%n  }%n%n  @Override%n  public boolean isPresent() {%n    return false;%n  }%n}%n", noClassName, className, noClassName, noClassName, noClassName, noClassName);

    var typeFile = FileUtils.createFile(project, className, typeContent);
    var noTypeFile = FileUtils.createFile(project, noClassName, noTypeContent);

    ApplicationManager.getApplication().invokeLater(() -> WriteCommandAction.runWriteCommandAction(project, () -> {
      selectedDirectory.add(typeFile);
      var createdFile = selectedDirectory.add(noTypeFile);
      FileUtils.openFile(project, (PsiFile) createdFile, 6, 10);
    }));
  }
}
