package org.ymmy.todo_chat.model.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomeDto implements Serializable {

  private List<TaskDto> todayTaskList;
  private List<TaskDto> dueInAWeekTaskList;
  @Builder.Default
  private String currentUrl = "/home";

}
