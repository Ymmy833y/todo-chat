package org.ymmy.todo_chat.service;

import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import java.net.http.HttpClient;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ymmy.todo_chat.config.DocumentConfig;
import org.ymmy.todo_chat.db.entity.Comment;
import org.ymmy.todo_chat.model.dto.CommentDto;
import org.ymmy.todo_chat.repository.CommentRepository;
import org.ymmy.todo_chat.tool.DateTool;
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
    final TaskTool taskTool,
    final DateTool dateTool
  ) {

    this.commentRepository = commentRepository;

    HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();

    JdkHttpClientBuilder jdkHttpClientBuilder = JdkHttpClient.builder()
      .httpClientBuilder(httpClientBuilder);

    final var chatLanguageModel = OpenAiChatModel.builder() //
      .apiKey(apiKey) //
      .modelName(model) //
      .httpClientBuilder(jdkHttpClientBuilder)
      .build();

    final var embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
    final var retrieverToDescription = new HashMap<ContentRetriever, String>();
    retrieverToDescription.put( //
      generateContentRetriever(embeddingModel,
        documentConfig.getDocumentPath() + "/manual/assistant.txt"),
      "AI Assistant Explained");
    retrieverToDescription.put( //
      generateContentRetriever(embeddingModel,
        documentConfig.getDocumentPath() + "/manual/comment.txt"),
      "About comments");
    retrieverToDescription.put( //
      generateContentRetriever(embeddingModel,
        documentConfig.getDocumentPath() + "/manual/createTask.txt"),
      "About creating a task");
    retrieverToDescription.put( //
      generateContentRetriever(embeddingModel,
        documentConfig.getDocumentPath() + "/manual/home.txt"),
      "About the home screen");
    retrieverToDescription.put( //
      generateContentRetriever(embeddingModel,
        documentConfig.getDocumentPath() + "/manual/loginAndLogout.txt"),
      "About login and logout");
    retrieverToDescription.put( //
      generateContentRetriever(embeddingModel,
        documentConfig.getDocumentPath() + "/manual/task.txt"),
      "About tasks");
    retrieverToDescription.put( //
      generateContentRetriever(embeddingModel,
        documentConfig.getDocumentPath() + "/manual/taskDetail.txt"),
      "Editing and Deleting Tasks on the Task Details screen");
    retrieverToDescription.put( //
      generateContentRetriever(embeddingModel,
        documentConfig.getDocumentPath() + "/manual/taskList.txt"),
      "About the task list screen and task search");
    final var queryRouter = new LanguageModelQueryRouter(chatLanguageModel, retrieverToDescription);
    final var retrievalAugmentor = DefaultRetrievalAugmentor.builder()
      .queryRouter(queryRouter)
      .build();

    this.assistant = AiServices.builder(Assistant.class) //
      .chatModel(chatLanguageModel) //
      .retrievalAugmentor(retrievalAugmentor) //
      .tools(taskTool, dateTool) //
      .chatMemoryProvider(this::initializeChatMemory) //
      .build();
  }

  private ContentRetriever generateContentRetriever(final EmbeddingModel embeddingModel,
    final String documentPath) {
    final var biographyEmbeddingStore = embed(documentPath, embeddingModel);
    return EmbeddingStoreContentRetriever.builder()
      .embeddingStore(biographyEmbeddingStore)
      .embeddingModel(embeddingModel)
      .maxResults(2)
      .minScore(0.6)
      .build();
  }

  private static EmbeddingStore<TextSegment> embed(final String documentPath,
    EmbeddingModel embeddingModel) {
    final var documentParser = new TextDocumentParser();
    final var document = FileSystemDocumentLoader.loadDocument(documentPath, documentParser);

    final var splitter = DocumentSplitters.recursive(300, 0);
    final var segments = splitter.split(document);

    final var embeddings = embeddingModel.embedAll(segments).content();

    EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
    embeddingStore.addAll(embeddings, segments);
    return embeddingStore;
  }

  private MessageWindowChatMemory initializeChatMemory(final Object memoryId) {
    final var commentList = commentRepository.selectByThreadId((Long) memoryId);
    final var chatMemory = MessageWindowChatMemory.withMaxMessages(10);

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
