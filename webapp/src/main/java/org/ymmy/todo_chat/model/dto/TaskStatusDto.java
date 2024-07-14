package org.ymmy.todo_chat.model.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ymmy.todo_chat.db.entity.TaskStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskStatusDto implements Serializable {

  private Long statusId;
  private String name;
  private String colorCode;
  private boolean isReadOnly;
  private String remarks;

  public TaskStatusDto(final TaskStatus taskStatus) {
    statusId = taskStatus.getId();
    name = taskStatus.getName();
    colorCode = taskStatus.getColorCode();
    isReadOnly = taskStatus.getIsReadOnly();
    remarks = taskStatus.getRemarks();
  }
}
