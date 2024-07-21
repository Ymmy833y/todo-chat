package org.ymmy.todo_chat.controller.task;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
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
import org.ymmy.todo_chat.model.form.TaskCompleteForm;
import org.ymmy.todo_chat.model.form.TaskEditForm;
import org.ymmy.todo_chat.repository.CommentRepository;
import org.ymmy.todo_chat.repository.TaskRepository;
import org.ymmy.todo_chat.util.CommentMessageEnum;
import org.ymmy.todo_chat.util.CommentStatusEnum;
import org.ymmy.todo_chat.util.ErrorMessageEnum;
import org.ymmy.todo_chat.utils.DatabaseUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskDetailControllerIT {

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
        .row() // id: 2
        .end() //
        .withDefaultValue("created_by", 1L) //
        .build();

    final var taskOperation = Operations.insertInto("task")
        .withGeneratedValue("id", ValueGenerators.sequence().startingAt(1))
        .withGeneratedValue("title", ValueGenerators.stringSequence("TITLE_").startingAt(1)) //
        .row() //
        .column("status_id", 1) //
        .column("start_date_time", LocalDateTime.of(2024, 1, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 1, 31, 23, 59)) //
        .column("description", "DESCRIPTION")
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 1) //
        .column("start_date_time", LocalDateTime.of(2024, 1, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 1, 31, 23, 59)) //
        .column("description", "DESCRIPTION")
        .column("created_by", 2L) //
        .end() //
        .build();

    DatabaseUtils.executeSqlFile(dataSource, "src/test/resources/db/deleteAll.sql");
    final var dbSetup = new DbSetup(new DataSourceDestination(dataSource),
        sequenceOf(userOperation, taskOperation));
    dbSetup.launch();
  }

  @Nested
  class Detail {

    @Test
    void ログインユーザーがタスクの権限がある場合タスク詳細画面を表示できる() throws Exception {
      final Long userId = 1L;
      final Long taskId = 1L;

      final var mvcResult = mockMvc.perform(get("/task/detail/{taskID}", taskId)
              .sessionAttr("userId", userId))
          .andExpect(status().isOk())
          .andExpect(view().name("task/detail"))
          .andReturn();

      final var actualTaskCompleteForm = (TaskCompleteForm) Objects.requireNonNull(
              mvcResult.getModelAndView())
          .getModel().get("taskCompleteForm");
      final var expectTaskCompleteForm = new TaskCompleteForm(taskId, 1L);
      assertThat(actualTaskCompleteForm)
          .usingRecursiveComparison()
          .isEqualTo(expectTaskCompleteForm);

      final var actualTaskEditForm = (TaskEditForm) Objects.requireNonNull(
              mvcResult.getModelAndView())
          .getModel().get("taskEditForm");
      final var expectTaskEditForm = new TaskEditForm(taskId, 1L, "TITLE_1",
          LocalDateTime.of(2024, 1, 1, 0, 0),
          LocalDateTime.of(2024, 1, 31, 23, 59),
          "DESCRIPTION");
      assertThat(actualTaskEditForm)
          .usingRecursiveComparison()
          .isEqualTo(expectTaskEditForm);
    }

    @Test
    void ログインユーザーがタスクの権限がない場合404を表示する() throws Exception {
      final Long userId = 1L;
      final Long taskId = 2L;

      mockMvc.perform(get("/task/detail/{taskID}", taskId)
              .sessionAttr("userId", userId))
          .andExpect(status().isNotFound());
    }

    @Test
    void セッションに有効な権限がない場合ログイン画面を表示する() throws Exception {
      final Long userId = 99L;
      final Long taskId = 1L;

      mockMvc.perform(get("/task/detail/{taskID}", taskId)
              .sessionAttr("userId", userId))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/login"));
    }
  }

  @Nested
  class Update {

    @Test
    void タスクを更新できる() throws Exception {

      localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(FIXED_DATETIME);

      final Long userId = 1L;
      final Long taskId = 1L;
      final var form = generateTaskEditForm( //
          taskId, LocalDateTime.of(2024, 7, 2, 0, 0, 0) //
      );

      mockMvc.perform(post("/task/update/{taskID}", taskId)
              .param("taskId", taskId.toString())
              .flashAttr("taskEditForm", form)
              .sessionAttr("userId", userId))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl(String.format("/task/detail/%s", taskId)));

      final var actualTask = taskRepository.selectById(taskId);
      final var expectTask = new Task(taskId, 2L, "NEW TITLE", //
          LocalDateTime.of(2024, 7, 1, 0, 0, 0),
          LocalDateTime.of(2024, 7, 2, 0, 0, 0),
          null, 1L, null, null, "NEW DESCRIPTION");
      Assertions.assertThat(actualTask) //
          .usingRecursiveComparison()  //
          .ignoringFieldsMatchingRegexes("createdAt", "updatedAt", "version") //
          .isEqualTo(expectTask);

      final var actualComment = commentRepository.selectByThreadId(userId);
      final var expectComment = List.of(
          generateComment(1L, userId, userId, "NEW TITLE", "07/01",
              CommentMessageEnum.UPDATE_TASK));
      Assertions.assertThat(actualComment) //
          .usingRecursiveComparison()  //
          .isEqualTo(expectComment);
    }

    @Test
    void 開始日時が終了日時より後になっている場合BadRequestExceptionを返す()
        throws Exception {
      final Long userId = 1L;
      final Long taskId = 1L;
      final var form = generateTaskEditForm( //
          taskId, LocalDateTime.of(2024, 6, 1, 0, 0, 0) //
      );

      mockMvc.perform(post("/task/update/{taskID}", taskId)
              .param("taskId", taskId.toString())
              .flashAttr("taskEditForm", form)
              .sessionAttr("userId", userId))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/task/detail/" + taskId))
          .andExpect(flash().attributeExists("exception"))
          .andExpect(flash().attribute("exception", ErrorMessageEnum.INVALID_PERIOD.getMessage()));
    }

    @Test
    void ログインユーザーがタスクの権限がない場合404を表示する()
        throws Exception {
      final Long userId = 1L;
      final Long taskId = 2L;
      final var form = generateTaskEditForm( //
          taskId, LocalDateTime.of(2024, 7, 2, 0, 0, 0) //
      );

      mockMvc.perform(post("/task/update/{taskID}", taskId)
              .param("taskId", taskId.toString())
              .flashAttr("taskEditForm", form)
              .sessionAttr("userId", userId))
          .andExpect(status().isNotFound());
    }

    @Test
    void セッションに有効な権限がない場合ログイン画面を表示する() throws Exception {
      final Long userId = 99L;
      final Long taskId = 1L;
      final var form = generateTaskEditForm( //
          taskId, LocalDateTime.of(2024, 7, 2, 0, 0, 0) //
      );

      mockMvc.perform(post("/task/update/{taskID}", taskId)
              .param("taskId", taskId.toString())
              .flashAttr("taskEditForm", form)
              .sessionAttr("userId", userId))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/login"));
    }

    private TaskEditForm generateTaskEditForm(final Long taskId, final LocalDateTime endDateTime) {
      return new TaskEditForm(taskId, 2L, "NEW TITLE", //
          LocalDateTime.of(2024, 7, 1, 0, 0), //
          endDateTime, "NEW DESCRIPTION");
    }
  }

  @Nested
  class UpdateStatus {

    @Test
    void タスクのステータスを完了にできる() throws Exception {

      localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(FIXED_DATETIME);

      final Long userId = 1L;
      final Long taskId = 1L;
      final var form = generateTaskCompleteForm(taskId);

      mockMvc.perform(post("/task/complete/{taskID}", taskId)
              .param("taskId", taskId.toString())
              .flashAttr("taskCompleteForm", form)
              .sessionAttr("userId", userId))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl(String.format("/task/detail/%s", taskId)));

      final var actualTask = taskRepository.selectById(taskId);
      final var expectTask = new Task(taskId, 3L, "TITLE_1", //
          LocalDateTime.of(2024, 1, 1, 0, 0, 0),
          LocalDateTime.of(2024, 1, 31, 23, 59),
          null, 1L, null, null, "DESCRIPTION");
      Assertions.assertThat(actualTask) //
          .usingRecursiveComparison()  //
          .ignoringFieldsMatchingRegexes("createdAt", "updatedAt", "version") //
          .isEqualTo(expectTask);

      final var actualComment = commentRepository.selectByThreadId(userId);
      final var expectComment = List.of(
          generateComment(1L, userId, userId, "TITLE_1", "01/01",
              CommentMessageEnum.COMPLETE_TASK));
      Assertions.assertThat(actualComment) //
          .usingRecursiveComparison()  //
          .isEqualTo(expectComment);
    }

    @Test
    void ログインユーザーがタスクの権限がない場合404を表示する()
        throws Exception {
      final Long userId = 1L;
      final Long taskId = 2L;
      final var form = generateTaskCompleteForm(taskId);

      mockMvc.perform(post("/task/complete/{taskID}", taskId)
              .param("taskId", taskId.toString())
              .flashAttr("taskCompleteForm", form)
              .sessionAttr("userId", userId))
          .andExpect(status().isNotFound());
    }

    @Test
    void セッションに有効な権限がない場合ログイン画面を表示する() throws Exception {
      final Long userId = 99L;
      final Long taskId = 1L;
      final var form = generateTaskCompleteForm(taskId);

      mockMvc.perform(post("/task/complete/{taskID}", taskId)
              .param("taskId", taskId.toString())
              .flashAttr("taskCompleteForm", form)
              .sessionAttr("userId", userId))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/login"));
    }

    private TaskCompleteForm generateTaskCompleteForm(final Long taskId) {
      return new TaskCompleteForm(taskId, 3L);
    }
  }

  private Comment generateComment(final Long id, final Long threadId, final Long userId,
      final String title, final String date, final CommentMessageEnum messageEnum) {
    return new Comment() //
        .withId(id) //
        .withId(threadId) //
        .withThreadId(userId) //
        .withStatus(CommentStatusEnum.USER_UNCONFIRMED.getCode()) //
        .withCreatedBy(0L) //
        .withCreatedAt(FIXED_DATETIME) //
        .withComment(String.format(messageEnum.getMessage(), title, date));
  }

  @Nested
  class Delete {

    @Test
    void タスクを削除できる() throws Exception {

      localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(FIXED_DATETIME);

      final Long userId = 1L;
      final Long taskId = 1L;
      final var form = new TaskEditForm(taskId, null, null, null, null, null);

      mockMvc.perform(post("/task/delete/{taskID}", taskId)
              .param("taskId", taskId.toString())
              .flashAttr("taskEditForm", form)
              .sessionAttr("userId", userId))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/task"));

      final var actualTask = taskRepository.selectById(taskId);
      Assertions.assertThat(actualTask).isEqualTo(null);

      final var actualComment = commentRepository.selectByThreadId(userId);
      final var expectComment = List.of(
          generateComment(1L, userId, userId, "TITLE_1", "01/01",
              CommentMessageEnum.DELETE_TASK));
      Assertions.assertThat(actualComment) //
          .usingRecursiveComparison()  //
          .isEqualTo(expectComment);
    }

    @Test
    void ログインユーザーがタスクの権限がない場合404を表示する()
        throws Exception {
      final Long userId = 1L;
      final Long taskId = 2L;
      final var form = new TaskEditForm(taskId, null, null, null, null, null);

      mockMvc.perform(post("/task/delete/{taskID}", taskId)
              .param("taskId", taskId.toString())
              .flashAttr("taskEditForm", form)
              .sessionAttr("userId", userId))
          .andExpect(status().isNotFound());
    }

    @Test
    void セッションに有効な権限がない場合ログイン画面を表示する() throws Exception {
      final Long userId = 99L;
      final Long taskId = 1L;
      final var form = new TaskEditForm(taskId, null, null, null, null, null);

      mockMvc.perform(post("/task/delete/{taskID}", taskId)
              .param("taskId", taskId.toString())
              .flashAttr("taskEditForm", form)
              .sessionAttr("userId", userId))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/login"));
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
