package org.ymmy.todo_chat.db.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TaskStatus implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_status.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_status.name
     *
     * @mbg.generated
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_status.color_code
     *
     * @mbg.generated
     */
    private String colorCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_status.is_read_only
     *
     * @mbg.generated
     */
    private Boolean isReadOnly;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_status.created_at
     *
     * @mbg.generated
     */
    private LocalDateTime createdAt;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_status.created_by
     *
     * @mbg.generated
     */
    private Long createdBy;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_status.remarks
     *
     * @mbg.generated
     */
    private String remarks;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table task_status
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    public TaskStatus(Long id, String name, String colorCode, Boolean isReadOnly, LocalDateTime createdAt, Long createdBy) {
        this.id = id;
        this.name = name;
        this.colorCode = colorCode;
        this.isReadOnly = isReadOnly;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    public TaskStatus(Long id, String name, String colorCode, Boolean isReadOnly, LocalDateTime createdAt, Long createdBy, String remarks) {
        this.id = id;
        this.name = name;
        this.colorCode = colorCode;
        this.isReadOnly = isReadOnly;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.remarks = remarks;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    public TaskStatus() {
        super();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_status.id
     *
     * @return the value of task_status.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    public TaskStatus withId(Long id) {
        this.setId(id);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_status.id
     *
     * @param id the value for task_status.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_status.name
     *
     * @return the value of task_status.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    public TaskStatus withName(String name) {
        this.setName(name);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_status.name
     *
     * @param name the value for task_status.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_status.color_code
     *
     * @return the value of task_status.color_code
     *
     * @mbg.generated
     */
    public String getColorCode() {
        return colorCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    public TaskStatus withColorCode(String colorCode) {
        this.setColorCode(colorCode);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_status.color_code
     *
     * @param colorCode the value for task_status.color_code
     *
     * @mbg.generated
     */
    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_status.is_read_only
     *
     * @return the value of task_status.is_read_only
     *
     * @mbg.generated
     */
    public Boolean getIsReadOnly() {
        return isReadOnly;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    public TaskStatus withIsReadOnly(Boolean isReadOnly) {
        this.setIsReadOnly(isReadOnly);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_status.is_read_only
     *
     * @param isReadOnly the value for task_status.is_read_only
     *
     * @mbg.generated
     */
    public void setIsReadOnly(Boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_status.created_at
     *
     * @return the value of task_status.created_at
     *
     * @mbg.generated
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    public TaskStatus withCreatedAt(LocalDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_status.created_at
     *
     * @param createdAt the value for task_status.created_at
     *
     * @mbg.generated
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_status.created_by
     *
     * @return the value of task_status.created_by
     *
     * @mbg.generated
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    public TaskStatus withCreatedBy(Long createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_status.created_by
     *
     * @param createdBy the value for task_status.created_by
     *
     * @mbg.generated
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_status.remarks
     *
     * @return the value of task_status.remarks
     *
     * @mbg.generated
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_status
     *
     * @mbg.generated
     */
    public TaskStatus withRemarks(String remarks) {
        this.setRemarks(remarks);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_status.remarks
     *
     * @param remarks the value for task_status.remarks
     *
     * @mbg.generated
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}