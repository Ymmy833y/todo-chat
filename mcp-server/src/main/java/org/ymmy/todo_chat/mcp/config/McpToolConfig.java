package org.ymmy.todo_chat.mcp.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ymmy.todo_chat.mcp.tool.TaskTools;

/**
 * {@link TaskTools} の @Tool メソッドを MCP サーバに公開する設定。
 */
@Configuration
public class McpToolConfig {

  @Bean
  public ToolCallbackProvider todoToolCallbackProvider(final TaskTools taskTools) {
    return MethodToolCallbackProvider.builder() //
        .toolObjects(taskTools) //
        .build();
  }
}
