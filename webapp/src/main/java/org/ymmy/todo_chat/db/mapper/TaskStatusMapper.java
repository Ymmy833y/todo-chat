package org.ymmy.todo_chat.db.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.ymmy.todo_chat.db.entity.TaskStatus;
import org.ymmy.todo_chat.db.entity.TaskStatusExample;

@Mapper
public interface TaskStatusMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    long countByExample(TaskStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    int deleteByExample(TaskStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    int insert(TaskStatus row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    int insertSelective(TaskStatus row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    List<TaskStatus> selectByExampleWithBLOBsWithRowbounds(TaskStatusExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    List<TaskStatus> selectByExampleWithBLOBs(TaskStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    List<TaskStatus> selectByExampleWithRowbounds(TaskStatusExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    List<TaskStatus> selectByExample(TaskStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    TaskStatus selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("row") TaskStatus row, @Param("example") TaskStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    int updateByExampleWithBLOBs(@Param("row") TaskStatus row, @Param("example") TaskStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    int updateByExample(@Param("row") TaskStatus row, @Param("example") TaskStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(TaskStatus row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    int updateByPrimaryKeyWithBLOBs(TaskStatus row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(TaskStatus row);
}