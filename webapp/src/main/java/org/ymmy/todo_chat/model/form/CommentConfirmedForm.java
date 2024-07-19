package org.ymmy.todo_chat.model.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ymmy.todo_chat.model.dto.CommentConfirmedDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentConfirmedForm {

  private Long threadId;

  public CommentConfirmedDto convertToDto() {
    return CommentConfirmedDto.builder() //
        .threadId(threadId) //
        .build();
  }
}
