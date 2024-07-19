package org.ymmy.todo_chat.model.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ymmy.todo_chat.model.dto.CommentDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentForm {

  private Long threadId;
  private String comment;

  public CommentDto convertToDto() {
    return CommentDto.builder() //
        .threadId(threadId) //
        .comment(comment) //
        .build();
  }
}
