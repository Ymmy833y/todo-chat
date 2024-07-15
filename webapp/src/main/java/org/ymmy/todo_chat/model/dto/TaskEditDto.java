package org.ymmy.todo_chat.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskEditDto implements Serializable {

  private Long taskId;
  private Long statusId;
  private String title;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String description;

}
