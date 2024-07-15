package org.ymmy.todo_chat.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.ymmy.todo_chat.model.dto.TaskDto;
import org.ymmy.todo_chat.model.dto.TaskEditDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskEditForm {

  @NotNull
  private Long taskId;

  @NotNull(message = "入力必須の項目です。")
  private Long statusId;

  @NotBlank(message = "入力必須の項目です。")
  @Size(max = 50, message = "説明は50文字以内で入力してください。")
  private String title;

  @NotNull(message = "入力必須の項目です。")
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime startDateTime;

  @NotNull(message = "入力必須の項目です。")
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime endDateTime;

  private String description;

  public TaskEditForm(final TaskDto taskDto) {
    taskId = taskDto.getTaskId();
    statusId = taskDto.getTaskStatusDto().getStatusId();
    title = taskDto.getTitle();
    startDateTime = taskDto.getStartDateTime();
    endDateTime = taskDto.getEndDateTime();
    description = taskDto.getDescription();
  }

  public TaskEditDto convertToDto() {
    return TaskEditDto.builder() //
        .taskId(this.taskId) //
        .statusId(this.statusId) //
        .title(this.title) //
        .startDateTime(this.startDateTime) //
        .endDateTime(this.endDateTime) //
        .description(this.description) //
        .build();
  }
}
