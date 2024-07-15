package org.ymmy.todo_chat.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessageEnum {

  INVALID_PERIOD("開始日時は終了日時より後に設定できません。"),
  CLEARED_SESSION("セッション情報が無効または破棄されました。"),
  NON_EXISTENT_DATA("存在しないデータです。"),
  NOT_FOUND_USER("ユーザーが見つかりませんでした。");

  private final String message;
}
