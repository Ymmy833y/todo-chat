package org.ymmy.todo_chat.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ymmy.todo_chat.exception.BadRequestException;
import org.ymmy.todo_chat.model.dto.LoginDto;
import org.ymmy.todo_chat.repository.UserRepository;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  /**
   * ユーザー情報が存在するか検証
   *
   * @param dto {@link LoginDto}
   * @return ユーザーID
   */
  public Long verifyUserExistence(final LoginDto dto) {
    final var optionalUser = userRepository.selectByUserName(dto.getUssrName());
    if (optionalUser.isEmpty()) {
      throw new BadRequestException(ErrorMessageEnum.NOT_FOUND_USER);
    }

    return optionalUser.get().getId();
  }

  /**
   * セッションに正しいuserIdが含まれているか検証
   *
   * @param session {@link HttpSession}
   */
  public void isAuthenticated(final HttpSession session) {
    final Long userId = (Long) session.getAttribute("userId");
    if (userId != null && userRepository.selectById(userId) != null) {
      return;
    }
    throw new BadRequestException(ErrorMessageEnum.CLEARED_SESSION);
  }
}
