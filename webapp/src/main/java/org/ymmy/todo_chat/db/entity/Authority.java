package org.ymmy.todo_chat.db.entity;

import java.io.Serializable;

public class Authority implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column authority.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column authority.user_id
     *
     * @mbg.generated
     */
    private Long userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column authority.role
     *
     * @mbg.generated
     */
    private String role;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table authority
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table authority
     *
     * @mbg.generated
     */
    public Authority(Long id, Long userId, String role) {
        this.id = id;
        this.userId = userId;
        this.role = role;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table authority
     *
     * @mbg.generated
     */
    public Authority() {
        super();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column authority.id
     *
     * @return the value of authority.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table authority
     *
     * @mbg.generated
     */
    public Authority withId(Long id) {
        this.setId(id);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column authority.id
     *
     * @param id the value for authority.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column authority.user_id
     *
     * @return the value of authority.user_id
     *
     * @mbg.generated
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table authority
     *
     * @mbg.generated
     */
    public Authority withUserId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column authority.user_id
     *
     * @param userId the value for authority.user_id
     *
     * @mbg.generated
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column authority.role
     *
     * @return the value of authority.role
     *
     * @mbg.generated
     */
    public String getRole() {
        return role;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table authority
     *
     * @mbg.generated
     */
    public Authority withRole(String role) {
        this.setRole(role);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column authority.role
     *
     * @param role the value for authority.role
     *
     * @mbg.generated
     */
    public void setRole(String role) {
        this.role = role;
    }
}