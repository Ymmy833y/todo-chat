package org.ymmy.todo_chat.service;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ymmy.todo_chat.db.entity.Comment;
import org.ymmy.todo_chat.model.dto.CommentDto;
import org.ymmy.todo_chat.repository.CommentRepository;

@Service
public class SemanticKernelService {

  private final Kernel kernel;
  private final ChatCompletionService chat;
  private final ChatHistory history;

  private final UserService userService;
  private final CommentRepository commentRepository;

  public SemanticKernelService( //
      @Value("${openai.endpoint}") final String endpoint, //
      @Value("${openai.apikey}") final String apiKey, //
      @Value("${openai.model}") final String model, //
      UserService userService,
      CommentRepository commentRepository
  ) {

    this.userService = userService;
    this.commentRepository = commentRepository;

    final var client = new OpenAIClientBuilder()
        .endpoint(endpoint)
        .credential(new AzureKeyCredential(apiKey))
        .buildAsyncClient();

    this.chat = ChatCompletionService.builder()
        .withModelId(model)
        .withOpenAIAsyncClient(client)
        .build();

    this.kernel = Kernel.builder()
        .withAIService(ChatCompletionService.class, chat)
        .build();

    this.history = new ChatHistory();
  }

  private void loadCommentsToHistory(final Long threadId) {
    final var comments = commentRepository.selectByThreadId(threadId);
    for (Comment comment : comments) {
      history.addUserMessage(comment.getComment());
    }
  }

  /**
   * ChatGPTがコメントの返答を作成する
   *
   * @param commentDto {@link CommentDto}
   * @return 返答内容
   */
  public String getResponse(final CommentDto commentDto) {
    loadCommentsToHistory(commentDto.getThreadId());

    history.addUserMessage(commentDto.getComment());

    final var invocationContext = InvocationContext.builder()
        .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
        .build();

    final var reply = chat.getChatMessageContentsAsync(history, kernel, invocationContext).block();
    final var message = Objects.requireNonNull(reply).get(reply.size() - 1).getContent();

    history.addAssistantMessage(message.toString());
    return message;
  }
}
