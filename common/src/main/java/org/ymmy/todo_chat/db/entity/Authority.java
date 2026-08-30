package org.ymmy.todo_chat.db.entity;

import jakarta.annotation.Generated;
import java.io.Serializable;

public class Authority implements Serializable {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: authority.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: authority.user_id")
    private Long userId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: authority.role")
    private String role;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    private static final long serialVersionUID = 1L;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    public Authority(Long id, Long userId, String role) {
        this.id = id;
        this.userId = userId;
        this.role = role;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    public Authority() {
        super();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: authority.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    public Authority withId(Long id) {
        this.setId(id);
        return this;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: authority.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: authority.user_id")
    public Long getUserId() {
        return userId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    public Authority withUserId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: authority.user_id")
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: authority.role")
    public String getRole() {
        return role;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: authority")
    public Authority withRole(String role) {
        this.setRole(role);
        return this;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: authority.role")
    public void setRole(String role) {
        this.role = role;
    }
}