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
import org.ymmy.todo_chat.model.dto.TaskCreateDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateForm {

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

  public TaskCreateDto convertToDto() {
    return TaskCreateDto.builder() //
        .title(this.title) //
        .startDateTime(this.startDateTime) //
        .endDateTime(this.endDateTime) //
        .description(this.description) //
        .build();
  }
}
