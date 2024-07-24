package org.ymmy.todo_chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DocumentConfig {

  @Value("${document.path}")
  private String documentPath;

  public String getDocumentPath() {
    return documentPath;
  }
}
