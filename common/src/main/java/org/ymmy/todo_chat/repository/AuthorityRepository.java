package org.ymmy.todo_chat.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.ymmy.todo_chat.db.entity.Authority;
import org.ymmy.todo_chat.db.entity.AuthorityExample;
import org.ymmy.todo_chat.db.mapper.AuthorityCustomMapper;

@Repository
@RequiredArgsConstructor
public class AuthorityRepository {

  private final AuthorityCustomMapper authorityCustomMapper;

  /**
   * ユーザーIDに紐づく権限情報を取得します
   *
   * @param userId ユーザーID
   * @return {@link Authority} のリスト
   */
  public List<Authority> selectByUserId(final Long userId) {
    final var example = new AuthorityExample();
    example.createCriteria().andUserIdEqualTo(userId);
    return authorityCustomMapper.selectByExample(example);
  }
}
