package org.ymmy.todo_chat.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.db.entity.TaskExample;
import org.ymmy.todo_chat.db.mapper.TaskCustomMapper;
import org.ymmy.todo_chat.model.dto.TaskSearchDto;
import org.ymmy.todo_chat.model.entity.TaskEntity;

@Repository
@RequiredArgsConstructor
public class TaskRepository {

  private final TaskCustomMapper taskCustomMapper;

  public List<Task> selectAll() {
    return taskCustomMapper.selectByExampleWithBLOBs(new TaskExample());
  }

  public Task selectById(final Long taskId) {
    return taskCustomMapper.selectByPrimaryKey(taskId);
  }

  /**
   * 検索条件に一致するタスク一覧を取得する
   *
   * @param taskSearchDto {@link TaskSearchDto} 検索条件
   * @return タスク一覧
   */
  public List<TaskEntity> selectAllBySearchCriteria(final TaskSearchDto taskSearchDto) {
    return taskCustomMapper.selectAllBySearchCriteria(taskSearchDto);
  }

  public Long insert(final Task task) {
    taskCustomMapper.insertSelective(task);
    return task.getId();
  }

}
