package org.ymmy.todo_chat.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthorityEnum {

  GENERAL("GENERAL", "一般"),
  ADMIN("ADMIN", "管理者");

  private final String code;
  private final String remarks;
}
