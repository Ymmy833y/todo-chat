package org.ymmy.todo_chat.controller.task;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.ymmy.todo_chat.exception.InvalidCredentialsException;
import org.ymmy.todo_chat.model.form.TaskCreateForm;
import org.ymmy.todo_chat.service.TaskService;
import org.ymmy.todo_chat.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskCreateController {

  private final TaskService taskService;
  private final UserService userService;

  /**
   * タスクの新規作成画面を表示する
   *
   * @param model   {@link Model}
   * @param session {@link HttpSession}
   * @return 新規作成画面 / ログイン画面
   */
  @GetMapping("add")
  public ModelAndView add(final Model model, final HttpSession session) {
    final var modelAndView = new ModelAndView("task/add");

    try {
      userService.isAuthenticated(session);
    } catch (InvalidCredentialsException e) {
      return new ModelAndView("redirect:/login");
    }

    modelAndView.addObject("taskCreateDetailDto", taskService.getTaskCreateDetailDto());
    modelAndView.addObject("taskCreateForm", new TaskCreateForm());
    modelAndView.addAllObjects(model.asMap());

    return modelAndView;
  }

  /**
   * タスクを登録する
   *
   * @param taskCreateForm     タスクの作成Form
   * @param bindingResult      {@link BindingResult}
   * @param redirectAttributes {@link RedirectAttributes}
   * @param model              {@link Model}
   * @param session            {@link HttpSession}
   * @return 新規作成画面 / 詳細画面 / ログイン画面
   */
  @PostMapping("create")
  public ModelAndView create(@Valid @ModelAttribute final TaskCreateForm taskCreateForm,
      BindingResult bindingResult, final RedirectAttributes redirectAttributes, final Model model,
      final HttpSession session) {

    final var redirectModelAndView = new ModelAndView("redirect:/task/add");
    model.asMap().forEach(redirectAttributes::addFlashAttribute);
    if (bindingResult.hasErrors()) {
      return redirectModelAndView;
    }

    try {
      final var userId = userService.isAuthenticated(session);
      final var taskId = taskService.create(taskCreateForm.convertToDto(), userId);
      return new ModelAndView(String.format("redirect:/task/detail/%s", taskId));
    } catch (InvalidCredentialsException e) {
      return new ModelAndView("redirect:/login");
    } catch (BadRequestException e) {
      redirectAttributes.addFlashAttribute("exception", e.getMessage());
    }
    return redirectModelAndView;
  }
}
