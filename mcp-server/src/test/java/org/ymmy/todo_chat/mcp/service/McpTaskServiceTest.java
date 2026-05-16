package org.ymmy.todo_chat.mcp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.exception.BadRequestException;
import org.ymmy.todo_chat.exception.NotFoundException;
import org.ymmy.todo_chat.logic.CommentLogic;
import org.ymmy.todo_chat.logic.TaskLogic;
import org.ymmy.todo_chat.mcp.config.McpProperties;
import org.ymmy.todo_chat.model.dto.TaskCreateDto;
import org.ymmy.todo_chat.model.dto.TaskDto;
import org.ymmy.todo_chat.model.dto.TaskEditDto;
import org.ymmy.todo_chat.model.dto.TaskSearchDto;
import org.ymmy.todo_chat.model.dto.TaskStatusDto;
import org.ymmy.todo_chat.repository.TaskRepository;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class McpTaskServiceTest {

  private static final Long USER_ID = 1L;
  private static final Long TASK_ID = 100L;

  @Mock
  private TaskRepository taskRepository;
  @Mock
  private TaskLogic taskLogic;
  @Mock
  private CommentLogic commentLogic;
  @Mock
  private McpProperties mcpProperties;

  @InjectMocks
  private McpTaskService mcpTaskService;

  @Nested
  class CreateTask {

    @Test
    void 正常系_insert後にアプリコメントが記録される() {
      doReturn(USER_ID).when(mcpProperties).getUserId();
      doReturn(TASK_ID).when(taskRepository).insert(any(Task.class), eq(USER_ID));

      final var dto = TaskCreateDto.builder() //
          .title("買い物") //
          .startDateTime(LocalDateTime.of(2026, 5, 20, 9, 0)) //
          .endDateTime(LocalDateTime.of(2026, 5, 20, 10, 0)) //
          .description("夕食用") //
          .build();

      final var actual = mcpTaskService.createTask(dto);

      assertThat(actual).isEqualTo(TASK_ID);
      final var commentCaptor = ArgumentCaptor.forClass(String.class);
      verify(commentLogic).insertAppComment(eq(USER_ID), commentCaptor.capture());
      assertThat(commentCaptor.getValue())
          .contains("買い物")
          .contains("05/20")
          .contains("新規作成");
    }

    @Test
    void 開始日時が終了日時より後ならBadRequestException() {
      doReturn(USER_ID).when(mcpProperties).getUserId();
      final var dto = TaskCreateDto.builder() //
          .title("x") //
          .startDateTime(LocalDateTime.of(2026, 5, 20, 12, 0)) //
          .endDateTime(LocalDateTime.of(2026, 5, 20, 11, 0)) //
          .build();

      assertThatThrownBy(() -> mcpTaskService.createTask(dto)) //
          .isInstanceOf(BadRequestException.class);
      verify(taskRepository, never()).insert(any(), anyLong());
      verify(commentLogic, never()).insertAppComment(anyLong(), anyString());
    }
  }

  @Nested
  class UpdateTask {

    @Test
    void 通常更新_UPDATE_TASKコメントが記録される() {
      doReturn(USER_ID).when(mcpProperties).getUserId();
      doReturn(buildTaskDto(2L)).when(taskLogic).getTaskDto(TASK_ID, USER_ID);

      final var dto = TaskEditDto.builder() //
          .taskId(TASK_ID) //
          .statusId(2L) //
          .title("更新後") //
          .startDateTime(LocalDateTime.of(2026, 5, 21, 9, 0)) //
          .endDateTime(LocalDateTime.of(2026, 5, 21, 10, 0)) //
          .build();

      mcpTaskService.updateTask(dto);

      verify(taskRepository).update(any(Task.class), eq(USER_ID));
      final var commentCaptor = ArgumentCaptor.forClass(String.class);
      verify(commentLogic).insertAppComment(eq(USER_ID), commentCaptor.capture());
      assertThat(commentCaptor.getValue()).contains("更新").doesNotContain("完了");
    }

    @Test
    void ステータス3への更新_COMPLETE_TASKコメントが記録される() {
      doReturn(USER_ID).when(mcpProperties).getUserId();
      doReturn(buildTaskDto(3L)).when(taskLogic).getTaskDto(TASK_ID, USER_ID);

      final var dto = TaskEditDto.builder() //
          .taskId(TASK_ID) //
          .statusId(3L) //
          .title("完了する") //
          .startDateTime(LocalDateTime.of(2026, 5, 22, 9, 0)) //
          .endDateTime(LocalDateTime.of(2026, 5, 22, 10, 0)) //
          .build();

      mcpTaskService.updateTask(dto);

      final var commentCaptor = ArgumentCaptor.forClass(String.class);
      verify(commentLogic).insertAppComment(eq(USER_ID), commentCaptor.capture());
      assertThat(commentCaptor.getValue()).contains("完了");
    }

    @Test
    void 他ユーザのタスクならNotFoundExceptionが伝播し更新されない() {
      doReturn(USER_ID).when(mcpProperties).getUserId();
      doThrow(new NotFoundException(ErrorMessageEnum.NON_EXISTENT_DATA)) //
          .when(taskLogic).getTaskDto(TASK_ID, USER_ID);

      final var dto = TaskEditDto.builder() //
          .taskId(TASK_ID) //
          .statusId(1L) //
          .title("t") //
          .startDateTime(LocalDateTime.of(2026, 5, 20, 9, 0)) //
          .endDateTime(LocalDateTime.of(2026, 5, 20, 10, 0)) //
          .build();

      assertThatThrownBy(() -> mcpTaskService.updateTask(dto)) //
          .isInstanceOf(NotFoundException.class);
      verify(taskRepository, never()).update(any(), anyLong());
      verify(commentLogic, never()).insertAppComment(anyLong(), anyString());
    }

    @Test
    void 期間不正ならBadRequestException() {
      doReturn(USER_ID).when(mcpProperties).getUserId();
      doReturn(buildTaskDto(1L)).when(taskLogic).getTaskDto(TASK_ID, USER_ID);

      final var dto = TaskEditDto.builder() //
          .taskId(TASK_ID) //
          .statusId(1L) //
          .title("t") //
          .startDateTime(LocalDateTime.of(2026, 5, 20, 12, 0)) //
          .endDateTime(LocalDateTime.of(2026, 5, 20, 11, 0)) //
          .build();

      assertThatThrownBy(() -> mcpTaskService.updateTask(dto)) //
          .isInstanceOf(BadRequestException.class);
      verify(taskRepository, never()).update(any(), anyLong());
    }
  }

  @Nested
  class DeleteTask {

    @Test
    void verifyForDelete経由でTaskDtoを取得しdeleteConfirmedで削除とコメントを行う() {
      doReturn(USER_ID).when(mcpProperties).getUserId();
      final var taskDto = buildTaskDto(1L);
      doReturn(taskDto).when(taskLogic).getTaskDto(TASK_ID, USER_ID);
      doNothing().when(taskRepository).delete(any(Task.class));

      final var verified = mcpTaskService.verifyForDelete(TASK_ID);
      mcpTaskService.deleteConfirmed(verified);

      verify(taskRepository).delete(any(Task.class));
      final var commentCaptor = ArgumentCaptor.forClass(String.class);
      verify(commentLogic).insertAppComment(eq(USER_ID), commentCaptor.capture());
      assertThat(commentCaptor.getValue()).contains("削除");
    }
  }

  @Nested
  class SearchTasks {

    @Test
    void 検索条件にuserIdが必ず埋め込まれる() {
      doReturn(USER_ID).when(mcpProperties).getUserId();
      doReturn(List.of(buildTaskStatus(1L))).when(taskLogic).getTaskStatusDtoList();
      doReturn(List.<TaskDto>of()).when(taskLogic).getTaskDtoList(any(TaskSearchDto.class));
      doReturn(0L).when(taskRepository).selectCountBySearchCriteria(any(TaskSearchDto.class));

      mcpTaskService.searchTasks("kw", List.of(1L), null, null, null, null, null, 1L, 10L);

      final var captor = ArgumentCaptor.forClass(TaskSearchDto.class);
      verify(taskLogic).getTaskDtoList(captor.capture());
      assertThat(captor.getValue().getIncludeCreatedBy()).isEqualTo(USER_ID);
      assertThat(captor.getValue().getIncludeCount()).isEqualTo(10L);
      assertThat(captor.getValue().getIncludeStartPosition()).isEqualTo(1L);
      assertThat(captor.getValue().getIncludeTitle()).isEqualTo("kw");
    }

    @Test
    void 未知のstatusIdを指定するとBadRequestException() {
      doReturn(List.of(buildTaskStatus(1L), buildTaskStatus(2L))) //
          .when(taskLogic).getTaskStatusDtoList();

      assertThatThrownBy(() -> mcpTaskService.searchTasks( //
          null, List.of(999L), null, null, null, null, null, 1L, 10L)) //
          .isInstanceOf(BadRequestException.class);
      verify(taskLogic, never()).getTaskDtoList(any());
      verify(taskRepository, never()).selectCountBySearchCriteria(any());
    }

    @Test
    void excludedStatusIdsも検証対象() {
      doReturn(List.of(buildTaskStatus(1L))).when(taskLogic).getTaskStatusDtoList();

      assertThatThrownBy(() -> mcpTaskService.searchTasks( //
          null, null, List.of(42L), null, null, null, null, 1L, 10L)) //
          .isInstanceOf(BadRequestException.class);
    }
  }

  @Nested
  class ListStatuses {

    @Test
    void デフォルトはcolorCodeとisReadOnlyがnull() {
      doReturn(List.of(buildTaskStatus(1L))).when(taskLogic).getTaskStatusDtoList();

      final var actual = mcpTaskService.listStatuses(false, false);

      assertThat(actual).hasSize(1);
      assertThat(actual.get(0).statusId()).isEqualTo(1L);
      assertThat(actual.get(0).colorCode()).isNull();
      assertThat(actual.get(0).isReadOnly()).isNull();
    }

    @Test
    void includeフラグがtrueなら対応するフィールドが入る() {
      doReturn(List.of(buildTaskStatus(1L))).when(taskLogic).getTaskStatusDtoList();

      final var actual = mcpTaskService.listStatuses(true, true);

      assertThat(actual.get(0).colorCode()).isEqualTo("#fff");
      assertThat(actual.get(0).isReadOnly()).isFalse();
    }
  }

  private TaskDto buildTaskDto(final Long statusId) {
    return TaskDto.builder() //
        .taskId(TASK_ID) //
        .title("既存タスク") //
        .startDateTime(LocalDateTime.of(2026, 5, 20, 9, 0)) //
        .endDateTime(LocalDateTime.of(2026, 5, 20, 10, 0)) //
        .createdBy(USER_ID) //
        .taskStatusDto(buildTaskStatus(statusId)) //
        .build();
  }

  private TaskStatusDto buildTaskStatus(final Long statusId) {
    return TaskStatusDto.builder() //
        .statusId(statusId) //
        .name("status-" + statusId) //
        .colorCode("#fff") //
        .isReadOnly(false) //
        .remarks("remarks-" + statusId) //
        .build();
  }
}
