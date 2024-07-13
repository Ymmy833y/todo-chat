package org.ymmy.todo_chat.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ymmy.todo_chat.db.entity.Task;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskCreateDto implements Serializable {

  private String title;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String description;

}
