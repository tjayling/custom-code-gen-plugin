package org.tom.customcodegen.validator;

import com.intellij.openapi.util.NlsSafe;
import org.apache.commons.lang.StringUtils;

public class ClassNameInputValidator extends StandardInputValidator {
  @Override
  public boolean checkInput(@NlsSafe String inputString) {
    return super.checkInput(inputString) && !Character.isDigit(inputString.charAt(0)) && StringUtils.isAlphanumeric(inputString);
  }

  @Override
  public boolean canClose(@NlsSafe String inputString) {
    return checkInput(inputString);
  }
}
