package org.ymmy.todo_chat.controller;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
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
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.db.entity.TaskStatus;
import org.ymmy.todo_chat.model.dto.HomeDto;
import org.ymmy.todo_chat.model.dto.TaskDto;
import org.ymmy.todo_chat.model.dto.TaskStatusDto;
import org.ymmy.todo_chat.repository.TaskRepository;
import org.ymmy.todo_chat.repository.TaskStatusRepository;
import org.ymmy.todo_chat.utils.DatabaseUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class HomeControllerIT {

  final static LocalDateTime FIXED_DATETIME = LocalDateTime.of(2024, 7, 7, 0, 0);

  @AfterEach
  void afterEach() {
    localDateTimeMockedStatic.close();
  }

  @BeforeEach
  void setUp() throws Exception {
    localDateTimeMockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);

    final var userOperation = Operations.insertInto("user")
        .withGeneratedValue("id", ValueGenerators.sequence().startingAt(1))
        .withGeneratedValue("name",
            ValueGenerators.stringSequence("USER_").startingAt(1)) //
        .withGeneratedValue("display_name",
            ValueGenerators.stringSequence("DISPLAY_NAME_").startingAt(1)) //
        .row() //
        .end() //
        .withDefaultValue("created_by", 1L) //
        .build();

    final var taskOperation = Operations.insertInto("task")
        .withGeneratedValue("id", ValueGenerators.sequence().startingAt(1))
        .withGeneratedValue("title",
            ValueGenerators.stringSequence("TITLE_").startingAt(1)) //
        .withGeneratedValue("description",
            ValueGenerators.stringSequence("DESCRIPTION_").startingAt(1)) //
        .row() // id: 1
        .column("status_id", 1L) //
        .column("start_date_time", LocalDateTime.of(2024, 7, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 7, 7, 23, 59)) //
        .column("created_by", 1L) //
        .end() //
        .row() // id: 2
        .column("status_id", 3L) //
        .column("start_date_time", LocalDateTime.of(2024, 7, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 7, 7, 23, 59)) //
        .column("created_by", 1L) //
        .end() //
        .row() // id: 3
        .column("status_id", 1L) //
        .column("start_date_time", LocalDateTime.of(2024, 7, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 7, 6, 23, 59)) //
        .column("created_by", 1L) //
        .end() //
        .row() // id: 4
        .column("status_id", 3L) //
        .column("start_date_time", LocalDateTime.of(2024, 7, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 7, 6, 23, 59)) //
        .column("created_by", 1L) //
        .end() //
        .row() // id: 6
        .column("status_id", 1L) //
        .column("start_date_time", LocalDateTime.of(2024, 7, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 7, 14, 23, 59)) //
        .column("created_by", 1L) //
        .end() //
        .row() // id: 6
        .column("status_id", 3L) //
        .column("start_date_time", LocalDateTime.of(2024, 7, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 7, 14, 23, 59)) //
        .column("created_by", 1L) //
        .end() //
        .row() // id: 7
        .column("status_id", 1L) //
        .column("start_date_time", LocalDateTime.of(2024, 7, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 7, 15, 23, 59)) //
        .column("created_by", 1L) //
        .end() //
        .row() // id: 8
        .column("status_id", 3L) //
        .column("start_date_time", LocalDateTime.of(2024, 7, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 7, 15, 23, 59)) //
        .column("created_by", 1L) //
        .end() //
        .row() // id: 9
        .column("status_id", 1L) //
        .column("start_date_time", LocalDateTime.of(2024, 7, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 7, 7, 23, 59)) //
        .column("created_by", 2L) //
        .end() //
        .row() // id: 10
        .column("status_id", 1L) //
        .column("start_date_time", LocalDateTime.of(2024, 7, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 7, 14, 23, 59)) //
        .column("created_by", 2L) //
        .end() //
        .row() // id: 11
        .column("status_id", 3L) //
        .column("start_date_time", LocalDateTime.of(2024, 7, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 7, 14, 23, 59)) //
        .column("created_by", 2L) //
        .end() //
        .build();

    DatabaseUtils.executeSqlFile(dataSource, "src/test/resources/db/deleteAll.sql");
    final var dbSetup = new DbSetup(new DataSourceDestination(dataSource),
        sequenceOf(userOperation, taskOperation));
    dbSetup.launch();

    initialTask = taskRepository.selectAll().stream()
        .collect(Collectors.toMap(Task::getId, e -> e));
    initialTaskStatus = taskStatusRepository.selectAll().stream()
        .collect(Collectors.toMap(TaskStatus::getId, e -> e));
  }

  @Nested
  class Index {

    @Test
    void ログイン状況にかかわらずログイン画面にリダイレクトできる() throws Exception {
      mockMvc.perform(get("/"))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/login"));
    }
  }

  @Nested
  class Home {

    @Test
    void 認証されていない場合ログイン画面にリダイレクトされる() throws Exception {
      mockMvc.perform(get("/home"))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/login"));
    }

    @Test
    void 認証情報が無効な場合ログイン画面にリダイレクトされる() throws Exception {
      mockMvc.perform(get("/home").sessionAttr("userId", 99L))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/login"));
    }

    @Test
    void 認証されている場合ホーム画面を表示できる() throws Exception {
      final var userId = 1L;
      localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(FIXED_DATETIME);

      final var mvcResult = mockMvc.perform(get("/home").sessionAttr("userId", userId))
          .andExpect(status().isOk())
          .andExpect(view().name("home"))
          .andReturn();

      final var actualHomeDto = (HomeDto) Objects.requireNonNull(mvcResult.getModelAndView())
          .getModel().get("homeDto");

      final var expectHomeDto = HomeDto.builder() //
          .todayTaskList(generateTaskDto(List.of(1L, 2L))) //
          .dueInAWeekTaskList(generateTaskDto(List.of(1L, 5L))) //
          .build();

      assertThat(actualHomeDto)
          .usingRecursiveComparison()
          .isEqualTo(expectHomeDto);
    }
  }

  private List<TaskDto> generateTaskDto(final List<Long> taskIdList) {
    return taskIdList.stream().map(taskId -> {
      final var task = initialTask.get(taskId);
      return TaskDto.builder() //
          .taskId(taskId) //
          .title(task.getTitle()) //
          .description(task.getDescription()) //
          .startDateTime(task.getStartDateTime()) //
          .endDateTime(task.getEndDateTime()) //
          .createdBy(task.getCreatedBy()) //
          .taskStatusDto(new TaskStatusDto(initialTaskStatus.get(task.getStatusId()))) //
          .build();
    }).toList();
  }

  @Autowired
  DataSource dataSource;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private TaskStatusRepository taskStatusRepository;

  Map<Long, Task> initialTask = null;
  Map<Long, TaskStatus> initialTaskStatus = null;

  static MockedStatic<LocalDateTime> localDateTimeMockedStatic;
}
