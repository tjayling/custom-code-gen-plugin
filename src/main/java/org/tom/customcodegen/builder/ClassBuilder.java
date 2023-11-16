package org.tom.customcodegen.builder;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.tom.customcodegen.utils.FileUtils;
import org.tom.customcodegen.utils.PackageUtils;
import org.tom.customcodegen.utils.SettingUtils;

public class ClassBuilder {
  private final StringBuilder stringBuilder;
  private final Project project;
  private final String tab;

  public ClassBuilder(Project project) {
    this.stringBuilder = new StringBuilder();
    this.project = project;
    this.tab = SettingUtils.getDefaultTabCharacter(project);
  }

  public void newLine(String string) {
    stringBuilder.append(string);
    stringBuilder.append(System.lineSeparator());
  }

  public void newLine(String string, int indentLevel) {
    stringBuilder.append(String.valueOf(tab).repeat(Math.max(0, indentLevel)));
    stringBuilder.append(string);
    stringBuilder.append(System.lineSeparator());
  }

  public void newLine(String string, int indentLevel, Object... args) {
    stringBuilder.append(String.valueOf(tab).repeat(Math.max(0, indentLevel)));
    stringBuilder.append(String.format(string, args));
    stringBuilder.append(System.lineSeparator());
  }

  public void blankLine() {
    stringBuilder.append(System.lineSeparator());
  }

  public void startOfFile() {
    blankLine();
    blankLine();
  }

  public void imports(String string, Object... args) {
    String imports = String.format(string, args);
    newLine("import %s;", 0, imports);
  }

  public void slf4jImport() {
    imports("lombok.extern.slf4j.Slf4j");
  }

  public void slf4jAnnotation() {
    newLine("@Slf4j");
  }

  public void defineClass(String className) {
    newLine("public class %s {", 0, className);
  }

  public void defineInterface(String interfaceName) {
    newLine("public interface %s {", 0, interfaceName);
  }

  public void defineClassImplements(String className, String implementsInterface) {
    newLine("public class %s implements %s {", 0, className, implementsInterface);
  }

  public void defineClassExtends(String className, String extendsClass) {
    newLine("public class %s extends %s {", 0, className, extendsClass);
  }

  public void closeCurly() {
    closeCurly(0);
  }

  public void closeCurly(int indentLevel) {
    newLine("}", indentLevel);
  }

  public void build(String className, PsiDirectory directory) {
    var file = getFile(className);
    ApplicationManager.getApplication().invokeLater(() -> WriteCommandAction.runWriteCommandAction(project, () -> {
      directory.add(file);
    }));
  }

  public void build(String className, PsiDirectory directory, String subDirectoryName) {
    var file = getFile(className);
    ApplicationManager.getApplication().invokeLater(() -> WriteCommandAction.runWriteCommandAction(project, () -> {
      var subDirectory = PackageUtils.getOrCreateSubdirectory(directory, subDirectoryName);
      subDirectory.add(file);
    }));
  }

  public PsiFile getFile(String fileName) {
    var fileContent = stringBuilder.toString();
    return FileUtils.createFile(project, fileName, fileContent);
  }

  public void buildAndOpenFile(String className, PsiDirectory directory) {
    var file = getFile(className);
    ApplicationManager.getApplication().invokeLater(() -> WriteCommandAction.runWriteCommandAction(project, () -> {
      var createdFile = (PsiFile) directory.add(file);
      FileUtils.openFile(project, createdFile);
    }));
  }

  public void buildAndOpenFile(String className, PsiDirectory directory, int line, int column) {
    var file = getFile(className);
    ApplicationManager.getApplication().invokeLater(() -> WriteCommandAction.runWriteCommandAction(project, () -> {
      var createdFile = (PsiFile) directory.add(file);
      FileUtils.openFile(project, createdFile, line, column);
    }));
  }

  public void buildAndOpenFile(String className, PsiDirectory directory, String subDirectoryName, int line, int column) {
    var file = getFile(className);
    ApplicationManager.getApplication().invokeLater(() -> WriteCommandAction.runWriteCommandAction(project, () -> {
      var subDirectory = PackageUtils.getOrCreateSubdirectory(directory, subDirectoryName);
      var createdFile = (PsiFile) subDirectory.add(file);
      FileUtils.openFile(project, createdFile, line, column);
    }));
  }

  @Override
  public String toString() {
    return stringBuilder.toString();
  }
}