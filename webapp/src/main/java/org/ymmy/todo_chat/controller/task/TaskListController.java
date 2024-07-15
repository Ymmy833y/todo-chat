package org.ymmy.todo_chat.controller.task;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.ymmy.todo_chat.exception.InvalidCredentialsException;
import org.ymmy.todo_chat.model.form.TaskSearchForm;
import org.ymmy.todo_chat.service.TaskService;
import org.ymmy.todo_chat.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskListController {

  private final TaskService taskService;
  private final UserService userService;

  /**
   * タスク一覧画面を表示する
   *
   * @param model   {@link Model}
   * @param session {@link HttpSession}
   * @return タスク一覧画面 / ログイン画面
   */
  @GetMapping("")
  public ModelAndView index(final Model model, final HttpSession session) {
    final var modelAndView = new ModelAndView("task/index");

    try {
      final var userId = userService.isAuthenticated(session);
      final var taskSearchForm = new TaskSearchForm();
      final var taskListDto = taskService.getTaskListDto(userId, taskSearchForm);
      modelAndView.addObject("taskListDto", taskListDto);
      modelAndView.addObject("taskSearchForm", taskSearchForm);
    } catch (InvalidCredentialsException e) {
      return new ModelAndView("redirect:/login");
    }

    modelAndView.addAllObjects(model.asMap());
    return modelAndView;
  }

  /**
   * タスク一覧の検索を行う
   *
   * @param taskSearchForm     検索Form {@link TaskSearchForm}
   * @param redirectAttributes {@link RedirectAttributes}
   * @param session            {@link HttpSession}
   * @return タスク一覧画面 / ログイン画面
   */
  @PostMapping("/search")
  public ModelAndView search(final TaskSearchForm taskSearchForm,
      final RedirectAttributes redirectAttributes, final HttpSession session) {
    final var modelAndView = new ModelAndView("redirect:/task");

    try {
      final var userId = userService.isAuthenticated(session);
      final var taskListDto = taskService.getTaskListDto(userId, taskSearchForm);
      redirectAttributes.addFlashAttribute("taskListDto", taskListDto);
      redirectAttributes.addFlashAttribute("taskSearchForm", new TaskSearchForm(taskSearchForm));
    } catch (InvalidCredentialsException e) {
      return new ModelAndView("redirect:/login");
    }

    return modelAndView;
  }

}
