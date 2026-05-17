package org.ymmy.todo_chat.mcp.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ymmy.todo_chat.db.entity.Task;
import org.ymmy.todo_chat.exception.BadRequestException;
import org.ymmy.todo_chat.logic.CommentLogic;
import org.ymmy.todo_chat.logic.TaskLogic;
import org.ymmy.todo_chat.mcp.config.McpProperties;
import org.ymmy.todo_chat.mcp.tool.dto.TaskDetail;
import org.ymmy.todo_chat.mcp.tool.dto.TaskStatusSummary;
import org.ymmy.todo_chat.mcp.tool.dto.TaskSummary;
import org.ymmy.todo_chat.model.dto.TaskCreateDto;
import org.ymmy.todo_chat.model.dto.TaskDto;
import org.ymmy.todo_chat.model.dto.TaskEditDto;
import org.ymmy.todo_chat.model.dto.TaskSearchDto;
import org.ymmy.todo_chat.repository.TaskRepository;
import org.ymmy.todo_chat.util.CommentMessageEnum;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

/**
 * MCP @Tool から呼び出されるタスク操作サービス。
 * <p>
 * webapp 側 {@code TaskService} と同一の処理手順 (検証 → DB 更新 → アプリ自動コメント) を、
 * セッション非依存の {@link McpProperties#getUserId()} で動作するよう再実装している。
 */
@Service
@RequiredArgsConstructor
public class McpTaskService {

  private static final Long COMPLETE_STATUS = 3L;
  private static final Long DEFAULT_STATUS_ON_CREATE = 1L;
  private static final DateTimeFormatter COMMENT_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd");

  private final TaskRepository taskRepository;
  private final TaskLogic taskLogic;
  private final CommentLogic commentLogic;
  private final McpProperties mcpProperties;

  /** 検索結果。TaskTools 側で {@code CallToolResult} のメッセージ組立に用いる。 */
  public record SearchResult(Long totalCount, List<TaskSummary> tasks) {

  }

  /**
   * 検索条件に一致するタスクと、その条件における総件数を取得する。
   */
  public SearchResult searchTasks(
      final String title,
      final List<Long> includeStatusIds,
      final List<Long> excludedStatusIds,
      final LocalDateTime startDateTimeFrom,
      final LocalDateTime startDateTimeTo,
      final LocalDateTime endDateTimeFrom,
      final LocalDateTime endDateTimeTo,
      final Long page,
      final Long pageSize) {

    validateStatusIds(includeStatusIds, excludedStatusIds);

    // includeCount と includeStartPosition は TaskCustomMapper.xml の LIMIT 計算で参照される。
    // includeStartPosition は 1 始まりのページ番号。@Tool 側でデフォルトを補完済み前提。
    final var searchDto = TaskSearchDto.builder() //
        .includeCreatedBy(mcpProperties.getUserId()) //
        .includeTitle(title) //
        .includeStatus(includeStatusIds == null ? List.of() : includeStatusIds) //
        .excludedStatus(excludedStatusIds == null ? List.of() : excludedStatusIds) //
        .startDateTimeFrom(startDateTimeFrom) //
        .startDateTimeTo(startDateTimeTo) //
        .endDateTimeFrom(endDateTimeFrom) //
        .endDateTimeTo(endDateTimeTo) //
        .includeCount(pageSize) //
        .includeStartPosition(page) //
        .build();

    final var taskDtoList = taskLogic.getTaskDtoList(searchDto);
    final var totalCount = taskRepository.selectCountBySearchCriteria(searchDto);
    final var summaries = taskDtoList.stream().map(McpTaskService::toSummary).toList();
    return new SearchResult(totalCount, summaries);
  }

  /**
   * タスク詳細を取得する。所有権チェックは {@link TaskLogic#getTaskDto(Long, Long)} に委譲。
   */
  public TaskDetail getTaskDetail(final Long taskId) {
    final var dto = taskLogic.getTaskDto(taskId, mcpProperties.getUserId());
    return toDetail(dto);
  }

  /**
   * タスクを登録する。期間バリデーション失敗時は {@link BadRequestException} を送出する。
   */
  @Transactional
  public Long createTask(final TaskCreateDto dto) {
    if (isInvalidPeriod(dto.getStartDateTime(), dto.getEndDateTime())) {
      throw new BadRequestException(ErrorMessageEnum.INVALID_PERIOD);
    }

    final var userId = mcpProperties.getUserId();
    final var task = new Task() //
        .withStatusId(DEFAULT_STATUS_ON_CREATE) //
        .withTitle(dto.getTitle()) //
        .withStartDateTime(dto.getStartDateTime()) //
        .withEndDateTime(dto.getEndDateTime()) //
        .withDescription(dto.getDescription());
    final var taskId = taskRepository.insert(task, userId);

    insertTaskComment(CommentMessageEnum.CREATE_TASK, dto.getTitle(), dto.getStartDateTime(),
        userId);
    return taskId;
  }

  /**
   * タスクを更新する。所有権・期間バリデーションを行い、ステータスが完了 (3) なら完了コメントを、
   * それ以外なら更新コメントを記録する。
   */
  @Transactional
  public TaskDetail updateTask(final TaskEditDto dto) {
    final var userId = mcpProperties.getUserId();
    taskLogic.getTaskDto(dto.getTaskId(), userId); // 所有権検証 (見つからなければ NotFoundException)
    if (isInvalidPeriod(dto.getStartDateTime(), dto.getEndDateTime())) {
      throw new BadRequestException(ErrorMessageEnum.INVALID_PERIOD);
    }

    final var task = new Task() //
        .withId(dto.getTaskId()) //
        .withStatusId(dto.getStatusId()) //
        .withTitle(dto.getTitle()) //
        .withStartDateTime(dto.getStartDateTime()) //
        .withEndDateTime(dto.getEndDateTime()) //
        .withDescription(dto.getDescription());
    taskRepository.update(task, userId);

    final var messageEnum = COMPLETE_STATUS.equals(dto.getStatusId())
        ? CommentMessageEnum.COMPLETE_TASK : CommentMessageEnum.UPDATE_TASK;
    insertTaskComment(messageEnum, dto.getTitle(), dto.getStartDateTime(), userId);

    return toDetail(taskLogic.getTaskDto(dto.getTaskId(), userId));
  }

  /**
   * 削除前の所有権検証。削除確認 (Elicitation) 前に呼び、コメント生成用の DTO を返す。
   */
  public TaskDto verifyForDelete(final Long taskId) {
    return taskLogic.getTaskDto(taskId, mcpProperties.getUserId());
  }

  /**
   * 削除を確定する。Elicitation でユーザに承認された後にのみ呼び出される想定。
   */
  @Transactional
  public void deleteConfirmed(final TaskDto taskDto) {
    final var userId = mcpProperties.getUserId();
    taskRepository.delete(new Task().withId(taskDto.getTaskId()));
    insertTaskComment(CommentMessageEnum.DELETE_TASK, taskDto.getTitle(),
        taskDto.getStartDateTime(), userId);
  }

  /**
   * 全タスクステータス一覧を返す。{@code includeColorCode} / {@code includeIsReadOnly} は
   * デフォルト不要のフィールドを opt-in で含めるためのフラグ。
   */
  public List<TaskStatusSummary> listStatuses(final boolean includeColorCode,
      final boolean includeIsReadOnly) {
    return taskLogic.getTaskStatusDtoList().stream() //
        .map(s -> new TaskStatusSummary( //
            s.getStatusId(), //
            s.getName(), //
            s.getRemarks(), //
            includeColorCode ? s.getColorCode() : null, //
            includeIsReadOnly ? s.isReadOnly() : null)) //
        .toList();
  }

  /**
   * 指定されたステータスID が DB に存在するかを検証する。未知の ID が含まれていれば
   * {@link BadRequestException} を送出する。
   */
  private void validateStatusIds(final List<Long> includeStatusIds,
      final List<Long> excludedStatusIds) {
    final var requested = new HashSet<Long>();
    if (includeStatusIds != null) {
      requested.addAll(includeStatusIds);
    }
    if (excludedStatusIds != null) {
      requested.addAll(excludedStatusIds);
    }
    if (requested.isEmpty()) {
      return;
    }
    final Set<Long> known = new HashSet<>();
    taskLogic.getTaskStatusDtoList().forEach(s -> known.add(s.getStatusId()));
    if (!known.containsAll(requested)) {
      throw new BadRequestException(ErrorMessageEnum.NON_EXISTENT_STATUS);
    }
  }

  private boolean isInvalidPeriod(final LocalDateTime startDateTime,
      final LocalDateTime endDateTime) {
    return startDateTime.isAfter(endDateTime);
  }

  private void insertTaskComment(final CommentMessageEnum messageEnum, final String title,
      final LocalDateTime startDateTime, final Long userId) {
    final var date = startDateTime.format(COMMENT_DATE_FORMAT);
    final var message = String.format(messageEnum.getMessage(), title, date);
    commentLogic.insertAppComment(userId, message);
  }

  private static TaskSummary toSummary(final TaskDto dto) {
    final var status = dto.getTaskStatusDto();
    return new TaskSummary( //
        dto.getTaskId(), //
        dto.getTitle(), //
        status == null ? null : status.getStatusId(), //
        status == null ? null : status.getName(), //
        dto.getStartDateTime(), //
        dto.getEndDateTime());
  }

  private static TaskDetail toDetail(final TaskDto dto) {
    final var status = dto.getTaskStatusDto();
    return new TaskDetail( //
        dto.getTaskId(), //
        dto.getTitle(), //
        dto.getDescription(), //
        status == null ? null : status.getStatusId(), //
        status == null ? null : status.getName(), //
        dto.getStartDateTime(), //
        dto.getEndDateTime());
  }
}
