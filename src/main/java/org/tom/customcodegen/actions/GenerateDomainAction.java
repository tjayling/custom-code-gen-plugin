package org.tom.customcodegen.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;
import org.tom.customcodegen.builder.ClassBuilder;
import org.tom.customcodegen.utils.InputHandler;
import org.tom.customcodegen.utils.InternalStringUtils;
import org.tom.customcodegen.utils.PackageUtils;

public class GenerateDomainAction extends AnAction {
  private Project project;
  private PsiDirectory directory;

  @Override
  public void actionPerformed(AnActionEvent e) {
    this.project = e.getRequiredData(CommonDataKeys.PROJECT);
    this.directory = (PsiDirectory) e.getRequiredData(CommonDataKeys.PSI_ELEMENT);

    InputHandler inputHandler = new InputHandler(project);

    var className = inputHandler.getClassName(directory,"Enter the domain name: ", "Domain Name");

    createController(className);
    createService(className);
    createRepositoryInterface(className);
    createRepository(className);
    createDomain(className);
  }

  @Override
  public void update(AnActionEvent e) {
    var selectedDirectory = e.getData(CommonDataKeys.PSI_ELEMENT);
    e.getPresentation().setEnabledAndVisible(PackageUtils.isValidPackage(selectedDirectory));
  }

  private void createController(String className) {
    var controllerBuilder = new ClassBuilder(project);

    var relativePackage = PackageUtils.getPackage(directory);
    var classNameLower = InternalStringUtils.lowercaseFirstLetter(className);

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
    controllerBuilder.newLine("private final %sService %sService;", className, classNameLower);
    controllerBuilder.blankLine();
    controllerBuilder.newLine("public %sController(%sService %sService) {", className, className, classNameLower);
    controllerBuilder.newLine("this.%sService = %sService;", classNameLower, classNameLower);
    controllerBuilder.closeCurly();
    controllerBuilder.closeCurly();

    controllerBuilder.buildAndOpenFile(className + "Controller", directory, "controller", 10, 10);
  }

  private void createService( String className) {
    var classNameLower = InternalStringUtils.lowercaseFirstLetter(className);

    var serviceName = className + "Service";
    var serviceBuilder = new ClassBuilder(project);

    serviceBuilder.startOfFile();
    serviceBuilder.slf4jImport();
    serviceBuilder.imports("org.springframework.stereotype.Service");
    serviceBuilder.blankLine();
    serviceBuilder.slf4jAnnotation();
    serviceBuilder.newLine("@Service");
    serviceBuilder.defineClass(serviceName);
    serviceBuilder.newLine("private final %sRepository %sRepository;", className, classNameLower);
    serviceBuilder.blankLine();
    serviceBuilder.newLine("public %sService(%sRepository %sRepository) {", className, className, classNameLower);
    serviceBuilder.newLine("this.%sRepository = %sRepository;", classNameLower, classNameLower);
    serviceBuilder.closeCurly();
    serviceBuilder.closeCurly();

    serviceBuilder.build(serviceName, directory, "service");
  }

  private void createRepositoryInterface(String className) {
    var repositoryName = className + "Repository";
    var repositoryInterfaceBuilder = new ClassBuilder(project);

    repositoryInterfaceBuilder.startOfFile();
    repositoryInterfaceBuilder.defineInterface(repositoryName);
    repositoryInterfaceBuilder.blankLine();
    repositoryInterfaceBuilder.closeCurly();

    repositoryInterfaceBuilder.build(repositoryName, directory, "service");
  }

  private void createRepository( String className) {
    var repositoryBuilder = new ClassBuilder(project);

    var relativePackage = PackageUtils.getPackage(directory);

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

  private void createDomain(String className) {
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
