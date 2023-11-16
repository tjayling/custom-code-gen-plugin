package org.tom.customcodegen.validator;

import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.util.NlsSafe;
import org.tom.customcodegen.utils.InternalStringUtils;

public class StandardInputValidator implements InputValidator {
  @Override
  public boolean checkInput(@NlsSafe String inputString) {
    return InternalStringUtils.isNotBlankOrEmpty(inputString);
  }

  @Override
  public boolean canClose(@NlsSafe String inputString) {
    return checkInput(inputString);
  }
}
