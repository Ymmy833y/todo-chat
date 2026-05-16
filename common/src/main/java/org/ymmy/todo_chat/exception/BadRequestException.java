package org.ymmy.todo_chat.exception;

import org.ymmy.todo_chat.util.ErrorMessageEnum;

public class BadRequestException extends RuntimeException {

  public BadRequestException(ErrorMessageEnum errorMessageEnum) {
    super(errorMessageEnum.getMessage());
  }
}
