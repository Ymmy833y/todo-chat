package org.ymmy.todo_chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.ymmy.todo_chat.config.CustomUserDetails;

@Controller
@RequiredArgsConstructor
public class LoginController {

  /**
   * ログイン画面を表示します
   *
   * @return ログイン画面
   */
  @GetMapping("/")
  public String index() {
    return "redirect:/login";
  }

  /**
   * ログイン画面を表示します
   *
   * @param authentication {@link Authentication}
   * @return ログイン済みの場合はホーム画面、ログインしていない場合はログイン画面
   */
  @GetMapping("/login")
  public String login(final Authentication authentication) {
    if (authentication != null) {
      final var userDetails = (CustomUserDetails) authentication.getPrincipal();
      if (userDetails.getUserId() != null) {
        return "redirect:/home";
      }
    }
    return "/login";
  }
}