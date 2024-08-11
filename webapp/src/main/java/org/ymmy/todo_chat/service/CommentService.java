package org.ymmy.todo_chat.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ymmy.todo_chat.exception.BadRequestException;
import org.ymmy.todo_chat.logic.CommentLogic;
import org.ymmy.todo_chat.logic.LoginUserLogic;
import org.ymmy.todo_chat.model.dto.CommentConfirmedDto;
import org.ymmy.todo_chat.model.dto.CommentDto;
import org.ymmy.todo_chat.repository.CommentRepository;
import org.ymmy.todo_chat.util.CommentStatusEnum;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final SemanticKernelService semanticKernelService;
  private final CommentLogic commentLogic;
  private final LoginUserLogic loginUserLogic;
  private final CommentRepository commentRepository;

  /**
   * ユーザーから送信されたコメントに返信します
   *
   * @param dto {@link CommentDto}
   * @return 返信コメント {@link CommentDto}
   */
  public CommentDto getReplyComment(final CommentDto dto) {
    verifyUserLinkedToThreadId(dto.getThreadId());

    final var userComment = commentLogic.insertUserComment(dto.getThreadId(), dto.getComment());
    return createReplyComment(new CommentDto(userComment));
  }

  /**
   * 返信コメントを作成します
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
    verifyUserLinkedToThreadId(dto.getThreadId());

    commentRepository.updateStatusByThreadIdAndStatus(dto.getThreadId(),
        CommentStatusEnum.USER_UNCONFIRMED.getCode());
  }

  /**
   * アプリによって自動生成されたコメントを保存します
   *
   * @param dto {@link CommentDto}
   */
  public void saveAndSendAppGeneratedComment(final CommentDto dto) {
    commentLogic.insertAppComment(dto.getThreadId(), dto.getComment());
  }

  /**
   * ログインユーザーとコメントスレッドが紐づいているか検証します
   *
   * @param threadId スレッドID
   */
  private void verifyUserLinkedToThreadId(final Long threadId) {
    final var userId = loginUserLogic.getUserDetails().getUserId();
    if (!threadId.equals(userId)) {
      throw new BadRequestException(ErrorMessageEnum.INVALID_COMMENT_THREAD);
    }
  }
}
