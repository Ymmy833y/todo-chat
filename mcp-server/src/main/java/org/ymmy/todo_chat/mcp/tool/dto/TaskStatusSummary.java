package org.ymmy.todo_chat.mcp.tool.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * タスクステータスの概要。{@code colorCode} と {@code isReadOnly} は {@code list_task_statuses}
 * ツールの引数で明示的に指定された場合にのみ値が設定され、それ以外は {@code null} として
 * Jackson から除外される。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskStatusSummary(
    Long statusId,
    String name,
    String remarks,
    String colorCode,
    Boolean isReadOnly
) {

}
