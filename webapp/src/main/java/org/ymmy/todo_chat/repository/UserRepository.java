package org.ymmy.todo_chat.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.ymmy.todo_chat.db.entity.User;
import org.ymmy.todo_chat.db.entity.UserExample;
import org.ymmy.todo_chat.db.mapper.UserCustomMapper;

@Repository
@RequiredArgsConstructor
public class UserRepository {

  private final UserCustomMapper userCustomMapper;

  public List<User> selectAll() {
    return userCustomMapper.selectByExample(new UserExample());
  }

  public User selectById(final Long userId) {
    return userCustomMapper.selectByPrimaryKey(userId);
  }

  /**
   * ユーザーネームを指定してuser情報を取得します
   *
   * @param userName ユーザーネーム
   * @return {@link User}
   */
  public Optional<User> selectByUserName(final String userName) {
    final var example = new UserExample();
    example.createCriteria().andNameEqualTo(userName);
    return userCustomMapper.selectByExample(example).stream().findFirst();
  }

}
