package org.ymmy.todo_chat.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessageEnum {

  INVALID_PERIOD("開始日時は終了日時より後に設定できません。");

  private final String message;
}
