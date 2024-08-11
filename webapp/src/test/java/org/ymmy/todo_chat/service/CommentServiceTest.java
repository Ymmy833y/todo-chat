package org.ymmy.todo_chat.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.ymmy.todo_chat.config.CustomUserDetails;
import org.ymmy.todo_chat.db.entity.Comment;
import org.ymmy.todo_chat.exception.BadRequestException;
import org.ymmy.todo_chat.logic.CommentLogic;
import org.ymmy.todo_chat.logic.LoginUserLogic;
import org.ymmy.todo_chat.model.dto.CommentConfirmedDto;
import org.ymmy.todo_chat.model.dto.CommentDto;
import org.ymmy.todo_chat.repository.CommentRepository;
import org.ymmy.todo_chat.util.CommentStatusEnum;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

@SpringBootTest
@ActiveProfiles("test")
public class CommentServiceTest {

  final Long USER_ID = 1L;

  final String USER_COMMENT = "USER_COMMENT";
  final String APP_COMMENT = "APP_COMMENT";

  @Nested
  class GetReplyComment {

    @Test
    void ユーザーのコメントをDBに登録した後返信コメントを作成できる() {
      final var threadID = 1L;
      final var commentDto = generateCommentDto(threadID, USER_COMMENT);

      doReturn(generateCustomUserDetails()).when(loginUserLogic).getUserDetails();
      doReturn(generateComment(threadID, USER_COMMENT, CommentStatusEnum.APP_UNCONFIRMED)).when(
              commentLogic)
          .insertUserComment(anyLong(), anyString());
      doReturn(generateComment(threadID, APP_COMMENT, CommentStatusEnum.USER_UNCONFIRMED)).when(
              commentLogic)
          .insertAppComment(anyLong(), anyString());

      commentService.getReplyComment(commentDto);

      verify(commentLogic, times(1)).insertUserComment(anyLong(), anyString());
      verify(commentLogic, times(1)).insertAppComment(anyLong(), anyString());
    }

    @Test
    void ログインユーザーとスレッドIDが紐づいていない場合BadRequestExceptionを返す() {
      final var threadID = 2L;

      final var commentDto = generateCommentDto(threadID, USER_COMMENT);

      doReturn(generateCustomUserDetails()).when(loginUserLogic).getUserDetails();

      assertThatThrownBy(() -> commentService.getReplyComment(commentDto)) //
          .isInstanceOf(BadRequestException.class) //
          .hasMessageContaining(ErrorMessageEnum.INVALID_COMMENT_THREAD.getMessage());

      verify(commentLogic, never()).insertUserComment(anyLong(), anyString());
      verify(commentLogic, never()).insertAppComment(anyLong(), anyString());
    }
  }

  @Nested
  class ConfirmedByUser {

    @Test
    void 指定したスレッドのユーザー確認待ちのコメントのステータスを全て確認済みにできる() {
      final var threadID = 1L;
      final var commentConfirmedDto = generateCommentConfirmedDto(threadID);

      doReturn(generateCustomUserDetails()).when(loginUserLogic).getUserDetails();
      doNothing().when(commentRepository).updateStatusByThreadIdAndStatus(anyLong(), anyLong());

      commentService.confirmedByUser(commentConfirmedDto);

      verify(commentRepository, times(1)).updateStatusByThreadIdAndStatus(anyLong(), anyLong());
    }

    @Test
    void ログインユーザーとスレッドIDが紐づいていない場合BadRequestExceptionを返す() {
      final var threadID = 2L;
      final var commentConfirmedDto = generateCommentConfirmedDto(threadID);

      doReturn(generateCustomUserDetails()).when(loginUserLogic).getUserDetails();

      assertThatThrownBy(() -> commentService.confirmedByUser(commentConfirmedDto)) //
          .isInstanceOf(BadRequestException.class) //
          .hasMessageContaining(ErrorMessageEnum.INVALID_COMMENT_THREAD.getMessage());

      verify(commentRepository, never()).updateStatusByThreadIdAndStatus(anyLong(), anyLong());
    }
  }

  private CustomUserDetails generateCustomUserDetails() {
    return new CustomUserDetails(USER_ID, "USERNAME", "PASSWORD", Collections.emptyList());
  }

  private CommentDto generateCommentDto(final Long threadId, final String comment) {
    return CommentDto.builder() //
        .threadId(threadId) //
        .comment(comment) //
        .build();
  }

  private CommentConfirmedDto generateCommentConfirmedDto(final Long threadId) {
    return CommentConfirmedDto.builder() //
        .threadId(threadId) //
        .build();
  }

  private Comment generateComment(final Long threadId, final String comment, final
  CommentStatusEnum status) {
    return new Comment()
        .withThreadId(threadId) //
        .withComment(comment) //
        .withStatus(status.getCode());
  }

  @InjectMocks
  private CommentService commentService;
  @Mock
  private CommentLogic commentLogic;
  @Mock
  private LoginUserLogic loginUserLogic;
  @Mock
  private CommentRepository commentRepository;
}
