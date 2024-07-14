package org.ymmy.todo_chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.ymmy.todo_chat.db.entity.User;
import org.ymmy.todo_chat.exception.BadRequestException;
import org.ymmy.todo_chat.model.dto.LoginDto;
import org.ymmy.todo_chat.repository.UserRepository;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

  @InjectMocks
  private UserService userService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private HttpSession session;

  @Nested
  class VerifyUserExistence {

    final String USER_NAME = "USER_NAME";

    @Test
    void ユーザーが存在する場合ユーザーIDを返す() {
      final var loginDto = LoginDto.builder().ussrName(USER_NAME).build();

      doReturn(Optional.of(generateUser())).when(userRepository).selectByUserName(anyString());

      final var actual = userService.verifyUserExistence(loginDto);

      assertThat(actual).isEqualTo(1L);
      verify(userRepository, times(1)).selectByUserName(anyString());
    }

    @Test
    void ユーザーが存在しない場合BadRequestExceptionを返す() {
      final var loginDto = LoginDto.builder().ussrName(USER_NAME).build();

      doReturn(Optional.empty()).when(userRepository).selectByUserName(anyString());

      assertThatThrownBy(() -> userService.verifyUserExistence(loginDto)) //
          .isInstanceOf(BadRequestException.class) //
          .hasMessageContaining(ErrorMessageEnum.NOT_FOUND_USER.getMessage());
      verify(userRepository, times(1)).selectByUserName(anyString());
    }

    private User generateUser() {
      return new User(1L, USER_NAME, "DISPLAY_NAME", LocalDateTime.of(2024, 1, 1, 0, 0, 0), 0L, 1L);
    }
  }

  @Nested
  class IsAuthenticated {

    @Test
    void セッションにuserIdが含まれている場合は例外を返さない() {
      final Long userId = 1L;
      doReturn(userId).when(session).getAttribute("userId");
      doReturn(
          new User(userId, "USER_NAME", "DISPLAY_NAME", LocalDateTime.of(2024, 1, 1, 0, 0, 0), 0L,
              1L)).when(userRepository).selectById(userId);

      userService.isAuthenticated(session);
    }

    @Test
    void セッションにuserIdが含まれていない場合BadRequestExceptionを返す() {
      doReturn(null).when(session).getAttribute("userId");

      assertThatThrownBy(() -> userService.isAuthenticated(session))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining(ErrorMessageEnum.CLEARED_SESSION.getMessage());
    }

    @Test
    void ユーザーが存在しない場合BadRequestExceptionを返す() {
      final Long userId = 1L;
      doReturn(userId).when(session).getAttribute("userId");
      doReturn(null).when(userRepository).selectById(userId);

      assertThatThrownBy(() -> userService.isAuthenticated(session))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining(ErrorMessageEnum.CLEARED_SESSION.getMessage());
    }
  }

}
