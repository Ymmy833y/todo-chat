package org.ymmy.todo_chat.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Assistant {

  @SystemMessage("You are a polite assistant in the Todo × chat app")
  String chat(@MemoryId final Long memoryId, @UserMessage final String userMessage);
}
