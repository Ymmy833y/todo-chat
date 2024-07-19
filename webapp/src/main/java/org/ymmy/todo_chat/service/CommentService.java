package org.ymmy.todo_chat.service;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.ymmy.todo_chat.logic.CommentLogic;
import org.ymmy.todo_chat.model.dto.CommentConfirmedDto;
import org.ymmy.todo_chat.model.dto.CommentDto;
import org.ymmy.todo_chat.repository.CommentRepository;
import org.ymmy.todo_chat.util.CommentStatusEnum;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final SimpMessagingTemplate messagingTemplate;

  private final SemanticKernelService semanticKernelService;

  private final CommentLogic commentLogic;
  private final CommentRepository commentRepository;

  /**
   * ユーザーから送信されたコメントをDBに登録します
   *
   * @param dto {@link CommentDto}
   * @return {@link CommentDto}
   */
  public CommentDto saveComment(final CommentDto dto) {
    final var comment = commentLogic.insertUserComment(dto.getComment(), dto.getThreadId());
    return new CommentDto(comment);
  }

  /**
   * ユーザーから送信されたコメントに返信します
   *
   * @param commentDto ユーザーのコメント
   * @return {@link CommentDto}
   */
  public CommentDto createReplyComment(final CommentDto commentDto) {
    final var replyText = semanticKernelService.getResponse(commentDto);
    final var replyComment = commentLogic.insertAppComment(commentDto.getThreadId(), replyText);
    return new CommentDto(replyComment);
  }

  /**
   * 指定したスレッドのユーザー確認待ちのコメントのステータスを全て確認済みにする
   *
   * @param dto {@link CommentConfirmedDto}
   */
  public void confirmedByUser(final CommentConfirmedDto dto) {
    commentRepository.updateStatusByThreadIdAndStatus(dto.getThreadId(),
        CommentStatusEnum.USER_UNCONFIRMED.getCode());
  }

  /**
   * アプリによって自動生成されたコメントを保存および送信します
   *
   * @param dto {@link CommentDto}
   */
  public void saveAndSendAppGeneratedComment(final CommentDto dto) {
    final var commentDto = commentLogic.insertAppComment(dto.getThreadId(), dto.getComment());
    messagingTemplate.convertAndSend("/message/comment", commentDto);
  }
}
