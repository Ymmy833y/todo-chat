package org.ymmy.todo_chat.service;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ymmy.todo_chat.db.entity.Comment;
import org.ymmy.todo_chat.model.dto.CommentDto;
import org.ymmy.todo_chat.repository.CommentRepository;

@Service
public class LangChain4jService {

  private final Assistant assistant;

  private final CommentRepository commentRepository;

  public LangChain4jService(
      @Value("${openai.apikey}") final String apiKey, //
      @Value("${openai.model}") final String model,
      final CommentRepository commentRepository
  ) {

    this.commentRepository = commentRepository;

    final var chatLanguageModel = OpenAiChatModel.builder() //
        .apiKey(apiKey) //
        .modelName(model) //
        .build();
    this.assistant = AiServices.builder(Assistant.class) //
        .chatLanguageModel(chatLanguageModel) //
        .chatMemoryProvider(this::initializeChatMemory) //
        .build();
  }

  private MessageWindowChatMemory initializeChatMemory(final Object memoryId) {
    final var commentList = commentRepository.selectByThreadId((Long) memoryId);
    final var chatMemory = MessageWindowChatMemory.withMaxMessages(20);

    commentList.forEach(comment -> chatMemory.add(getChatMessage(comment)));

    return chatMemory;
  }

  /**
   * ChatGPTがコメントの返答を作成する
   *
   * @param commentDto {@link CommentDto}
   * @return 返答内容
   */
  public String getResponse(final CommentDto commentDto) {
    return assistant.chat(commentDto.getThreadId(), commentDto.getComment());
  }

  private ChatMessage getChatMessage(final Comment comment) {
    if (comment.getCreatedBy() == 0) {
      return AiMessage.aiMessage(comment.getComment());
    }
    return UserMessage.userMessage(comment.getComment());
  }
}
