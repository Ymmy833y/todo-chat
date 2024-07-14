package org.ymmy.todo_chat.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.ymmy.todo_chat.exception.BadRequestException;
import org.ymmy.todo_chat.model.form.LoginForm;
import org.ymmy.todo_chat.service.UserService;

@Controller
@AllArgsConstructor
@RequestMapping("/login")
public class LoginController {

  private final UserService userService;

  /**
   * ログイン画面を表示します
   *
   * @param model   {@link Model}
   * @param session {@link HttpSession}
   * @return ログイン済みの場合はホーム画面、ログインしていない場合はログイン画面
   */
  @GetMapping("")
  public ModelAndView login(final Model model, final HttpSession session) {
    try {
      userService.isAuthenticated(session);
    } catch (BadRequestException e) {
      final var modelAndView = new ModelAndView("login");
      modelAndView.addObject("loginForm", new LoginForm());
      modelAndView.addAllObjects(model.asMap());

      return modelAndView;
    }

    return new ModelAndView("redirect:/home");
  }

  /**
   * ログインチェックをします
   *
   * @param loginForm          ログインフォーム
   * @param bindingResult      {@link BindingResult}
   * @param model              {@link Model}
   * @param redirectAttributes {@link RedirectAttributes}
   * @param request            {@link HttpServletRequest}
   * @return ログインチェックに成功した場合ホーム画面、失敗した場合はログイン画面
   */
  @PostMapping("")
  public ModelAndView login(@Valid @ModelAttribute final LoginForm loginForm,
      final BindingResult bindingResult, final Model model,
      final RedirectAttributes redirectAttributes, final HttpServletRequest request) {

    final var redirectModelAndView = new ModelAndView("redirect:/login");

    model.asMap().forEach(redirectAttributes::addFlashAttribute);
    if (bindingResult.hasErrors()) {
      return redirectModelAndView;
    }

    try {
      final var userId = userService.verifyUserExistence(loginForm.convertToDto());
      final var session = request.getSession();
      session.setAttribute("userId", userId);
      session.setMaxInactiveInterval(1800);
      return new ModelAndView("redirect:/home");
    } catch (BadRequestException e) {
      redirectAttributes.addFlashAttribute("exception", e.getMessage());
    }

    return redirectModelAndView;
  }
}