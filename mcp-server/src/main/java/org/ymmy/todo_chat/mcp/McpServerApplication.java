package org.ymmy.todo_chat.mcp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.ymmy.todo_chat.mcp.config.McpProperties;

@SpringBootApplication(scanBasePackages = "org.ymmy.todo_chat")
@MapperScan(basePackages = "org.ymmy.todo_chat.db")
@EnableConfigurationProperties(McpProperties.class)
public class McpServerApplication {

  public static void main(final String[] args) {
    SpringApplication.run(McpServerApplication.class, args);
  }
}
