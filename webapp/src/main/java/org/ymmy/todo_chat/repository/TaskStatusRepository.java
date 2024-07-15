package org.ymmy.todo_chat.repository;

import java.util.List;
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

}
