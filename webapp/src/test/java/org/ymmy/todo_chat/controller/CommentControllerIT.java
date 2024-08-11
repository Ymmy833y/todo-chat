package org.ymmy.todo_chat.controller;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.ymmy.todo_chat.db.entity.Comment;
import org.ymmy.todo_chat.model.dto.CommentDto;
import org.ymmy.todo_chat.repository.CommentRepository;
import org.ymmy.todo_chat.util.CommentStatusEnum;
import org.ymmy.todo_chat.util.ErrorMessageEnum;
import org.ymmy.todo_chat.utils.AuthorityUtils;
import org.ymmy.todo_chat.utils.DatabaseUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommentControllerIT {

  final String USER_COMMENT = "USER_COMMENT";
  final String APP_COMMENT = "APP_COMMENT";

  @BeforeEach
  void setUp() throws Exception {

    DatabaseUtils.executeSqlFile(dataSource, "src/test/resources/db/deleteAll.sql");

    final var commentOperation = Operations.insertInto("comment")
        .withGeneratedValue("id", ValueGenerators.sequence().startingAt(1))
        .row() //
        .column("thread_id", 1L) //
        .column("comment", USER_COMMENT) //
        .column("status", CommentStatusEnum.APP_UNCONFIRMED.getCode()) //
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("thread_id", 1L) //
        .column("comment", APP_COMMENT) //
        .column("status", CommentStatusEnum.USER_UNCONFIRMED.getCode()) //
        .column("created_by", 0L) //
        .end() //
        .row() //
        .column("thread_id", 2L) //
        .column("comment", USER_COMMENT) //
        .column("status", CommentStatusEnum.APP_UNCONFIRMED.getCode()) //
        .column("created_by", 2L) //
        .end() //
        .withDefaultValue("created_at", LocalDateTime.of(2024, 1, 1, 0, 0)) //
        .build();

    final var dbSetup = new DbSetup(new DataSourceDestination(dataSource),
        sequenceOf(commentOperation));
    dbSetup.launch();

    initialComment = commentRepository.selectAll().stream()
        .collect(Collectors.toMap(Comment::getId, e -> e));
  }

  @Nested
  class Send {

    @Test
    void ユーザーのコメントに対して返信コメントを返す() throws Exception {
      final var threadId = 1L;
      final var userId = 1L;
      final var auth = AuthorityUtils.getDefaultAuth(userId);

      final var mvcResult = mockMvc.perform(post("/comment/send")
              .contentType(MediaType.APPLICATION_JSON)
              .content(generateCommentJson(threadId, USER_COMMENT))
              .with(authentication(auth))
              .with(csrf()))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.comment").exists())
          .andExpect(jsonPath("$.threadId").exists())
          .andExpect(jsonPath("$.status").exists())
          .andExpect(jsonPath("$.createdBy").exists())
          .andReturn();

      final var jsonResponse = mvcResult.getResponse().getContentAsString();
      assertThat(jsonResponse).isNotNull();

      final var objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());

      final var actualReplyCommentDto = objectMapper.readValue(jsonResponse, CommentDto.class);
      final var expectReplyCommentDto = generateReplyCommentDto(threadId);

      assertThat(actualReplyCommentDto) //
          .usingRecursiveComparison()  //
          .ignoringFieldsMatchingRegexes("id", "comment", "createdAt") //
          .isEqualTo(expectReplyCommentDto);

      // DB登録されているか検証
      final var commentList = commentRepository.selectByThreadId(1L);
      assertThat(commentList.size()).isEqualTo(4);
    }

    @Test
    void ログインユーザーとスレッドIDが紐づいていない場合BadRequestExceptionを返す()
        throws Exception {
      final var threadId = 2L;
      final var userId = 1L;
      final var auth = AuthorityUtils.getDefaultAuth(userId);

      mockMvc.perform(post("/comment/send")
              .contentType(MediaType.APPLICATION_JSON)
              .content(generateCommentJson(threadId, USER_COMMENT))
              .with(authentication(auth))
              .with(csrf()))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  class Confirmed {

    @Test
    void 指定したスレッドのユーザー確認待ちのコメントのステータスを全て確認済みにできる()
        throws Exception {
      final var threadId = 1L;
      final var userId = 1L;
      final var auth = AuthorityUtils.getDefaultAuth(userId);

      mockMvc.perform(post("/comment/confirmed")
              .contentType(MediaType.APPLICATION_JSON)
              .content(generateCommentConfirmedJson(threadId))
              .with(authentication(auth))
              .with(csrf()))
          .andExpect(status().isOk())
          .andExpect(content().string("Confirmed"))
          .andReturn();

      final var comment = commentRepository.selectById(2L);
      assertThat(comment).isNotEmpty();
      assertThat(comment.get().getStatus()).isEqualTo(CommentStatusEnum.CONFIRMED.getCode());
    }

    @Test
    void ログインユーザーとスレッドIDが紐づいていない場合BadRequestExceptionを返す()
        throws Exception {
      final var threadId = 2L;
      final var userId = 1L;
      final var auth = AuthorityUtils.getDefaultAuth(userId);

      mockMvc.perform(post("/comment/confirmed")
              .contentType(MediaType.APPLICATION_JSON)
              .content(generateCommentConfirmedJson(threadId))
              .with(authentication(auth))
              .with(csrf()))
          .andExpect(status().isBadRequest())
          .andExpect(content().string(ErrorMessageEnum.INVALID_COMMENT_THREAD.getMessage()))
          .andReturn();

      final var comment = commentRepository.selectById(2L);
      assertThat(comment).isNotEmpty();
      assertThat(comment.get().getStatus()).isEqualTo(CommentStatusEnum.USER_UNCONFIRMED.getCode());
    }
  }


  private String generateCommentJson(final Long threadId, final String comment) {
    return String.format("{\"threadId\":\"%d\", \"comment\":\"%s\"}", threadId, comment);
  }

  private String generateCommentConfirmedJson(final Long threadId) {
    return String.format("{\"threadId\":\"%d\"}", threadId);
  }

  private CommentDto generateReplyCommentDto(final Long threadId) {
    return CommentDto.builder() //
        .threadId(threadId) //
        .status(CommentStatusEnum.USER_UNCONFIRMED.getCode()) //
        .createdBy(0L) //
        .build();
  }

  @Autowired
  DataSource dataSource;

  @Autowired
  private MockMvc mockMvc;


  @Autowired
  private CommentRepository commentRepository;

  Map<Long, Comment> initialComment = null;
}
