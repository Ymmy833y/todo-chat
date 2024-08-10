package org.ymmy.todo_chat.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.ymmy.todo_chat.config.CustomUserDetails;

@Component
@RequiredArgsConstructor
public class LoginUserLogic {

  /**
   * セッションからログインユーザー情報を取得する
   *
   * @return
   */
  public CustomUserDetails getUserDetails() {
    return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
  }
}
