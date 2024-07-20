package org.ymmy.todo_chat.service;

import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ymmy.todo_chat.model.dto.CommentDto;

@Service
public class LangChain4jService {

  private final ChatLanguageModel chatLanguageModel;

  public LangChain4jService(
      @Value("${openai.apikey}") final String apiKey, //
      @Value("${openai.model}") final String model
  ) {
    this.chatLanguageModel = OpenAiChatModel.builder() //
        .apiKey(apiKey) //
        .modelName(model) //
        .build();
  }

  /**
   * ChatGPTがコメントの返答を作成する
   *
   * @param commentDto {@link CommentDto}
   * @return 返答内容
   */
  public String getResponse(final CommentDto commentDto) {
    final var reply = chatLanguageModel.generate( //
        UserMessage.from(TextContent.from(commentDto.getComment()))
    );

    return reply.content().text();
  }
}
