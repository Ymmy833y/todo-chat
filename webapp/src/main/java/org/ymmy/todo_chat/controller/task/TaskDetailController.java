package org.ymmy.todo_chat.controller.task;


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
import org.ymmy.todo_chat.model.form.TaskCompleteForm;
import org.ymmy.todo_chat.model.form.TaskEditForm;
import org.ymmy.todo_chat.service.TaskService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskDetailController {

  private final TaskService taskService;

  /**
   * タスク詳細画面を表示する
   *
   * @param taskId タスクID
   * @param model  {@link Model}
   * @return タスク詳細画面 / ログイン画面
   */
  @GetMapping("detail/{taskId}")
  public ModelAndView detail(@PathVariable("taskId") final Long taskId,
      final Model model) {
    final var modelAndView = new ModelAndView("task/detail");

    final var taskDetailDto = taskService.getTaskDetailDto(taskId);
    modelAndView.addObject("taskDetailDto", taskDetailDto);
    modelAndView.addObject("taskCompleteForm", new TaskCompleteForm(taskDetailDto.getTaskDto()));
    modelAndView.addObject("taskEditForm", new TaskEditForm(taskDetailDto.getTaskDto()));

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
   * @return タスク詳細画面 / ログイン画面
   */
  @PostMapping("update/{taskID}")
  public ModelAndView update(@RequestParam("taskId") final Long taskId,
      @Valid @ModelAttribute final TaskEditForm taskEditForm,
      BindingResult bindingResult, final RedirectAttributes redirectAttributes, final Model model) {

    final var redirectModelAndView = new ModelAndView(
        String.format("redirect:/task/detail/%s", taskId));
    model.asMap().forEach(redirectAttributes::addFlashAttribute);
    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute("isSelectEdit", true);
      return redirectModelAndView;
    }

    try {
      taskService.update(taskEditForm.convertToDto());
      redirectAttributes.addFlashAttribute("isShowMessage", true);
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
   * @return タスク詳細画面 / ログイン画面
   */
  @PostMapping("complete/{taskID}")
  public ModelAndView complete(@RequestParam("taskId") final Long taskId,
      @ModelAttribute final TaskCompleteForm taskCompleteForm,
      final RedirectAttributes redirectAttributes) {

    try {
      taskService.updateStatus(taskCompleteForm.convertToDto());
      redirectAttributes.addFlashAttribute("isShowMessage", true);
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
   * @return タスク一覧画面 / ログイン画面
   */
  @PostMapping("delete/{taskID}")
  public ModelAndView delete(@RequestParam("taskId") final Long taskId,
      @ModelAttribute final TaskEditForm taskEditForm,
      final RedirectAttributes redirectAttributes) {

    try {
      taskService.delete(taskEditForm.convertToDto());
      redirectAttributes.addFlashAttribute("isShowMessage", true);
    } catch (BadRequestException e) {
      redirectAttributes.addFlashAttribute("exception", e.getMessage());
    }

    return new ModelAndView("redirect:/task");
  }
}
