package org.ymmy.todo_chat.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.ymmy.todo_chat.db.entity.TaskStatus;
import org.ymmy.todo_chat.utils.DatabaseUtils;

@SpringBootTest
@ActiveProfiles("test")
public class TaskStatusRepositoryTest {

  @BeforeEach
  void setUp() throws Exception {
    DatabaseUtils.executeSqlFile(dataSource, "src/test/resources/db/deleteAll.sql");

    initialTaskStatus = taskStatusRepository.selectAll().stream()
        .collect(Collectors.toMap(TaskStatus::getId, e -> e));
  }

  @Nested
  class SelectByName {

    @Test
    void 検索条件に一致するタスクステータスを取得できる() {
      final var name = "未着手";
      final var actual = taskStatusRepository.selectByName(name);

      final var expect = initialTaskStatus.get(1L);

      assertThat(actual).isNotEmpty();
      assertThat(actual.get())
          .usingRecursiveComparison()
          .ignoringFieldsMatchingRegexes("createdAt", "createdBy")
          .isEqualTo(expect);
    }
  }

  @Autowired
  TaskStatusRepository taskStatusRepository;

  @Autowired
  DataSource dataSource;
  Map<Long, TaskStatus> initialTaskStatus = null;
}
