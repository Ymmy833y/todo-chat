package org.ymmy.todo_chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.ymmy.todo_chat.exception.BadRequestException;
import org.ymmy.todo_chat.logic.CommentLogic;
import org.ymmy.todo_chat.logic.TaskLogic;
import org.ymmy.todo_chat.model.dto.CommentDetailDto;
import org.ymmy.todo_chat.model.dto.PaginationDto;
import org.ymmy.todo_chat.model.dto.TaskCompleteDto;
import org.ymmy.todo_chat.model.dto.TaskCreateDto;
import org.ymmy.todo_chat.model.dto.TaskDetailDto;
import org.ymmy.todo_chat.model.dto.TaskDto;
import org.ymmy.todo_chat.model.dto.TaskEditDto;
import org.ymmy.todo_chat.model.dto.TaskStatusDto;
import org.ymmy.todo_chat.repository.TaskRepository;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

@SpringBootTest
@ActiveProfiles("test")
public class TaskServiceTest {

  final Long TASK_ID = 1L;
  final Long USER_ID = 1L;

  @Nested
  class GetTaskDetailDto {

    final String STATUS_REMARKS = "STATUS_REMARKS";
    private final String DETAIL_MESSAGE_FORMAT = "このタスクは、%sです。";

    @Test
    void タスク詳細画面の詳細情報を取得できる() {
      final var taskDto = generateTaskDto(TASK_ID, 1L, STATUS_REMARKS);
      final var taskStatusDtoList = List.of(
          generateTaskStatusDto(1L, STATUS_REMARKS),
          generateTaskStatusDto(2L, STATUS_REMARKS),
          generateTaskStatusDto(3L, STATUS_REMARKS)
      );
      final var commentDetailDto = generateCommentDetailDto(USER_ID);

      doReturn(taskDto).when(taskLogic).getTaskDto(anyLong(), anyLong());
      doReturn(taskStatusDtoList).when(taskLogic).getTaskStatusDtoList();
      doReturn(commentDetailDto).when(commentLogic).getCommentDetailDto(anyLong());

      final var actual = taskService.getTaskDetailDto(TASK_ID, USER_ID);

      final var expectDetailMessages = new HashMap<String, String>();
      expectDetailMessages.put("warning", String.format(DETAIL_MESSAGE_FORMAT, STATUS_REMARKS));
      final var expect = TaskDetailDto.builder() //
          .taskDto(taskDto) //
          .statusDtoList(taskStatusDtoList) //
          .detailMessages(expectDetailMessages) //
          .commentDetailDto(commentDetailDto) //
          .build();

      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expect);
    }
  }

  @Nested
  class Create {

    final Long USER_ID = 1L;

    @Test
    void タスクを作成できる() {
      final var taskCreateDto = TaskCreateDto.builder() //
          .title("TITLE") //
          .startDateTime(LocalDateTime.of(2024, 1, 1, 0, 0, 0)) //
          .endDateTime(LocalDateTime.of(2024, 1, 2, 0, 0, 0)) //
          .description("DESCRIPTION") //
          .build();

      doReturn(1L).when(taskRepository).insert(any(), anyLong());
      doNothing().when(commentService).saveAndSendAppGeneratedComment(any());

      final var actual = taskService.create(taskCreateDto, USER_ID);

      assertThat(actual).isEqualTo(1L);
      verify(taskRepository, times(1)).insert(any(), anyLong());
      verify(commentService, times(1)).saveAndSendAppGeneratedComment(any());
    }

    @Test
    void 開始日時が終了日時より後になっている場合BadRequestExceptionを返す() {
      final var taskCreateDto = TaskCreateDto.builder() //
          .title("TITLE") //
          .startDateTime(LocalDateTime.of(2024, 1, 11, 0, 0, 0)) //
          .endDateTime(LocalDateTime.of(2024, 1, 2, 0, 0, 0)) //
          .description("DESCRIPTION") //
          .build();

      assertThatThrownBy(() -> taskService.create(taskCreateDto, USER_ID)) //
          .isInstanceOf(BadRequestException.class) //
          .hasMessageContaining(ErrorMessageEnum.INVALID_PERIOD.getMessage());
      verify(taskRepository, never()).insert(any(), anyLong());
    }
  }

  @Nested
  class Update {

    @Test
    void タスクを更新できる() {
      final var taskEditDto = generateTaskEditDto(TASK_ID, LocalDateTime.of(2024, 1, 2, 0, 0));
      doReturn(generateTaskDto(TASK_ID, 1L, "REMARKS")).when(taskLogic)
          .getTaskDto(anyLong(), anyLong());
      doNothing().when(taskRepository).update(any(), anyLong());
      doNothing().when(commentService).saveAndSendAppGeneratedComment(any());

      taskService.update(taskEditDto, USER_ID);
      verify(taskRepository, times(1)).update(any(), anyLong());
      verify(commentService, times(1)).saveAndSendAppGeneratedComment(any());
    }

    @Test
    void 開始日時が終了日時より後になっている場合BadRequestExceptionを返す() {
      final var taskEditDto = generateTaskEditDto(TASK_ID, LocalDateTime.of(2023, 12, 31, 0, 0));
      doReturn(generateTaskDto(TASK_ID, 1L, "REMARKS")).when(taskLogic)
          .getTaskDto(anyLong(), anyLong());
      doNothing().when(taskRepository).update(any(), anyLong());

      assertThatThrownBy(() -> taskService.update(taskEditDto, USER_ID)) //
          .isInstanceOf(BadRequestException.class) //
          .hasMessageContaining(ErrorMessageEnum.INVALID_PERIOD.getMessage());
      verify(taskRepository, never()).update(any(), anyLong());
    }

    private TaskEditDto generateTaskEditDto(final Long taskId, final LocalDateTime endDateTime) {
      return TaskEditDto.builder() //
          .taskId(taskId) //
          .statusId(1L) //
          .title("TITLE") //
          .description("DESCRIPTION") //
          .startDateTime(LocalDateTime.of(2024, 1, 1, 0, 0)) //
          .endDateTime(endDateTime) //
          .build();
    }
  }

  @Nested
  class UpdateStatus {

    @Test
    void タスクを更新できる() {
      final var taskCompleteDto = generateTaskCompleteDto(TASK_ID);
      doReturn(generateTaskDto(TASK_ID, 1L, "REMARKS")).when(taskLogic)
          .getTaskDto(anyLong(), anyLong());
      doNothing().when(taskRepository).update(any(), anyLong());
      doNothing().when(commentService).saveAndSendAppGeneratedComment(any());

      taskService.updateStatus(taskCompleteDto, USER_ID);
      verify(taskRepository, times(1)).update(any(), anyLong());
      verify(commentService, times(1)).saveAndSendAppGeneratedComment(any());
    }

    private TaskCompleteDto generateTaskCompleteDto(final Long taskId) {
      return TaskCompleteDto.builder() //
          .taskId(taskId) //
          .statusId(3L) //
          .build();
    }
  }

  @Nested
  class GeneratePaginationDto {

    @Test
    void ページネーションDTOを生成できる() {
      final var total = 50L;
      final var split = 5L;
      final var current = 5L;

      final var actual = taskService.generatePaginationDto(total, split, current);

      final var expectPageList = Map.of(
          1L, false,
          4L, false,
          5L, true,
          6L, false,
          10L, false
      );
      final var expect = PaginationDto.builder() //
          .currentPage(current) //
          .pageList(expectPageList) //
          .build();

      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expect);
    }

    @Test
    void タスクの合計数が0の場合ページネーションDTOを生成できる() {
      final var total = 0L;
      final var split = 5L;
      final var current = 5L;

      final var actual = taskService.generatePaginationDto(total, split, current);

      final var expect = PaginationDto.builder().build();

      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expect);
    }
  }

  private TaskDto generateTaskDto(final Long taskId, final Long statusId,
      final String statusRemarks) {
    return TaskDto.builder()
        .taskId(taskId) //
        .taskStatusDto(generateTaskStatusDto(statusId, statusRemarks)) //
        .title("TITLE") //
        .startDateTime(LocalDateTime.of(2024, 1, 1, 0, 0)) //
        .build();
  }

  private TaskStatusDto generateTaskStatusDto(final Long statusId, final String remarks) {
    return TaskStatusDto.builder()
        .statusId(statusId) //
        .name("NAME") //
        .colorCode("COLOR_CODE") //
        .isReadOnly(true) //
        .remarks(remarks) //
        .build();
  }

  private CommentDetailDto generateCommentDetailDto(final Long threadId) {
    return CommentDetailDto.builder() //
        .commentDtoList(Collections.emptyList()) //
        .threadId(threadId) //
        .build();
  }

  @InjectMocks
  private TaskService taskService;
  @Mock
  private CommentService commentService;
  @Mock
  private TaskLogic taskLogic;
  @Mock
  private CommentLogic commentLogic;
  @Mock
  private TaskRepository taskRepository;
}
