package org.ymmy.todo_chat.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentMessageEnum {

  CREATE_TASK("タスク「%s（%s~）」を新規作成しました。"),
  UPDATE_TASK("タスク「%s（%s~）」を更新しました。"),
  COMPLETE_TASK("タスク「%s（%s~）」を完了にしました。");

  private final String message;
}
