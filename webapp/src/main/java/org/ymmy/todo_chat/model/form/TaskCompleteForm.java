package org.ymmy.todo_chat.model.form;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ymmy.todo_chat.model.dto.TaskCompleteDto;
import org.ymmy.todo_chat.model.dto.TaskDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskCompleteForm {

  @NotNull
  private Long taskId;
  @NotNull
  private Long statusId;

  public TaskCompleteForm(final TaskDto taskDto) {
    taskId = taskDto.getTaskId();
    statusId = taskDto.getTaskStatusDto().getStatusId();
  }

  public TaskCompleteDto convertToDto() {
    return TaskCompleteDto.builder() //
        .taskId(taskId) //
        .statusId(statusId)
        .build();
  }
}
