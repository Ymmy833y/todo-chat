package org.ymmy.todo_chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ymmy.todo_chat.util.ViewUtil;

@Configuration
public class ThymeleafConfig {

  @Bean
  public ViewUtil viewUtil() {
    return new ViewUtil();
  }
}
