package org.ymmy.todo_chat.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentStatusEnum {

  USER_UNCONFIRMED(100L, "ユーザー未確認"),
  APP_UNCONFIRMED(200L, "アプリ未確認"),
  CONFIRMED(300L, "確認済み");

  private final Long code;
  private final String description;

}
