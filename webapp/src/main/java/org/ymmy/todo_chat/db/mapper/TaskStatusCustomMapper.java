package org.ymmy.todo_chat.db.mapper;

import org.ymmy.todo_chat.db.entity.TaskStatus;

public interface TaskStatusCustomMapper extends TaskStatusMapper {

  TaskStatus selectByName(final String name);
}