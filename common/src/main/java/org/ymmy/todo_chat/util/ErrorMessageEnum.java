package org.ymmy.todo_chat.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessageEnum {

  INVALID_PERIOD("開始日時は終了日時より後に設定できません。"),
  INVALID_COMMENT_THREAD("有効なコメントスレッドではありません。"),
  NON_EXISTENT_DATA("存在しないデータです。"),
  NON_EXISTENT_STATUS("存在しないステータスです。"),
  NOT_FOUND_USER("ユーザーが見つかりませんでした。");

  private final String message;
}
