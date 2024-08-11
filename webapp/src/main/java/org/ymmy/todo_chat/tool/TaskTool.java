package org.ymmy.todo_chat.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.logic.LoginUserLogic;
import org.ymmy.todo_chat.repository.TaskRepository;

@Service
@AllArgsConstructor
public class TaskTool {

  private final DateTool dateTool;
  private final TaskRepository taskRepository;
  private final LoginUserLogic loginUserLogic;

  @Tool("""
      Create a new task
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
}
