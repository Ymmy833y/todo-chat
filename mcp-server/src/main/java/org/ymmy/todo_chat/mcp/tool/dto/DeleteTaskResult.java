package org.ymmy.todo_chat.mcp.tool.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 削除ツールの結果。Elicitation でユーザに拒否された場合は {@code deleted=false}・
 * {@code reason="user_declined"} を返す。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DeleteTaskResult(
    Long taskId,
    boolean deleted,
    String reason
) {

}
