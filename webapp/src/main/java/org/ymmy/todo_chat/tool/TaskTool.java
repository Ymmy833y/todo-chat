package org.ymmy.todo_chat.tool;

import dev.langchain4j.agent.tool.Tool;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.logic.TaskLogic;
import org.ymmy.todo_chat.repository.TaskRepository;

@Service
@AllArgsConstructor
public class TaskTool {

  private final TaskRepository taskRepository;
  private final TaskLogic taskLogic;

  @Tool("""
      Create a task with title
      """)
  String createTask(final String title, final String description) {

    final var task = new Task() //
        .withTitle(title) //
        .withStatusId(1L) //
        .withStartDateTime(LocalDateTime.now()) //
        .withEndDateTime(taskLogic.getEndDateTimeAfterDays(0)) //
        .withDescription(description);

    taskRepository.insert(task, 0L);

    return title;
  }

  @Tool("Get the current date and time")
  LocalDateTime getCurrentDateTime() {
    return LocalDateTime.now();
  }
}
