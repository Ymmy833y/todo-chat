package org.ymmy.todo_chat.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.exception.BadRequestException;
import org.ymmy.todo_chat.logic.CommentLogic;
import org.ymmy.todo_chat.logic.LoginUserLogic;
import org.ymmy.todo_chat.logic.TaskLogic;
import org.ymmy.todo_chat.model.dto.CommentDto;
import org.ymmy.todo_chat.model.dto.PaginationDto;
import org.ymmy.todo_chat.model.dto.TaskCompleteDto;
import org.ymmy.todo_chat.model.dto.TaskCreateDetailDto;
import org.ymmy.todo_chat.model.dto.TaskCreateDto;
import org.ymmy.todo_chat.model.dto.TaskDetailDto;
import org.ymmy.todo_chat.model.dto.TaskDto;
import org.ymmy.todo_chat.model.dto.TaskEditDto;
import org.ymmy.todo_chat.model.dto.TaskListDto;
import org.ymmy.todo_chat.model.form.TaskSearchForm;
import org.ymmy.todo_chat.repository.TaskRepository;
import org.ymmy.todo_chat.util.CommentMessageEnum;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

@Service
@RequiredArgsConstructor
public class TaskService {

  private final CommentService commentService;

  private final TaskLogic taskLogic;
  private final CommentLogic commentLogic;
  private final LoginUserLogic loginUserLogic;
  private final TaskRepository taskRepository;

  private final String DETAIL_MESSAGE_FORMAT = "このタスクは、%sです。";
  private final Long COMPLETE_STATUS = 3L;

  /**
   * タスク一覧画面の詳細情報を取得します
   *
   * @param searchForm 検索条件 {@link TaskSearchForm}
   * @return
   */
  public TaskListDto getTaskListDto(final TaskSearchForm searchForm) {
    final var userId = loginUserLogic.getUserDetails().getUserId();
    final var searchDto = searchForm.convertToDto(userId);

    final var taskDto = taskLogic.getTaskDtoList(searchDto);
    final var taskCount = taskRepository.selectCountBySearchCriteria(searchDto);

    return TaskListDto.builder() //
        .taskDtoList(taskDto) //
        .paginationDto(generatePaginationDto(taskCount, searchDto.getIncludeCount(),
            searchDto.getIncludeStartPosition())) //
        .statusDtoList(taskLogic.getTaskStatusDtoList()) //
        .commentDetailDto(commentLogic.getCommentDetailDto(userId)) //
        .build();
  }

  /**
   * タスク新規作成画面の詳細情報を取得します
   *
   * @return {@link TaskCreateDetailDto}
   */
  public TaskCreateDetailDto getTaskCreateDetailDto() {
    final var userId = loginUserLogic.getUserDetails().getUserId();
    return TaskCreateDetailDto.builder() //
        .commentDetailDto(commentLogic.getCommentDetailDto(userId)) //
        .build();
  }

  /**
   * タスク詳細画面の詳細情報を取得します
   *
   * @param taskId タスクID
   * @return {@link TaskDetailDto}
   */
  public TaskDetailDto getTaskDetailDto(final Long taskId) {
    final var userId = loginUserLogic.getUserDetails().getUserId();
    final var taskDto = taskLogic.getTaskDto(taskId, userId);
    return TaskDetailDto.builder()
        .taskDto(taskDto) //
        .statusDtoList(taskLogic.getTaskStatusDtoList()) //
        .detailMessages(generateDetailMessages(taskDto)) //
        .commentDetailDto(commentLogic.getCommentDetailDto(userId)) //
        .build();
  }

  /**
   * タスクを登録します
   *
   * @param dto {@link TaskCreateDto}
   * @return 登録されたTaskのID
   */
  @Transactional
  public Long create(final TaskCreateDto dto) {
    final var userId = loginUserLogic.getUserDetails().getUserId();
    verifyForCreate(dto);
    final Long taskId = taskRepository.insert(convertToTask(dto), userId);

    // コメントを登録します
    commentService.saveAndSendAppGeneratedComment(generateTaskComment(
        CommentMessageEnum.CREATE_TASK, dto.getTitle(), dto.getStartDateTime(), userId));
    return taskId;
  }

  /**
   * タスクを更新します
   *
   * @param dto {@link TaskEditDto}
   */
  @Transactional
  public void update(final TaskEditDto dto) {
    final var userId = loginUserLogic.getUserDetails().getUserId();
    verifyForUpdate(dto, userId);

    taskRepository.update(convertToTask(dto), userId);

    final var messageEnum = COMPLETE_STATUS.equals(dto.getStatusId()) ?
        CommentMessageEnum.COMPLETE_TASK : CommentMessageEnum.UPDATE_TASK;
    // コメントを登録します
    commentService.saveAndSendAppGeneratedComment(generateTaskComment(
        messageEnum, dto.getTitle(), dto.getStartDateTime(), userId));
  }

  /**
   * タスクのステータスを更新します
   *
   * @param dto {@link TaskCompleteDto}
   */
  @Transactional
  public void updateStatus(final TaskCompleteDto dto) {
    final var userId = loginUserLogic.getUserDetails().getUserId();
    final var taskDto = verifyForUpdateStatus(dto, userId);

    taskRepository.update(convertToTask(dto), userId);

    // コメントを登録します
    commentService.saveAndSendAppGeneratedComment(generateTaskComment(
        CommentMessageEnum.COMPLETE_TASK, taskDto.getTitle(), taskDto.getStartDateTime(), userId));
  }

  /**
   * タスクを削除します
   *
   * @param dto {@link TaskEditDto}
   */
  @Transactional
  public void delete(final TaskEditDto dto) {
    final var userId = loginUserLogic.getUserDetails().getUserId();
    final var taskDto = verifyForDelete(dto, userId);

    taskRepository.delete(convertToTask(dto));

    // コメントを登録します
    commentService.saveAndSendAppGeneratedComment(generateTaskComment(
        CommentMessageEnum.DELETE_TASK, taskDto.getTitle(), taskDto.getStartDateTime(), userId));
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
  private TaskDto verifyForUpdateStatus(final TaskCompleteDto dto, final Long userId) {
    return taskLogic.getTaskDto(dto.getTaskId(), userId); // 操作権限の検証
  }

  /**
   * タスク削除時の検証
   *
   * @param dto    {@link TaskEditDto}
   * @param userId ユーザーID
   */
  private TaskDto verifyForDelete(final TaskEditDto dto, final Long userId) {
    return taskLogic.getTaskDto(dto.getTaskId(), userId); // 操作権限の検証
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

  /**
   * タスクの処理に纏わるコメントを生成します
   *
   * @param messageEnum   生成するコメントのテンプレート
   * @param title         タスクのタイトル
   * @param startDateTime タスクの開始日時
   * @param userId        ユーザーID
   * @return {@link CommentDto}
   */
  private CommentDto generateTaskComment(final CommentMessageEnum messageEnum, final String title,
      final LocalDateTime startDateTime, final Long userId) {
    final var date = startDateTime.format(DateTimeFormatter.ofPattern("MM/dd"));
    return CommentDto.builder()
        .comment(String.format(messageEnum.getMessage(), title, date)) //
        .threadId(userId) //
        .build();
  }

  /**
   * ページネーションに表示するDTOを作成する
   *
   * @param total   タスクの合計
   * @param split   画面表示する件数
   * @param current 表示位置
   * @return {@link PaginationDto}
   */
  @VisibleForTesting
  PaginationDto generatePaginationDto(final Long total, final Long split,
      final Long current) {
    final var pageList = new HashMap<Long, Boolean>();

    final var totalPages = (total + split - 1) / split;
    final var startPage = Math.max(1, current - 1);
    final var endPage = Math.min(totalPages, current + 1);

    // ページリストを生成
    for (long i = startPage; i <= endPage; i++) {
      pageList.put(i, i == current);
    }

    // 表示するリストページがない場合はここで返却
    if (pageList.isEmpty()) {
      return PaginationDto.builder().build();
    }

    // 最初と最後のページを追加
    if (!pageList.containsKey(1L)) {
      pageList.put(1L, false);
    }
    if (!pageList.containsKey(totalPages)) {
      pageList.put(totalPages, false);
    }
    return PaginationDto.builder() //
        .currentPage(current) //
        .pageList(pageList) //
        .build();
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
