package org.ymmy.todo_chat.repository;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.db.entity.TaskStatus;
import org.ymmy.todo_chat.model.dto.TaskSearchDto;
import org.ymmy.todo_chat.model.entity.TaskEntity;
import org.ymmy.todo_chat.utils.DatabaseUtils;

@SpringBootTest
@ActiveProfiles("test")
public class TaskRepositoryTest {

  @BeforeEach
  void setUp() throws Exception {
    final var taskOperation = Operations.insertInto("task")
        .withGeneratedValue("id", ValueGenerators.sequence().startingAt(1))
        .withGeneratedValue("title",
            ValueGenerators.stringSequence("TITLE_").startingAt(1)) //
        .withGeneratedValue("description",
            ValueGenerators.stringSequence("DESCRIPTION_").startingAt(1)) //
        .row() //
        .column("status_id", 1L) //
        .column("start_date_time", LocalDateTime.of(2024, 1, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 1, 2, 0, 0)) //
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 1L) //
        .column("start_date_time", LocalDateTime.of(2023, 12, 31, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 1, 1, 0, 0)) //
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 2L) //
        .column("start_date_time", LocalDateTime.of(2024, 1, 31, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 2, 1, 0, 0)) //
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 3L) //
        .column("start_date_time", LocalDateTime.of(2024, 2, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 2, 2, 0, 0)) //
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 4L) //
        .column("start_date_time", LocalDateTime.of(2024, 2, 2, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 2, 3, 0, 0)) //
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("status_id", 1L) //
        .column("start_date_time", LocalDateTime.of(2024, 1, 1, 0, 0)) //
        .column("end_date_time", LocalDateTime.of(2024, 1, 2, 0, 0)) //
        .column("created_by", 2L) //
        .end() //
        .build();

    DatabaseUtils.executeSqlFile(dataSource, "src/test/resources/db/deleteAll.sql");
    final var dbSetup = new DbSetup(new DataSourceDestination(dataSource),
        sequenceOf(taskOperation));
    dbSetup.launch();

    initialTask = taskRepository.selectAll().stream()
        .collect(Collectors.toMap(Task::getId, e -> e));
    initialTaskStatus = taskStatusRepository.selectAll().stream()
        .collect(Collectors.toMap(TaskStatus::getId, e -> e));
  }

  @Nested
  class SelectAllBySearchCriteria {

    @Test
    void 作成者が1Lのタスクを取得できる() {
      final var searchDto = TaskSearchDto.builder()
          .includeCreatedBy(1L)
          .build();

      final var actual = taskRepository.selectAllBySearchCriteria(searchDto);
      final var expect = List.of(
          generetaTaskEntity(1L, 1L),
          generetaTaskEntity(2L, 1L),
          generetaTaskEntity(3L, 2L),
          generetaTaskEntity(4L, 3L),
          generetaTaskEntity(5L, 4L)
      );
      assertThatForTaskEntity(actual, expect);
    }

    @Test
    void ステータスが1Lのタスクを取得できる() {
      final var searchDto = TaskSearchDto.builder()
          .includeStatus(List.of(1L))
          .build();

      final var actual = taskRepository.selectAllBySearchCriteria(searchDto);
      final var expect = List.of(
          generetaTaskEntity(1L, 1L),
          generetaTaskEntity(2L, 1L)
      );
      assertThatForTaskEntity(actual, expect);
    }

    @Test
    void ステータスが1L以外のタスクを取得できる() {
      final var searchDto = TaskSearchDto.builder()
          .excludedStatus(List.of(1L))
          .build();

      final var actual = taskRepository.selectAllBySearchCriteria(searchDto);
      final var expect = List.of(
          generetaTaskEntity(3L, 2L),
          generetaTaskEntity(4L, 3L),
          generetaTaskEntity(5L, 4L)
      );
      assertThatForTaskEntity(actual, expect);
    }

    @Test
    void ステータスが3Lで作成者が1Lのタスクを取得できる() {
      final var searchDto = TaskSearchDto.builder()
          .includeStatus(List.of(3L))
          .includeCreatedBy(1L)
          .build();

      final var actual = taskRepository.selectAllBySearchCriteria(searchDto);
      final var expect = List.of(
          generetaTaskEntity(4L, 3L)
      );
      assertThatForTaskEntity(actual, expect);
    }

    @Test
    void 開始日時が2024年2月1日以降のタスクを取得できる() {
      final var searchDto = TaskSearchDto.builder()
          .startDateTimeFrom(LocalDateTime.of(2024, 2, 1, 0, 0))
          .build();

      final var actual = taskRepository.selectAllBySearchCriteria(searchDto);
      final var expect = List.of(
          generetaTaskEntity(4L, 3L),
          generetaTaskEntity(5L, 4L)
      );
      assertThatForTaskEntity(actual, expect);
    }

    @Test
    void 開始日時が2024年1月13日以前のタスクを取得できる() {
      final var searchDto = TaskSearchDto.builder()
          .startDateTimeTo(LocalDateTime.of(2024, 1, 31, 0, 0))
          .build();

      final var actual = taskRepository.selectAllBySearchCriteria(searchDto);
      final var expect = List.of(
          generetaTaskEntity(1L, 1L),
          generetaTaskEntity(2L, 1L),
          generetaTaskEntity(3L, 2L),
          generetaTaskEntity(6L, 1L)
      );
      assertThatForTaskEntity(actual, expect);
    }

    @Test
    void 終了日時が2024年2月1日以降のタスクを取得できる() {
      final var searchDto = TaskSearchDto.builder()
          .endDateTimeFrom(LocalDateTime.of(2024, 2, 1, 0, 0))
          .build();

      final var actual = taskRepository.selectAllBySearchCriteria(searchDto);
      final var expect = List.of(
          generetaTaskEntity(3L, 2L),
          generetaTaskEntity(4L, 3L),
          generetaTaskEntity(5L, 4L)
      );
      assertThatForTaskEntity(actual, expect);
    }

    @Test
    void 終了日時が2024年2月1日以前のタスクを取得できる() {
      final var searchDto = TaskSearchDto.builder()
          .endDateTimeTo(LocalDateTime.of(2024, 2, 1, 0, 0))
          .build();

      final var actual = taskRepository.selectAllBySearchCriteria(searchDto);
      final var expect = List.of(
          generetaTaskEntity(1L, 1L),
          generetaTaskEntity(2L, 1L),
          generetaTaskEntity(3L, 2L),
          generetaTaskEntity(6L, 1L)
      );
      assertThatForTaskEntity(actual, expect);
    }

    @Test
    void タイトルに特定の文字列を含むタスクを取得できる() {
      final var searchDto = TaskSearchDto.builder()
          .includeTitle("TITLE_1")
          .build();

      final var actual = taskRepository.selectAllBySearchCriteria(searchDto);
      final var expect = List.of(
          generetaTaskEntity(1L, 1L)
      );
      assertThatForTaskEntity(actual, expect);
    }

    @Test
    void タイトルが空文字列の場合に全タスクを取得できる() {
      final var searchDto = TaskSearchDto.builder()
          .includeTitle("")
          .build();

      final var actual = taskRepository.selectAllBySearchCriteria(searchDto);
      final var expect = List.of(
          generetaTaskEntity(1L, 1L),
          generetaTaskEntity(2L, 1L),
          generetaTaskEntity(3L, 2L),
          generetaTaskEntity(4L, 3L),
          generetaTaskEntity(5L, 4L),
          generetaTaskEntity(6L, 1L)
      );
      assertThatForTaskEntity(actual, expect);
    }

    @Test
    void 特定の範囲のタスクを取得できる() {
      final var searchDto = TaskSearchDto.builder()
          .includeCount(2L)
          .includeStartPosition(0L)
          .build();

      final var actual = taskRepository.selectAllBySearchCriteria(searchDto);
      final var expect = List.of(
          generetaTaskEntity(1L, 1L),
          generetaTaskEntity(2L, 1L)
      );
      assertThatForTaskEntity(actual, expect);
    }
  }

  private void assertThatForTaskEntity(final List<TaskEntity> actual,
      final List<TaskEntity> expect) {
    IntStream.range(0, expect.size()).forEach(i -> {
      final var actualTaskEntity = actual.get(i);
      final var expectTaskEntity = expect.get(i);

      // TaskEntityの比較
      assertThat(actualTaskEntity)
          .usingRecursiveComparison()
          .ignoringFieldsMatchingRegexes("createdAt", "updatedAt", "version", "taskStatus")
          .isEqualTo(expectTaskEntity);

      // TaskStatusの比較
      final var actualTaskStatus = actualTaskEntity.getTaskStatus();
      final var expectTaskStatus = expectTaskEntity.getTaskStatus();
      assertThat(actualTaskStatus)
          .usingRecursiveComparison()
          .ignoringFieldsMatchingRegexes("createdAt", "createdBy")
          .isEqualTo(expectTaskStatus);
    });
  }

  private TaskEntity generetaTaskEntity(final Long taskId, final Long statusId) {
    final var task = initialTask.get(taskId);
    return new TaskEntity(taskId, statusId, task.getTitle(), task.getDescription(),
        task.getStartDateTime(),
        task.getEndDateTime(), task.getCreatedBy(), initialTaskStatus.get(statusId)
    );
  }


  @Autowired
  TaskRepository taskRepository;

  @Autowired
  TaskStatusRepository taskStatusRepository;

  @Autowired
  DataSource dataSource;

  Map<Long, Task> initialTask = null;
  Map<Long, TaskStatus> initialTaskStatus = null;
}
