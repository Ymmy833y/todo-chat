package org.ymmy.todo_chat.service;

import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.bge.small.en.v15.BgeSmallEnV15EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ymmy.todo_chat.config.DocumentConfig;
import org.ymmy.todo_chat.db.entity.Comment;
import org.ymmy.todo_chat.model.dto.CommentDto;
import org.ymmy.todo_chat.repository.CommentRepository;
import org.ymmy.todo_chat.tool.TaskTool;

@Service
public class LangChain4jService {

  private final Assistant assistant;

  private final CommentRepository commentRepository;

  public LangChain4jService(
      @Value("${openai.apikey}") final String apiKey, //
      @Value("${openai.model}") final String model,
      final DocumentConfig documentConfig,
      final CommentRepository commentRepository,
      final TaskTool taskTool
  ) {

    this.commentRepository = commentRepository;

    final var chatLanguageModel = OpenAiChatModel.builder() //
        .apiKey(apiKey) //
        .modelName(model) //
        .build();

    final var documentParser = new TextDocumentParser();
    final var document = FileSystemDocumentLoader.loadDocuments(documentConfig.getDocumentPath(),
        documentParser);

    final var splitter = DocumentSplitters.recursive(300, 0);
    final var segments = splitter.splitAll(document);

    final var embeddingModel = new BgeSmallEnV15EmbeddingModel();
    final var embeddings = embeddingModel.embedAll(segments).content();

    final var embeddingStore = new InMemoryEmbeddingStore<TextSegment>();
    embeddingStore.addAll(embeddings, segments);

    final var contentRetriever = EmbeddingStoreContentRetriever.builder() //
        .embeddingStore(embeddingStore) //
        .embeddingModel(embeddingModel) //
        .maxResults(2) //
        .minScore(0.5) //
        .build();

    this.assistant = AiServices.builder(Assistant.class) //
        .chatLanguageModel(chatLanguageModel) //
        .tools(taskTool) //
        .contentRetriever(contentRetriever) //
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
