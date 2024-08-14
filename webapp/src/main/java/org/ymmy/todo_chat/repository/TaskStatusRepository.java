package org.ymmy.todo_chat.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.ymmy.todo_chat.db.entity.TaskStatus;
import org.ymmy.todo_chat.db.entity.TaskStatusExample;
import org.ymmy.todo_chat.db.mapper.TaskStatusCustomMapper;

@Repository
@RequiredArgsConstructor
public class TaskStatusRepository {

  private final TaskStatusCustomMapper taskStatusCustomMapper;

  public List<TaskStatus> selectAll() {
    return taskStatusCustomMapper.selectByExampleWithBLOBs(new TaskStatusExample());
  }

  /**
   * ステータス名に部分一致するステータスを取得する
   *
   * @param name ステータス名
   * @return {@link TaskStatus} のOptional
   */
  public Optional<TaskStatus> selectByName(final String name) {
    return Optional.ofNullable(taskStatusCustomMapper.selectByName(name));
  }
}
