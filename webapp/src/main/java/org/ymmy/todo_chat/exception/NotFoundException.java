package org.ymmy.todo_chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

  public NotFoundException(ErrorMessageEnum errorMessageEnum) {
    super(errorMessageEnum.getMessage());
  }
}
