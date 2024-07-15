package org.ymmy.todo_chat.logic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.ymmy.todo_chat.db.entity.TaskStatus;
import org.ymmy.todo_chat.exception.NotFoundException;
import org.ymmy.todo_chat.model.entity.TaskEntity;
import org.ymmy.todo_chat.repository.TaskRepository;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

@SpringBootTest
@ActiveProfiles("test")
public class TaskLogicTest {

  @InjectMocks
  private TaskLogic taskLogic;
  @Mock
  private TaskRepository taskRepository;

  @Nested
  class GetTaskDto {

    final Long TASK_ID = 1L;
    final Long USER_ID = 1L;

    @Test
    void タスクを取得できる() {
      final var taskEntity = new TaskEntity(
          TASK_ID, 1L, "TITLE", "DESCRIPTION", LocalDateTime.of(2024, 1, 1, 0, 0),
          LocalDateTime.of(2024, 1, 1, 0, 0), USER_ID,
          new TaskStatus(1L, "NAME", "COLOR_CODE", true,
              LocalDateTime.of(2024, 1, 1, 0, 0), 1L)
      );

      doReturn(Optional.of(taskEntity)).when(taskRepository)
          .selectEntityByTaskIdAndUserId(anyLong(), anyLong());

      final var actual = taskLogic.getTaskDto(TASK_ID, USER_ID);

      assertThat(actual)
          .extracting("taskId", "createdBy")
          .containsExactly(TASK_ID, USER_ID);
    }

    @Test
    void タスクが存在しない場合NotFoundExceptionを返す() {
      doReturn(Optional.empty()).when(taskRepository)
          .selectEntityByTaskIdAndUserId(anyLong(), anyLong());

      assertThatThrownBy(() -> taskLogic.getTaskDto(TASK_ID, USER_ID)) //
          .isInstanceOf(NotFoundException.class) //
          .hasMessageContaining(ErrorMessageEnum.NON_EXISTENT_DATA.getMessage());
    }
  }

}
