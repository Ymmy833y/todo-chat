package org.ymmy.todo_chat.db.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.db.entity.TaskExample;

@Mapper
public interface TaskMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    long countByExample(TaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    int deleteByExample(TaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    int insert(Task row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    int insertSelective(Task row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    List<Task> selectByExampleWithBLOBsWithRowbounds(TaskExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    List<Task> selectByExampleWithBLOBs(TaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    List<Task> selectByExampleWithRowbounds(TaskExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    List<Task> selectByExample(TaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    Task selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("row") Task row, @Param("example") TaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    int updateByExampleWithBLOBs(@Param("row") Task row, @Param("example") TaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    int updateByExample(@Param("row") Task row, @Param("example") TaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(Task row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    int updateByPrimaryKeyWithBLOBs(Task row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Task row);
}