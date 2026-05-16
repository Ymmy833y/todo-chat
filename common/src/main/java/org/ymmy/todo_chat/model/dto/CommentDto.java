package org.ymmy.todo_chat.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ymmy.todo_chat.db.entity.Comment;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto implements Serializable {

  private Long id;
  private Long threadId;
  private String comment;
  private Long status;
  private LocalDateTime createdAt;
  private Long createdBy;

  public CommentDto(final Comment comment) {
    this.id = comment.getId();
    this.threadId = comment.getThreadId();
    this.comment = comment.getComment();
    this.status = comment.getStatus();
    this.createdAt = comment.getCreatedAt();
    this.createdBy = comment.getCreatedBy();
  }

}
