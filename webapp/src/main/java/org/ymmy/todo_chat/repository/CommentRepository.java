package org.ymmy.todo_chat.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.ymmy.todo_chat.db.entity.Comment;
import org.ymmy.todo_chat.db.entity.CommentExample;
import org.ymmy.todo_chat.db.mapper.CommentCustomMapper;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

  private final CommentCustomMapper commentCustomMapper;

  public List<Comment> selectAll() {
    return commentCustomMapper.selectByExampleWithBLOBs(new CommentExample());
  }

  /**
   * threadIdが一致するコメントリストを取得します。
   *
   * @param threadId スレッドID
   * @return {@link Comment} のリスト
   */
  public List<Comment> selectByThreadId(final Long threadId) {
    final var example = new CommentExample();
    example.createCriteria().andThreadIdEqualTo(threadId);
    return commentCustomMapper.selectByExampleWithBLOBs(example);
  }

  public Comment insert(final Comment comment, final Long createdBy) {
    comment.setCreatedBy(createdBy);
    commentCustomMapper.insertSelective(comment);
    return comment;
  }

  /**
   * 指定したスレッドIDと変更前ステータスが一致するコメントのステータスを確認済み300にする
   *
   * @param threadId     スレッドID
   * @param beforeStatus 更新対象となるステータス
   */
  public void updateStatusByThreadIdAndStatus(final Long threadId, final Long beforeStatus) {
    commentCustomMapper.updateStatusByThreadIdAndStatus(threadId, beforeStatus);
  }
}
