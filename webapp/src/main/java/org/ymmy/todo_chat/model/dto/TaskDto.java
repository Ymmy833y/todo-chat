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
public class TaskDto implements Serializable {

  private Long taskId;
  private TaskStatusDto taskStatusDto;
  private String title;
  private String description;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private Long createdBy;

}
