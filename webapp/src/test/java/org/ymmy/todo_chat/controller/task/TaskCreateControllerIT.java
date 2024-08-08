package org.ymmy.todo_chat.controller.task;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.ymmy.todo_chat.db.entity.Comment;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.model.form.TaskCreateForm;
import org.ymmy.todo_chat.repository.CommentRepository;
import org.ymmy.todo_chat.repository.TaskRepository;
import org.ymmy.todo_chat.util.CommentMessageEnum;
import org.ymmy.todo_chat.util.CommentStatusEnum;
import org.ymmy.todo_chat.util.ErrorMessageEnum;
import org.ymmy.todo_chat.utils.AuthorityUtils;
import org.ymmy.todo_chat.utils.DatabaseUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskCreateControllerIT {

  final static LocalDateTime FIXED_DATETIME = LocalDateTime.of(2024, 1, 1, 0, 0);

  @AfterEach
  void afterEach() {
    localDateTimeMockedStatic.close();
  }

  @BeforeEach
  void setUp() throws Exception {
    localDateTimeMockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);

    final var userOperation = Operations.insertInto("user")
        .withGeneratedValue("id", ValueGenerators.sequence().startingAt(1))
        .withGeneratedValue("name", ValueGenerators.stringSequence("USER_").startingAt(1)) //
        .withGeneratedValue("display_name",
            ValueGenerators.stringSequence("DISPLAY_NAME_").startingAt(1)) //
        .row() // id: 1
        .end() //
        .withDefaultValue("password", "password") //
        .withDefaultValue("created_by", 1L) //
        .build();

    final var taskOperation = Operations.insertInto("task")
        .withGeneratedValue("id", ValueGenerators.sequence().startingAt(1))
        .withGeneratedValue("title",
            ValueGenerators.stringSequence("TITLE_").startingAt(1).incrementingBy(2)) //
        .row() //
        .column("status_id", 1) //
        .column("start_date_time", LocalDateTime.of(2024, 1, 1, 0, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 1, 31, 23, 59, 59)) //
        .column("description", "DESCRIPTION")
        .end() //
        .withDefaultValue("created_by", 1L) //
        .build();

    DatabaseUtils.executeSqlFile(dataSource, "src/test/resources/db/deleteAll.sql");
    final var dbSetup = new DbSetup(new DataSourceDestination(dataSource),
        sequenceOf(userOperation, taskOperation));
    dbSetup.launch();
  }

  private TaskCreateForm generateTaskCreateForm(final LocalDateTime startDateTime,
      final LocalDateTime endDataTime) {
    return new TaskCreateForm("TITLE", startDateTime, endDataTime, "DESCRIPTION");
  }

  @Nested
  class Add {

    @Test
    void タスク新規作成画面を表示できる() throws Exception {
      final var userId = 1L;
      final var auth = AuthorityUtils.getDefaultAuth(userId);
      mockMvc.perform(get("/task/add")
              .with(authentication(auth))
              .with(csrf()))
          .andExpect(status().isOk())
          .andExpect(view().name("task/add"))
          .andExpect(model().attributeExists("taskCreateForm"));
    }
  }

  @Nested
  class Create {

    @Test
    void タスクを作成できる() throws Exception {

      localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(FIXED_DATETIME);

      final var userId = 1L;
      final var form = generateTaskCreateForm( //
          LocalDateTime.of(2024, 1, 1, 0, 0, 0), //
          LocalDateTime.of(2024, 1, 2, 0, 0, 0) //
      );
      final var auth = AuthorityUtils.getDefaultAuth(userId);

      mockMvc.perform(post("/task/create")
              .with(authentication(auth))
              .with(csrf())
              .flashAttr("taskCreateForm", form))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/task/detail/2"));

      final var actualTask = taskRepository.selectById(2L);
      final var expectTask = new Task(2L, 1L, "TITLE", LocalDateTime.of(2024, 1, 1, 0, 0, 0),
          LocalDateTime.of(2024, 1, 2, 0, 0, 0),
          null, userId, null, null, "DESCRIPTION");
      assertThat(actualTask) //
          .usingRecursiveComparison()  //
          .ignoringFieldsMatchingRegexes("createdAt", "updatedAt", "version") //
          .isEqualTo(expectTask);

      final var actualComment = commentRepository.selectByThreadId(userId);
      final var expectComment = List.of(
          generateComment(1L, userId, userId, "TITLE", CommentMessageEnum.CREATE_TASK));
      assertThat(actualComment) //
          .usingRecursiveComparison()  //
          .isEqualTo(expectComment);
    }

    @Test
    void 開始日時が終了日時より後になっている場合タスクを作成できない() throws Exception {
      final var userId = 1L;
      final var form = generateTaskCreateForm( //
          LocalDateTime.of(2024, 1, 2, 0, 0, 0), //
          LocalDateTime.of(2024, 1, 1, 0, 0, 0) //
      );
      final var auth = AuthorityUtils.getDefaultAuth(userId);

      mockMvc.perform(post("/task/create")
              .with(authentication(auth))
              .with(csrf())
              .flashAttr("taskCreateForm", form))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/task/add"))
          .andExpect(flash().attributeExists("exception"))
          .andExpect(flash().attribute("exception", ErrorMessageEnum.INVALID_PERIOD.getMessage()));

      final var actual = taskRepository.selectAll();
      assertThat(actual).hasSize(1);
    }

    private Comment generateComment(final Long id, final Long threadId, final Long userId,
        final String title, final
    CommentMessageEnum messageEnum) {
      return new Comment() //
          .withId(id) //
          .withId(threadId) //
          .withThreadId(userId) //
          .withStatus(CommentStatusEnum.USER_UNCONFIRMED.getCode()) //
          .withCreatedBy(0L) //
          .withCreatedAt(FIXED_DATETIME) //
          .withComment(String.format(messageEnum.getMessage(), title, "01/01"));
    }
  }


  @Autowired
  DataSource dataSource;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private TaskRepository taskRepository;
  @Autowired
  private CommentRepository commentRepository;

  static MockedStatic<LocalDateTime> localDateTimeMockedStatic;
}
