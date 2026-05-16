package org.ymmy.todo_chat.mcp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MCP サーバ固有の設定値。
 * <p>
 * webapp のように HTTP セッションを持たないため、操作対象ユーザを設定で固定する。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "todo-chat.mcp")
public class McpProperties {

  /** 全 @Tool 実行時に使用するユーザーID。 */
  private Long userId;
}
