package org.ymmy.todo_chat.logic;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.ymmy.todo_chat.exception.NotFoundException;
import org.ymmy.todo_chat.model.dto.TaskDto;
import org.ymmy.todo_chat.model.dto.TaskSearchDto;
import org.ymmy.todo_chat.model.dto.TaskStatusDto;
import org.ymmy.todo_chat.model.entity.TaskEntity;
import org.ymmy.todo_chat.repository.TaskRepository;
import org.ymmy.todo_chat.repository.TaskStatusRepository;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

@Component
@RequiredArgsConstructor
public class TaskLogic {

  private final TaskRepository taskRepository;
  private final TaskStatusRepository taskStatusRepository;

  /**
   * 検索条件に一致するタスク一覧を取得する
   *
   * @param taskSearchDto {@link TaskSearchDto} 検索条件
   * @return タスク一覧
   */
  public List<TaskDto> getTaskDtoList(final TaskSearchDto taskSearchDto) {
    final var taskEntities = taskRepository.selectAllBySearchCriteria(taskSearchDto);
    return taskEntities.stream().map(this::convertToTaskDto).toList();
  }

  /**
   * 指定したタスクが存在する且つログインユーザーに権限がある場合タスクを取得する
   *
   * @param taskId タスクID
   * @param userId ユーザーID
   * @return {@link TaskDto}
   */
  public TaskDto getTaskDto(final Long taskId, final Long userId) {
    final var taskEntityOptional = taskRepository.selectEntityByTaskIdAndUserId(taskId, userId);
    if (taskEntityOptional.isEmpty()) {
      throw new NotFoundException(ErrorMessageEnum.NON_EXISTENT_DATA);
    }

    return convertToTaskDto(taskEntityOptional.get());
  }

  /**
   * 指定された日数後の終了日時を取得します。
   *
   * @param days 終了日時を計算するための基準となる日数
   * @return 終了日時
   */
  public LocalDateTime getEndDateTimeAfterDays(int days) {
    final var today = LocalDateTime.now();
    return today.plusDays(days) //
        .withHour(23) //
        .withMinute(59) //
        .withSecond(59) //
        .withNano(999999999);
  }

  /**
   * 指定された日数後の開始日時を取得します。
   *
   * @param days 開始日時を計算するための基準となる日数
   * @return 開始日時
   */
  public LocalDateTime getStartDateTimeAfterDays(int days) {
    final var today = LocalDateTime.now();
    return today.plusDays(days) //
        .withHour(0) //
        .withMinute(0) //
        .withSecond(0) //
        .withNano(0);
  }

  /**
   * 全てのタスクステータスを取得します
   *
   * @return {@link TaskStatusDto} のリスト
   */
  public List<TaskStatusDto> getTaskStatusDtoList() {
    return taskStatusRepository.selectAll().stream().map(TaskStatusDto::new).toList();
  }

  public TaskDto convertToTaskDto(final TaskEntity taskEntity) {
    return TaskDto.builder()
        .taskId(taskEntity.getTaskId()) //
        .taskStatusDto(new TaskStatusDto(taskEntity.getTaskStatus())) //
        .title(taskEntity.getTitle()) //
        .description(taskEntity.getDescription()) //
        .startDateTime(taskEntity.getStartDateTime()) //
        .endDateTime(taskEntity.getEndDateTime()) //
        .createdBy(taskEntity.getCreatedBy()) //
        .build();
  }
}
