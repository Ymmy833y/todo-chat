package org.ymmy.todo_chat.mcp.tool.dto;

import java.time.LocalDateTime;

/**
 * タスク詳細を返却する DTO。
 */
public record TaskDetail(
    Long taskId,
    String title,
    String description,
    Long statusId,
    String statusName,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime
) {

}
