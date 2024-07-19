package org.ymmy.todo_chat.controller;

import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.ymmy.todo_chat.model.form.CommentConfirmedForm;
import org.ymmy.todo_chat.model.form.CommentForm;
import org.ymmy.todo_chat.service.CommentService;

@Controller
@AllArgsConstructor
public class CommentController {

  private final SimpMessagingTemplate messagingTemplate;
  private final CommentService commentService;

  /**
   * ユーザーからコメントを受け取り、受け取ったコメントと返信コメントを画面に返す
   *
   * @param commentForm ユーザーからのコメント
   */
  @MessageMapping("/comment")
  public void handleComment(final CommentForm commentForm) {
    final var commentDto = commentService.saveComment(commentForm.convertToDto());
    messagingTemplate.convertAndSend("/message/comment", commentDto);

    final var replyCommentDto = commentService.createReplyComment(commentDto);
    messagingTemplate.convertAndSend("/message/comment", replyCommentDto);
  }

  /**
   * ユーザーのコメント確認を反映する
   *
   * @param commentConfirmedForm ユーザーからのコメント
   */
  @MessageMapping("/confirmed")
  public void handleConfirmed(final CommentConfirmedForm commentConfirmedForm) {
    commentService.confirmedByUser(commentConfirmedForm.convertToDto());
  }
}
