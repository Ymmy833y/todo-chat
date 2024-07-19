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
public class TaskListDto implements Serializable {

  private List<TaskDto> taskDtoList;
  private PaginationDto paginationDto;
  private List<TaskStatusDto> statusDtoList;
  private CommentDetailDto commentDetailDto;
  @Builder.Default
  private String currentUrl = "/task";

}
