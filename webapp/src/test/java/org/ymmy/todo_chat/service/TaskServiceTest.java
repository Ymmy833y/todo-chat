package org.ymmy.todo_chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.ymmy.todo_chat.exception.BadRequestException;
import org.ymmy.todo_chat.model.dto.TaskCreateDto;
import org.ymmy.todo_chat.repository.TaskRepository;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

@SpringBootTest
@ActiveProfiles("test")
public class TaskServiceTest {

  @InjectMocks
  private TaskService taskService;
  @Mock
  private TaskRepository taskRepository;

  @Nested
  class Create {

    @Test
    void タスクを作成できる() {
      final var taskCreateDto = TaskCreateDto.builder() //
          .title("TITLE") //
          .startDateTime(LocalDateTime.of(2024, 1, 1, 0, 0, 0)) //
          .endDateTime(LocalDateTime.of(2024, 1, 2, 0, 0, 0)) //
          .description("DESCRIPTION") //
          .build();

      doReturn(1L).when(taskRepository).insert(any());

      final var actual = taskService.create(taskCreateDto);

      assertThat(actual).isEqualTo(1L);
      verify(taskRepository, times(1)).insert(any());
    }

    @Test
    void 開始日時が終了日時より後になっている場合BadRequestExceptionを返す() {
      final var taskCreateDto = TaskCreateDto.builder() //
          .title("TITLE") //
          .startDateTime(LocalDateTime.of(2024, 1, 11, 0, 0, 0)) //
          .endDateTime(LocalDateTime.of(2024, 1, 2, 0, 0, 0)) //
          .description("DESCRIPTION") //
          .build();

      assertThatThrownBy(() -> taskService.create(taskCreateDto)) //
          .isInstanceOf(BadRequestException.class) //
          .hasMessageContaining(ErrorMessageEnum.INVALID_PERIOD.getMessage());
      verify(taskRepository, never()).insert(any());
    }
  }

}
