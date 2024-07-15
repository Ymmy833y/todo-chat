package org.ymmy.todo_chat.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.exception.BadRequestException;
import org.ymmy.todo_chat.logic.TaskLogic;
import org.ymmy.todo_chat.model.dto.TaskCompleteDto;
import org.ymmy.todo_chat.model.dto.TaskCreateDetailDto;
import org.ymmy.todo_chat.model.dto.TaskCreateDto;
import org.ymmy.todo_chat.model.dto.TaskDetailDto;
import org.ymmy.todo_chat.model.dto.TaskDto;
import org.ymmy.todo_chat.model.dto.TaskEditDto;
import org.ymmy.todo_chat.repository.TaskRepository;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

@Service
@RequiredArgsConstructor
public class TaskService {

  private final TaskLogic taskLogic;
  private final TaskRepository taskRepository;

  private final String DETAIL_MESSAGE_FORMAT = "このタスクは、%sです。";

  /**
   * タスク新規作成画面の詳細情報を取得します
   *
   * @return {@link TaskCreateDetailDto}
   */
  public TaskCreateDetailDto getTaskCreateDetailDto() {
    return new TaskCreateDetailDto();
  }

  /**
   * タスク詳細画面の詳細情報を取得します
   *
   * @param taskId タスクID
   * @param userId ユーザーID
   * @return {@link TaskDetailDto}
   */
  public TaskDetailDto getTaskDetailDto(final Long taskId, final Long userId) {
    final var taskDto = taskLogic.getTaskDto(taskId, userId);
    return TaskDetailDto.builder()
        .taskDto(taskDto) //
        .statusDtoList(taskLogic.getTaskStatusDtoList()) //
        .detailMessages(generateDetailMessages(taskDto)) //
        .build();
  }

  /**
   * タスクを登録します
   *
   * @param dto {@link TaskCreateDto}
   * @return 登録されたTaskのID
   */
  @Transactional
  public Long create(final TaskCreateDto dto, final Long userId) {
    verifyForCreate(dto);
    return taskRepository.insert(convertToTask(dto), userId);
  }

  /**
   * タスクを更新します
   *
   * @param dto    {@link TaskEditDto}
   * @param userId ユーザーID
   */
  @Transactional
  public void update(final TaskEditDto dto, final Long userId) {
    verifyForUpdate(dto, userId);

    taskRepository.update(convertToTask(dto), userId);
  }

  /**
   * タスクのステータスを更新します
   *
   * @param dto    {@link TaskCompleteDto}
   * @param userId ユーザーID
   */
  @Transactional
  public void updateStatus(final TaskCompleteDto dto, final Long userId) {
    verifyForUpdateStatus(dto, userId);

    taskRepository.update(convertToTask(dto), userId);
  }

  /**
   * タスク作成時の検証
   *
   * @param taskCreateDto {@link TaskCreateDto}
   */
  private void verifyForCreate(final TaskCreateDto taskCreateDto) {
    if (isInvalidPeriod(taskCreateDto.getStartDateTime(), taskCreateDto.getEndDateTime())) {
      throw new BadRequestException(ErrorMessageEnum.INVALID_PERIOD);
    }
  }

  /**
   * タスク更新時の検証
   *
   * @param dto    {@link TaskEditDto}
   * @param userId ユーザーID
   */
  private void verifyForUpdate(final TaskEditDto dto, final Long userId) {
    taskLogic.getTaskDto(dto.getTaskId(), userId); // 操作権限の検証

    if (isInvalidPeriod(dto.getStartDateTime(), dto.getEndDateTime())) {
      throw new BadRequestException(ErrorMessageEnum.INVALID_PERIOD);
    }
  }

  /**
   * タスクステータス更新時の検証
   *
   * @param dto    {@link TaskCompleteDto}
   * @param userId ユーザーID
   */
  private void verifyForUpdateStatus(final TaskCompleteDto dto, final Long userId) {
    taskLogic.getTaskDto(dto.getTaskId(), userId); // 操作権限の検証
  }

  /**
   * 登録された期間の検証
   *
   * @param startDateTime 開始日時
   * @param endDateTime   　終了日時
   * @return 開始日時が登録日時の後になっている場合true
   */
  private boolean isInvalidPeriod(final LocalDateTime startDateTime,
      final LocalDateTime endDateTime) {
    return startDateTime.isAfter(endDateTime);
  }

  /**
   * タスク詳細画面に表示するメッセージを生成します
   *
   * @param taskDto {@link TaskDto}
   * @return メッセージ
   */
  private Map<String, String> generateDetailMessages(final TaskDto taskDto) {
    final var taskStatus = taskDto.getTaskStatusDto();
    final var detailMessages = new HashMap<String, String>();

    if (taskStatus.getStatusId() == 3L) {
      detailMessages.put("success", String.format(DETAIL_MESSAGE_FORMAT, taskStatus.getRemarks()));
    } else {
      detailMessages.put("warning", String.format(DETAIL_MESSAGE_FORMAT, taskStatus.getRemarks()));
    }
    return detailMessages;
  }

  private Task convertToTask(final TaskCreateDto dto) {
    return new Task() //
        .withStatusId(1L) //
        .withTitle(dto.getTitle()) //
        .withStartDateTime(dto.getStartDateTime()) //
        .withEndDateTime(dto.getEndDateTime()) //
        .withDescription(dto.getDescription());
  }

  private Task convertToTask(final TaskEditDto dto) {
    return new Task() //
        .withId(dto.getTaskId()) //
        .withStatusId(dto.getStatusId()) //
        .withTitle(dto.getTitle()) //
        .withStartDateTime(dto.getStartDateTime()) //
        .withEndDateTime(dto.getEndDateTime()) //
        .withDescription(dto.getDescription());
  }

  private Task convertToTask(final TaskCompleteDto dto) {
    return new Task() //
        .withId(dto.getTaskId()) //
        .withStatusId(dto.getStatusId());
  }
}
