package org.tom.customcodegen.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import io.netty.util.internal.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.tom.customcodegen.builder.ClassBuilder;
import org.tom.customcodegen.utils.PackageUtils;
import org.tom.customcodegen.utils.StringUtils;

public class GenerateDomainAction extends AnAction {
  @Override
  public void actionPerformed(AnActionEvent e) {
    var project = e.getRequiredData(CommonDataKeys.PROJECT);
    var selectedDirectory = (PsiDirectory) e.getRequiredData(CommonDataKeys.PSI_ELEMENT);
    var className = Messages.showInputDialog("Enter the domain name: ", "Domain Name", Messages.getQuestionIcon());

    if (StringUtil.isNullOrEmpty(className)) {
      return;
    }

    className = StringUtils.capitaliseFirstLetter(className);

    String finalClassName = className;
    createController(project, selectedDirectory, finalClassName);
    createService(project, selectedDirectory, finalClassName);
    createRepositoryInterface(project, selectedDirectory, finalClassName);
    createRepository(project, selectedDirectory, finalClassName);
    createDomain(project, selectedDirectory, finalClassName);
  }

  @Override
  public void update(AnActionEvent e) {
    var selectedDirectory = e.getData(CommonDataKeys.PSI_ELEMENT);
    e.getPresentation().setEnabledAndVisible(PackageUtils.isValidPackage(selectedDirectory));
  }

  private void createController(Project project, PsiDirectory directory, String className) {
    var controllerBuilder = new ClassBuilder(project);

    var relativePackage = PackageUtils.getRelativePackage(directory);
    var classNameLower = StringUtils.lowercaseFirstLetter(className);

    controllerBuilder.startOfFile();
    controllerBuilder.imports("%s.service.%sService;", relativePackage, className);
    controllerBuilder.slf4jImport();
    controllerBuilder.imports("org.springframework.web.bind.annotation.RequestMapping");
    controllerBuilder.imports("org.springframework.web.bind.annotation.RestController");
    controllerBuilder.blankLine();
    controllerBuilder.slf4jAnnotation();
    controllerBuilder.newLine("@RestController");
    controllerBuilder.newLine("@RequestMapping(\"/api/\")");
    controllerBuilder.defineClass(className + "Controller");
    controllerBuilder.newLine("private final %sService %sService;", 1, className, classNameLower);
    controllerBuilder.blankLine();
    controllerBuilder.newLine("public %sController(%sService %sService) {", 1, className, className, classNameLower);
    controllerBuilder.newLine("this.%sService = %sService;", 2, classNameLower, classNameLower);
    controllerBuilder.closeCurly(1);
    controllerBuilder.closeCurly();

    controllerBuilder.buildAndOpenFile(className + "Controller", directory, "controller", 10, 10);
  }

  private void createService(Project project, PsiDirectory directory, String className) {
    var classNameLower = StringUtils.lowercaseFirstLetter(className);

    var serviceName = className + "Service";
    var serviceBuilder = new ClassBuilder(project);

    serviceBuilder.startOfFile();
    serviceBuilder.slf4jImport();
    serviceBuilder.imports("org.springframework.stereotype.Service");
    serviceBuilder.blankLine();
    serviceBuilder.slf4jAnnotation();
    serviceBuilder.newLine("@Service");
    serviceBuilder.defineClass(serviceName);
    serviceBuilder.newLine("private final %sRepository %sRepository;", 1, className, classNameLower);
    serviceBuilder.blankLine();
    serviceBuilder.newLine("public %sService(%sRepository %sRepository) {", 1, className, className, classNameLower);
    serviceBuilder.newLine("this.%sRepository = %sRepository;", 2, classNameLower, classNameLower);
    serviceBuilder.closeCurly(1);
    serviceBuilder.closeCurly();

    serviceBuilder.build(serviceName, directory, "service");
  }

  private void createRepositoryInterface(Project project, PsiDirectory directory, String className) {
    var repositoryName = className + "Repository";
    var repositoryInterfaceBuilder = new ClassBuilder(project);

    repositoryInterfaceBuilder.startOfFile();
    repositoryInterfaceBuilder.defineInterface(repositoryName);
    repositoryInterfaceBuilder.blankLine();
    repositoryInterfaceBuilder.closeCurly();

    repositoryInterfaceBuilder.build(repositoryName, directory, "service");
  }

  private void createRepository(Project project, PsiDirectory directory, String className) {
    var repositoryBuilder = new ClassBuilder(project);

    var relativePackage = PackageUtils.getRelativePackage(directory);

    repositoryBuilder.startOfFile();
    repositoryBuilder.imports("%s.service.%sRepository;", relativePackage, className);
    repositoryBuilder.slf4jImport();
    repositoryBuilder.imports("org.springframework.stereotype.Repository");
    repositoryBuilder.blankLine();
    repositoryBuilder.slf4jAnnotation();
    repositoryBuilder.newLine("@Repository");
    repositoryBuilder.defineClassImplements(className + "RepositoryImpl", className + "Repository");
    repositoryBuilder.blankLine();
    repositoryBuilder.closeCurly();

    repositoryBuilder.build(className + "RepositoryImpl", directory, "repository");

  }

  private void createDomain(Project project, PsiDirectory directory, String className) {
    var domainBuilder = new ClassBuilder(project);

    domainBuilder.startOfFile();
    domainBuilder.imports("lombok.Value");
    domainBuilder.blankLine();
    domainBuilder.newLine("@Value");
    domainBuilder.defineClass(className);
    domainBuilder.blankLine();
    domainBuilder.closeCurly();

    domainBuilder.build(className, directory, "domain");
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return super.getActionUpdateThread();
  }
}
