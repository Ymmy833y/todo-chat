package org.ymmy.todo_chat.model.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskCompleteDto implements Serializable {

  private Long taskId;
  private Long statusId;

}
