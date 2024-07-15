package org.ymmy.todo_chat.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.ymmy.todo_chat.exception.InvalidCredentialsException;
import org.ymmy.todo_chat.service.HomeService;
import org.ymmy.todo_chat.service.UserService;

@Controller
@AllArgsConstructor
@RequestMapping("")
public class HomeController {

  private final HomeService homeService;
  private final UserService userService;

  /**
   * ホーム画面を表示します
   *
   * @param model   {@link Model}
   * @param session {@link HttpSession}
   * @return ホーム画面 / ログイン画面
   */
  @GetMapping("/home")
  public ModelAndView home(final Model model, final HttpSession session) {
    final var modelAndView = new ModelAndView("home");

    try {
      final var userId = userService.isAuthenticated(session);
      final var homeDto = homeService.getHomeDto(userId);
      modelAndView.addObject("homeDto", homeDto);
    } catch (InvalidCredentialsException e) {
      return new ModelAndView("redirect:/login");
    }

    modelAndView.addAllObjects(model.asMap());
    return modelAndView;
  }
}