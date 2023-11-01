package org.tom.customcodegen.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import io.netty.util.internal.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.tom.customcodegen.utils.FileUtils;
import org.tom.customcodegen.utils.PackageUtils;
import org.tom.customcodegen.utils.StringUtils;

public class GenerateDomainAction extends AnAction {
  @Override
  public void actionPerformed(AnActionEvent e) {
    var project = e.getRequiredData(CommonDataKeys.PROJECT);
    var selectedDirectory = (PsiDirectory) e.getRequiredData(CommonDataKeys.PSI_ELEMENT);
    var className = Messages.showInputDialog("Enter the class name for the domain: ", "Domain Class Name", Messages.getQuestionIcon());

    if (StringUtil.isNullOrEmpty(className)) {
      return;
    }

    className = StringUtils.capitaliseFirstLetter(className);

    String finalClassName = className;
    ApplicationManager.getApplication().invokeLater(() -> WriteCommandAction.runWriteCommandAction(project, () -> {
      createController(project, selectedDirectory, finalClassName);
      createService(project, selectedDirectory, finalClassName);
      createRepository(project, selectedDirectory, finalClassName);
      createDomain(project, selectedDirectory, finalClassName);
    }));
  }

  @Override
  public void update(AnActionEvent e) {
    var selectedDirectory = e.getData(CommonDataKeys.PSI_ELEMENT);
    e.getPresentation().setEnabledAndVisible(PackageUtils.isValidPackage(selectedDirectory));
  }

  private void createController(Project project, PsiDirectory directory, String className) {
    var relativePackage = PackageUtils.getRelativePackage(directory);
    var classNameLower = StringUtils.lowercaseFirstLetter(className);
    var fileContent = String.format("%n%nimport %s.service.%sService;%nimport lombok.extern.slf4j.Slf4j;%nimport org.springframework.web.bind.annotation.RequestMapping;%nimport org.springframework.web.bind.annotation.RestController;%n%n@Slf4j%n@RestController%n@RequestMapping(\"/api/\")%npublic class %sController {%n\tprivate final %sService %sService;%n%n\tpublic %sController(%sService %sService) {%n\t\tthis.%sService = %sService;%n\t}%n%n\t%n}", relativePackage, className, className, className, classNameLower, className, className, classNameLower, classNameLower, classNameLower);

    className += "Controller";

    var file = FileUtils.createFile(project, className, fileContent);



    var subDirectory = PackageUtils.getOrCreateSubdirectory(directory, "controller");

    var createdFile = subDirectory.add(file);
    FileUtils.openFile(project, (PsiFile) createdFile, 17, 6);
  }

  private void createService(Project project, PsiDirectory directory, String className) {
    var relativePackage = PackageUtils.getRelativePackage(directory);
    var classNameLower = StringUtils.lowercaseFirstLetter(className);

    var serviceContent = String.format("%n%nimport %s.service.%sRepository;%nimport lombok.extern.slf4j.Slf4j;%nimport org.springframework.stereotype.Service;%n%n@Slf4j%n@Service%npublic class %sService {%n\tprivate final %sRepository %sRepository;%n%n\tpublic %sService(%sRepository %sRepository) {%n\t\tthis.%sRepository = %sRepository;%n\t}%n}", relativePackage, className, className, className, classNameLower, className, className, classNameLower, classNameLower, classNameLower);
    var repositoryContent = String.format("package %s.service;%n%npublic interface %sRepository {%n%n}", relativePackage, className);

    String serviceName = className + "Service";
    String repositoryName = className + "Repository";

    var serviceFile = FileUtils.createFile(project, serviceName, serviceContent);
    var repositoryFile = FileUtils.createFile(project, repositoryName, repositoryContent);

    var subDirectory = PackageUtils.getOrCreateSubdirectory(directory, "service");

    subDirectory.add(serviceFile);
    subDirectory.add(repositoryFile);
  }

  private void createRepository(Project project, PsiDirectory directory, String className) {
    var relativePackage = PackageUtils.getRelativePackage(directory);
    var fileContent = String.format("%n%nimport %s.service.%sRepository;%nimport lombok.extern.slf4j.Slf4j;%nimport org.springframework.stereotype.Repository;%n%n@Slf4j%n@Repository%npublic class %sRepositoryImpl implements %sRepository {%n%n}", relativePackage, className, className, className);

    className += "RepositoryImpl";

    var file = FileUtils.createFile(project, className, fileContent);

    var subDirectory = PackageUtils.getOrCreateSubdirectory(directory, "repository");
    subDirectory.add(file);
  }

  private void createDomain(Project project, PsiDirectory directory, String className) {
    var relativePackage = PackageUtils.getRelativePackage(directory);
    var fileContent = String.format("package %s.model;%n%nimport lombok.Value;%n%n@Value%npublic class %s {%n%n}", relativePackage, className);

    var file = FileUtils.createFile(project, className, fileContent);

    var subDirectory = PackageUtils.getOrCreateSubdirectory(directory, "domain");
    subDirectory.add(file);
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return super.getActionUpdateThread();
  }
}
