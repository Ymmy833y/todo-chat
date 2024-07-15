package org.ymmy.todo_chat.exception;

import org.ymmy.todo_chat.util.ErrorMessageEnum;

public class InvalidCredentialsException extends RuntimeException {

  public InvalidCredentialsException(ErrorMessageEnum errorMessageEnum) {
    super(errorMessageEnum.getMessage());
  }
}
