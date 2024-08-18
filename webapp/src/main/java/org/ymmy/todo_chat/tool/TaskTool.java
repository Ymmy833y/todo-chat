package org.ymmy.todo_chat.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.exception.NotFoundException;
import org.ymmy.todo_chat.logic.LoginUserLogic;
import org.ymmy.todo_chat.logic.TaskLogic;
import org.ymmy.todo_chat.model.dto.TaskDto;
import org.ymmy.todo_chat.repository.TaskRepository;
import org.ymmy.todo_chat.repository.TaskStatusRepository;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

@Service
@AllArgsConstructor
public class TaskTool {

  private final DateTool dateTool;

  private final TaskRepository taskRepository;
  private final TaskStatusRepository taskStatusRepository;
  private final TaskLogic taskLogic;
  private final LoginUserLogic loginUserLogic;

  @Tool("""
      タイトル、開始日時、終了日時、説明をもとにタスクを新規作成する
      """)
  String createTask(
      @P("Task title") final String title,
      @P("Task start deadline") final String startDateTime,
      @P("Task deadline") final String endDataTime,
      @P("Task Description") final String description
  ) {
    final var userId = loginUserLogic.getUserDetails().getUserId();
    final var task = new Task() //
        .withTitle(title) //
        .withStatusId(1L) //
        .withStartDateTime(dateTool.convertToLocalDateTime(startDateTime)) //
        .withEndDateTime(dateTool.convertToLocalDateTime(endDataTime)) //
        .withDescription(description);

    taskRepository.insert(task, userId);

    return title;
  }

  @Tool("""
      指定したタスクの詳細情報を取得する
      """)
  TaskDto getTask(
      @P("task id") Long taskId
  ) {
    final var userId = loginUserLogic.getUserDetails().getUserId();
    return taskLogic.getTaskDto(taskId, userId);
  }

  @Tool("""
      指定したタスクを更新する
      """)
  String updateTask(
      @P("task id") Long taskId,
      @P(value = "Task title", required = false) final String title,
      @P(value = "status id", required = false) Long statusId,
      @P(value = "Task start deadline", required = false) final String startDateTime,
      @P(value = "Task deadline", required = false) final String endDataTime,
      @P(value = "Task Description", required = false) final String description
  ) {
    final var userId = loginUserLogic.getUserDetails().getUserId();
    final var dto = taskLogic.getTaskDto(taskId, userId);
    final var task = new Task() //
        .withId(dto.getTaskId()) //
        .withTitle(Optional.ofNullable(title).orElse(dto.getTitle())) //
        .withStatusId(Optional.ofNullable(statusId).orElse(dto.getTaskStatusDto().getStatusId())) //
        .withStartDateTime(Optional.ofNullable(startDateTime)
            .map(dateTool::convertToLocalDateTime)
            .orElse(dto.getStartDateTime())) //
        .withEndDateTime(Optional.ofNullable(endDataTime)
            .map(dateTool::convertToLocalDateTime)
            .orElse(dto.getEndDateTime())) //
        .withDescription(Optional.ofNullable(description).orElse(dto.getDescription()));

    taskRepository.update(task, userId);

    return String.format("「%s」を更新しました", dto.getTitle());
  }

  @Tool("""
      指定したタスクを削除する
      """)
  String removeTask(
      @P("task id") Long taskId
  ) {
    final var userId = loginUserLogic.getUserDetails().getUserId();
    final var dto = taskLogic.getTaskDto(taskId, userId);
    final var task = new Task() //
        .withId(dto.getTaskId());
    taskRepository.delete(task);

    return String.format("「%s」を削除しました", dto.getTitle());
  }

  @Tool("""
      タスクのタイトルからタスクIDを取得する
      """)
  Long getTaskIdForTitle(final String title) {
    final var userId = loginUserLogic.getUserDetails().getUserId();
    final var task = taskRepository.selectByNameAndUserId(title, userId)
        .orElseThrow(() -> new NotFoundException(ErrorMessageEnum.NON_EXISTENT_DATA));

    return task.getId();
  }

  @Tool("""
      ステータスの名前からタスクのステータスIDを取得する
      """)
  Long getTaskStatusIdForStatusName(final String statusName) {
    final var taskStatus = taskStatusRepository.selectByName(statusName)
        .orElseThrow(() -> new NotFoundException(ErrorMessageEnum.NON_EXISTENT_STATUS));

    return taskStatus.getId();
  }
}
