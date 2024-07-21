package org.ymmy.todo_chat.controller.task;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.ymmy.todo_chat.exception.BadRequestException;
import org.ymmy.todo_chat.exception.InvalidCredentialsException;
import org.ymmy.todo_chat.model.form.TaskCompleteForm;
import org.ymmy.todo_chat.model.form.TaskEditForm;
import org.ymmy.todo_chat.service.TaskService;
import org.ymmy.todo_chat.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskDetailController {

  private final TaskService taskService;
  private final UserService userService;

  /**
   * タスク詳細画面を表示する
   *
   * @param taskId  タスクID
   * @param model   {@link Model}
   * @param session {@link HttpSession}
   * @return タスク詳細画面 / ログイン画面
   */
  @GetMapping("detail/{taskId}")
  public ModelAndView detail(@PathVariable("taskId") final Long taskId,
      final Model model, final HttpSession session) {
    final var modelAndView = new ModelAndView("task/detail");

    try {
      final var userId = userService.isAuthenticated(session);
      final var taskDetailDto = taskService.getTaskDetailDto(taskId, userId);
      modelAndView.addObject("taskDetailDto", taskDetailDto);
      modelAndView.addObject("taskCompleteForm", new TaskCompleteForm(taskDetailDto.getTaskDto()));
      modelAndView.addObject("taskEditForm", new TaskEditForm(taskDetailDto.getTaskDto()));
    } catch (InvalidCredentialsException e) {
      return new ModelAndView("redirect:/login");
    }

    modelAndView.addAllObjects(model.asMap()); // redirect時はここでtaskEditFormを上書き
    return modelAndView;
  }

  /**
   * タスクを更新する
   *
   * @param taskId             タスクID
   * @param taskEditForm       編集フォーム
   * @param bindingResult      {@link BindingResult}
   * @param redirectAttributes {@link RedirectAttributes}
   * @param model              {@link Model}
   * @param session            {@link HttpSession}
   * @return タスク詳細画面 / ログイン画面
   */
  @PostMapping("update/{taskID}")
  public ModelAndView update(@RequestParam("taskId") final Long taskId,
      @Valid @ModelAttribute final TaskEditForm taskEditForm,
      BindingResult bindingResult, final RedirectAttributes redirectAttributes, final Model model,
      final HttpSession session) {

    final var redirectModelAndView = new ModelAndView(
        String.format("redirect:/task/detail/%s", taskId));
    model.asMap().forEach(redirectAttributes::addFlashAttribute);
    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute("isSelectEdit", true);
      return redirectModelAndView;
    }

    try {
      final var userId = userService.isAuthenticated(session);
      taskService.update(taskEditForm.convertToDto(), userId);
      redirectAttributes.addFlashAttribute("isShowMessage", true);
    } catch (InvalidCredentialsException e) {
      return new ModelAndView("redirect:/login");
    } catch (BadRequestException e) {
      redirectAttributes.addFlashAttribute("exception", e.getMessage());
      redirectAttributes.addFlashAttribute("isSelectEdit", true);
    }
    return redirectModelAndView;
  }

  /**
   * タスクのステータスを完了にする
   *
   * @param taskId             タスクID
   * @param taskCompleteForm   {@link TaskCompleteForm}
   * @param redirectAttributes {@link RedirectAttributes}
   * @param session            {@link HttpSession}
   * @return タスク詳細画面 / ログイン画面
   */
  @PostMapping("complete/{taskID}")
  public ModelAndView complete(@RequestParam("taskId") final Long taskId,
      @ModelAttribute final TaskCompleteForm taskCompleteForm,
      final RedirectAttributes redirectAttributes, final HttpSession session) {

    try {
      final var userId = userService.isAuthenticated(session);
      taskService.updateStatus(taskCompleteForm.convertToDto(), userId);
      redirectAttributes.addFlashAttribute("isShowMessage", true);
    } catch (InvalidCredentialsException e) {
      return new ModelAndView("redirect:/login");
    } catch (BadRequestException e) {
      redirectAttributes.addFlashAttribute("exception", e.getMessage());
    }

    return new ModelAndView(String.format("redirect:/task/detail/%s", taskId));
  }

  /**
   * タスクを削除する
   *
   * @param taskId             タスクID
   * @param taskEditForm       {@link TaskEditForm}
   * @param redirectAttributes {@link RedirectAttributes}
   * @param session            {@link HttpSession}
   * @return タスク一覧画面 / ログイン画面
   */
  @PostMapping("delete/{taskID}")
  public ModelAndView delete(@RequestParam("taskId") final Long taskId,
      @ModelAttribute final TaskEditForm taskEditForm,
      final RedirectAttributes redirectAttributes, final HttpSession session) {

    try {
      final var userId = userService.isAuthenticated(session);
      taskService.delete(taskEditForm.convertToDto(), userId);
      redirectAttributes.addFlashAttribute("isShowMessage", true);
    } catch (InvalidCredentialsException e) {
      return new ModelAndView("redirect:/login");
    } catch (BadRequestException e) {
      redirectAttributes.addFlashAttribute("exception", e.getMessage());
    }

    return new ModelAndView("redirect:/task");
  }
}
