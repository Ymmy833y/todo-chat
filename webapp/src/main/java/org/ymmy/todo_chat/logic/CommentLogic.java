package org.ymmy.todo_chat.logic;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.ymmy.todo_chat.db.entity.Comment;
import org.ymmy.todo_chat.model.dto.CommentDetailDto;
import org.ymmy.todo_chat.model.dto.CommentDto;
import org.ymmy.todo_chat.model.form.CommentForm;
import org.ymmy.todo_chat.repository.CommentRepository;
import org.ymmy.todo_chat.util.CommentStatusEnum;

@Component
@RequiredArgsConstructor
public class CommentLogic {

  private final CommentRepository commentRepository;

  public CommentDetailDto getCommentDetailDto(final Long threadId) {
    final var commentList = commentRepository.selectByThreadId(threadId);
    final var commentDtoList = commentList.stream().map(CommentDto::new).toList();
    final var commentForm = new CommentForm();
    commentForm.setThreadId(threadId);

    return CommentDetailDto.builder() //
        .commentDtoList(commentDtoList) //
        .threadId(threadId) //
        .build();
  }

  /**
   * App側で作成されたコメントを登録します。
   *
   * @param threadId    スレッドID
   * @param commentText コメント
   * @return commentId
   */
  public Comment insertAppComment(final Long threadId, final String commentText) {
    final var comment = new Comment() //
        .withThreadId(threadId) //
        .withComment(commentText) //
        .withStatus(CommentStatusEnum.USER_UNCONFIRMED.getCode()) //
        .withCreatedAt(LocalDateTime.now());
    // Commentを戻り値とする都合。CURRENT_TIMESTAMPではなくここで作成日時を設定する

    return commentRepository.insert(comment, 0L);
  }

  /**
   * User側で作成されたコメントを登録します
   *
   * @param userId      ユーザーID
   * @param commentText コメント
   * @return commentId
   */
  public Comment insertUserComment(final Long userId, final String commentText) {
    final var comment = new Comment() //
        .withThreadId(userId) //
        .withComment(commentText) //
        .withStatus(CommentStatusEnum.APP_UNCONFIRMED.getCode()) //
        .withCreatedAt(LocalDateTime.now());
    // Commentを戻り値とする都合。CURRENT_TIMESTAMPではなくここで作成日時を設定する

    return commentRepository.insert(comment, userId);
  }
}

