package org.ymmy.todo_chat.controller.task;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.FlashMap;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.db.entity.TaskStatus;
import org.ymmy.todo_chat.model.dto.TaskDto;
import org.ymmy.todo_chat.model.dto.TaskListDto;
import org.ymmy.todo_chat.model.dto.TaskStatusDto;
import org.ymmy.todo_chat.model.form.TaskSearchForm;
import org.ymmy.todo_chat.repository.TaskRepository;
import org.ymmy.todo_chat.repository.TaskStatusRepository;
import org.ymmy.todo_chat.utils.AuthorityUtils;
import org.ymmy.todo_chat.utils.DatabaseUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskListControllerIT {

  @BeforeEach
  void setUp() throws Exception {
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
        .withGeneratedValue("title", ValueGenerators.stringSequence("TITLE_").startingAt(1)) //
        .row() //
        .column("status_id", 1) //
        .column("start_date_time", LocalDateTime.of(2024, 1, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 1, 1, 23, 59)) //
        .column("description", "DESCRIPTION")
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 2) //
        .column("start_date_time", LocalDateTime.of(2024, 1, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 1, 1, 23, 59)) //
        .column("description", "DESCRIPTION")
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 3) //
        .column("start_date_time", LocalDateTime.of(2024, 1, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 1, 1, 23, 59)) //
        .column("description", "DESCRIPTION")
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 4) //
        .column("start_date_time", LocalDateTime.of(2024, 1, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 1, 1, 23, 59)) //
        .column("description", "DESCRIPTION")
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 1) //
        .column("start_date_time", LocalDateTime.of(2024, 2, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 2, 1, 23, 59)) //
        .column("description", "DESCRIPTION")
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 2) //
        .column("start_date_time", LocalDateTime.of(2024, 2, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 2, 1, 23, 59)) //
        .column("description", "DESCRIPTION")
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 3) //
        .column("start_date_time", LocalDateTime.of(2024, 2, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 2, 1, 23, 59)) //
        .column("description", "DESCRIPTION")
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 4) //
        .column("start_date_time", LocalDateTime.of(2024, 2, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 2, 1, 23, 59)) //
        .column("description", "DESCRIPTION")
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 1) //
        .column("start_date_time", LocalDateTime.of(2024, 3, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 3, 1, 23, 59)) //
        .column("description", "DESCRIPTION")
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 2) //
        .column("start_date_time", LocalDateTime.of(2024, 3, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 3, 1, 23, 59)) //
        .column("description", "DESCRIPTION")
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 1) //
        .column("start_date_time", LocalDateTime.of(2024, 1, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 1, 1, 23, 59)) //
        .column("description", "DESCRIPTION")
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
    void タスク一覧画面を表示できる() throws Exception {
      final Long userId = 1L;
      final var auth = AuthorityUtils.getDefaultAuth(userId);

      final var mvcResult = mockMvc.perform(get("/task")
              .with(authentication(auth)))
          .andExpect(status().isOk())
          .andExpect(view().name("task/index"))
          .andReturn();

      final var actualTaskListDto = (TaskListDto) Objects.requireNonNull(
              mvcResult.getModelAndView())
          .getModel().get("taskListDto");
      final var expectTaskDtoList = List.of( //
          generateTaskDto(1L, 1L), //
          generateTaskDto(2L, 2L), //
          generateTaskDto(3L, 3L), //
          generateTaskDto(4L, 4L), //
          generateTaskDto(5L, 1L), //
          generateTaskDto(6L, 2L), //
          generateTaskDto(7L, 3L), //
          generateTaskDto(8L, 4L), //
          generateTaskDto(9L, 1L), //
          generateTaskDto(10L, 2L) //
      );

      assertThatForTaskDtoList(actualTaskListDto.getTaskDtoList(), expectTaskDtoList);
    }
  }


  @Nested
  class Search {

    @Test
    void 検索条件に一致するタスク一覧画面を表示できる() throws Exception {
      final var userId = 1L;

      final var taskSearchForm = new TaskSearchForm( //
          Collections.emptyList(), Collections.emptyList(), null, null, //
          null, null, "TITLE_", 2L, 3L
      );
      final var auth = AuthorityUtils.getDefaultAuth(userId);

      final var mvcResult = mockMvc.perform(post("/task/search")
              .flashAttr("taskSearchForm", taskSearchForm)
              .with(authentication(auth))
              .with(csrf()))
          .andExpect(status().is3xxRedirection())
          .andExpect(view().name("redirect:/task"))
          .andReturn();

      final var redirectAttributes = (FlashMap) mvcResult.getFlashMap();
      final var actualTaskListDto = (TaskListDto) redirectAttributes.get("taskListDto");

      final var expectTaskDtoList = List.of( //
          generateTaskDto(5L, 1L), //
          generateTaskDto(6L, 2L)
      );

      assertThatForTaskDtoList(actualTaskListDto.getTaskDtoList(), expectTaskDtoList);
    }
  }

  private TaskDto generateTaskDto(final Long taskId, final Long statusId) {
    return TaskDto.builder()
        .taskId(taskId) //
        .taskStatusDto(generateTaskStatusDto(statusId)) //
        .title(initialTask.get(taskId).getTitle()) //
        .startDateTime(initialTask.get(taskId).getStartDateTime()) //
        .endDateTime(initialTask.get(taskId).getEndDateTime()) //
        .description(initialTask.get(taskId).getDescription()) //
        .createdBy(initialTask.get(taskId).getCreatedBy()) //
        .build();
  }

  private TaskStatusDto generateTaskStatusDto(final Long statusId) {
    return TaskStatusDto.builder() //
        .statusId(statusId) //
        .name(initialTaskStatus.get(statusId).getName()) //
        .colorCode(initialTaskStatus.get(statusId).getColorCode()) //
        .isReadOnly(initialTaskStatus.get(statusId).getIsReadOnly()) //
        .remarks(initialTaskStatus.get(statusId).getRemarks()) //
        .build();
  }

  private void assertThatForTaskDtoList(final List<TaskDto> actual,
      final List<TaskDto> expect) {
    IntStream.range(0, expect.size()).forEach(i -> {
      final var actualTaskDto = actual.get(i);
      final var expectTaskDto = expect.get(i);

      // TaskDtoの比較
      Assertions.assertThat(actualTaskDto)
          .usingRecursiveComparison()
          .ignoringFieldsMatchingRegexes("taskStatusDto")
          .isEqualTo(expectTaskDto);

      // TaskStatusDtoの比較
      final var actualTaskStatusDto = actualTaskDto.getTaskStatusDto();
      final var expectTaskStatusDto = expectTaskDto.getTaskStatusDto();
      Assertions.assertThat(actualTaskStatusDto)
          .usingRecursiveComparison()
          .isEqualTo(expectTaskStatusDto);
    });
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
}
