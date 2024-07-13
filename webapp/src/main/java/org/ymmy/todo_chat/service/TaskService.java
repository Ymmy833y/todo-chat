package org.ymmy.todo_chat.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.exception.BadRequestException;
import org.ymmy.todo_chat.model.dto.TaskCreateDetailDto;
import org.ymmy.todo_chat.model.dto.TaskCreateDto;
import org.ymmy.todo_chat.repository.TaskRepository;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

@Service
@RequiredArgsConstructor
public class TaskService {

  private final TaskRepository taskRepository;

  /**
   * タスク新規作成画面の詳細情報を取得します
   *
   * @return {@link TaskCreateDetailDto}
   */
  public TaskCreateDetailDto getTaskCreateDetailDto() {
    return new TaskCreateDetailDto();
  }

  /**
   * タスクをDB登録します
   *
   * @param dto {@link TaskCreateDto}
   * @return 登録されたTaskのID
   */
  @Transactional
  public Long create(final TaskCreateDto dto) {
    verifyForCreate(dto);
    return taskRepository.insert(convertToTask(dto));
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

  private Task convertToTask(final TaskCreateDto dto) {
    return new Task() //
        .withStatusId(1L) //
        .withTitle(dto.getTitle()) //
        .withStartDateTime(dto.getStartDateTime()) //
        .withEndDateTime(dto.getEndDateTime()) //
        .withDescription(dto.getDescription()) //
        .withCreatedBy(1L);
  }
}
