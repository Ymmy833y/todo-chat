package org.ymmy.todo_chat.model.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ymmy.todo_chat.db.entity.TaskStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {

  private Long taskId;
  private Long statusId;
  private String title;
  private String description;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private Long createdBy;
  private TaskStatus taskStatus;

}
