package org.ymmy.todo_chat.model.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDetailDto implements Serializable {

  private TaskDto taskDto;
  private List<TaskStatusDto> statusDtoList;
  private Map<String, String> detailMessages; // keyが警告色（danger、success）valueがメッセージ
  @Builder.Default
  private String currentUrl = "/task/detail";

}
