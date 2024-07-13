package org.ymmy.todo_chat.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.db.entity.TaskExample;
import org.ymmy.todo_chat.db.mapper.TaskCustomMapper;

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

  public Long insert(final Task task) {
    taskCustomMapper.insertSelective(task);
    return task.getId();
  }

}
