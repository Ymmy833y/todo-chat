package org.ymmy.todo_chat.db.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.ymmy.todo_chat.model.dto.TaskSearchDto;
import org.ymmy.todo_chat.model.entity.TaskEntity;

public interface TaskCustomMapper extends TaskMapper {

  public List<TaskEntity> selectAllBySearchCriteria(
      @Param("searchDto") final TaskSearchDto searchDto);
}