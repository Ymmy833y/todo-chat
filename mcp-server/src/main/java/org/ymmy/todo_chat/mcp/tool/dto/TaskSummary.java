package org.ymmy.todo_chat.mcp.tool.dto;

import java.time.LocalDateTime;

/**
 * 検索結果一覧で返却するタスク概要。
 */
public record TaskSummary(
    Long taskId,
    String title,
    Long statusId,
    String statusName,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime
) {

}
