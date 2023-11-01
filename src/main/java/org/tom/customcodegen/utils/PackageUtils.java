package org.tom.customcodegen.utils;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import java.util.Arrays;

public class PackageUtils {
  private PackageUtils() {
  }

  public static boolean isValidPackage(PsiElement psiElement) {
    if (psiElement instanceof PsiDirectory psiDirectory) {
      return getRelativePackage(psiDirectory) != null;
    }
    return false;
  }

  public static String getRelativePackage(PsiDirectory psiDirectory) {
    boolean isInSrcMainJava = psiDirectory.toString().contains("src/main/java/");
    boolean isInValidPackage = false;
    String packageLocation = null;
    if (isInSrcMainJava) {
      packageLocation = psiDirectory.toString().split("src/main/java/")[1];
      isInValidPackage = packageLocation.chars().mapToObj(c -> (char) c).filter(c -> c == '/').count() >= 2;
    }
    return isInSrcMainJava && isInValidPackage ? packageLocation.replace('/', '.') : null;
  }

  public static PsiDirectory getOrCreateSubdirectory(PsiDirectory parentDirectory, String subdirectoryName) {
    return Arrays.stream(parentDirectory.getSubdirectories())
        .filter(d -> d.getName().equals(subdirectoryName))
        .findFirst()
        .orElseGet(() -> parentDirectory.createSubdirectory(subdirectoryName));
  }
}
