package org.ymmy.todo_chat.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentCustomMapper extends CommentMapper {

  public void updateStatusByThreadIdAndStatus(@Param("threadId") final Long threadId,
      @Param("beforeStatus") final Long beforeStatus);
}