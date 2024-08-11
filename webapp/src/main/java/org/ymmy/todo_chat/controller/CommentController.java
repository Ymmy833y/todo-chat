package org.ymmy.todo_chat.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.ymmy.todo_chat.exception.BadRequestException;
import org.ymmy.todo_chat.model.dto.CommentDto;
import org.ymmy.todo_chat.model.form.CommentConfirmedForm;
import org.ymmy.todo_chat.model.form.CommentForm;
import org.ymmy.todo_chat.service.CommentService;

@Controller
@AllArgsConstructor
@RequestMapping("/comment")
public class CommentController {

  private final CommentService commentService;

  /**
   * ユーザーからコメントを受け取り、返信コメントを返す
   *
   * @param commentForm ユーザーからのコメント
   */
  @PostMapping("/send")
  public ResponseEntity<CommentDto> send(@RequestBody final CommentForm commentForm) {

    try {
      final var replyCommentDto = commentService.getReplyComment(commentForm.convertToDto());
      return ResponseEntity.ok(replyCommentDto);
    } catch (final BadRequestException e) {
      return ResponseEntity.badRequest().body(null);
    }
  }

  /**
   * ユーザーのコメント確認を反映する
   *
   * @param commentConfirmedForm ユーザーからのコメント
   */
  @PostMapping("/confirmed")
  public ResponseEntity<String> confirmed(
      @RequestBody final CommentConfirmedForm commentConfirmedForm) {

    try {
      commentService.confirmedByUser(commentConfirmedForm.convertToDto());
      return ResponseEntity.ok("Confirmed");
    } catch (final BadRequestException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
