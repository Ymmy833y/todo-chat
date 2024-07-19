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
public class CommentDetailDto implements Serializable {

  private List<CommentDto> commentDtoList;
  private Long threadId;

}
