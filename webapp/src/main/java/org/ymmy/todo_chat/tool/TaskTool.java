package org.ymmy.todo_chat.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.logic.LoginUserLogic;
import org.ymmy.todo_chat.logic.TaskLogic;
import org.ymmy.todo_chat.repository.TaskRepository;

@Service
@AllArgsConstructor
public class TaskTool {

  private final TaskRepository taskRepository;
  private final TaskLogic taskLogic;
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
        .withStartDateTime(convertToLocalDateTime(startDateTime)) //
        .withEndDateTime(convertToLocalDateTime(endDataTime)) //
        .withDescription(description);

    taskRepository.insert(task, userId);

    return title;
  }

  @Tool("Get the current date and time")
  String getCurrentDateTime() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    return LocalDateTime.now().format(formatter);
  }

  private LocalDateTime convertToLocalDateTime(final String localDateTime) {
    try {
      return LocalDateTime.parse(localDateTime);
    } catch (DateTimeParseException e) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
      return LocalDateTime.parse(localDateTime, formatter);
    }
  }
}
