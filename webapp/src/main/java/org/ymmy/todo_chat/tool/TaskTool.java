package org.ymmy.todo_chat.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.exception.NotFoundException;
import org.ymmy.todo_chat.logic.LoginUserLogic;
import org.ymmy.todo_chat.logic.TaskLogic;
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
      指定したタスクのステータスを指定したステータスに変更する
      """)
  String updateTaskStatus(
      @P("task Id") Long taskId,
      @P("status id") Long statusId
  ) {
    final var userId = loginUserLogic.getUserDetails().getUserId();
    final var dto = taskLogic.getTaskDto(taskId, userId);
    final var task = new Task() //
        .withId(dto.getTaskId()) //
        .withStatusId(statusId);
    taskRepository.update(task, userId);

    return String.format("「%s」を更新しました", dto.getTitle());
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
