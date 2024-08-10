package org.ymmy.todo_chat.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.ymmy.todo_chat.service.HomeService;

@Controller
@AllArgsConstructor
@RequestMapping("")
public class HomeController {

  private final HomeService homeService;

  /**
   * ホーム画面を表示します
   *
   * @param model {@link Model}
   * @return ホーム画面 / ログイン画面
   */
  @GetMapping("/home")
  public ModelAndView home(final Model model) {
    final var modelAndView = new ModelAndView("home");

    final var homeDto = homeService.getHomeDto();
    modelAndView.addObject("homeDto", homeDto);

    modelAndView.addAllObjects(model.asMap());
    return modelAndView;
  }
}