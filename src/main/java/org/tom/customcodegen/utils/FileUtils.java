package org.tom.customcodegen.utils;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;

public class FileUtils {
  private FileUtils() {
  }

  public static PsiFile createFile(Project project, String className, String content) {
    PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);

    var fileName = className + ".java";
    return fileFactory.createFileFromText(fileName, FileTypes.PLAIN_TEXT, content);
  }

  public static void openFile(Project project, PsiFile createdFile, int line, int column) {
    FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, createdFile.getVirtualFile(), line, column), true);
  }
}
