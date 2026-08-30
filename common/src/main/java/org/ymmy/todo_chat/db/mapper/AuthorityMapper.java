package org.ymmy.todo_chat.db.mapper;

import jakarta.annotation.Generated;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.ymmy.todo_chat.db.entity.Authority;
import org.ymmy.todo_chat.db.entity.AuthorityExample;

@Mapper
public interface AuthorityMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    long countByExample(AuthorityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    int deleteByExample(AuthorityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    int insert(Authority row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    int insertSelective(Authority row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    List<Authority> selectByExampleWithRowbounds(AuthorityExample example, RowBounds rowBounds);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    List<Authority> selectByExample(AuthorityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    Authority selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    int updateByExampleSelective(@Param("row") Authority row, @Param("example") AuthorityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    int updateByExample(@Param("row") Authority row, @Param("example") AuthorityExample example);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    int updateByPrimaryKeySelective(Authority row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    int updateByPrimaryKey(Authority row);
}