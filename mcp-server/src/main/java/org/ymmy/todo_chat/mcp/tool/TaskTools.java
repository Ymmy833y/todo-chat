package org.ymmy.todo_chat.mcp.tool;

import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ElicitResult.Action;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.stereotype.Component;
import org.ymmy.todo_chat.exception.BadRequestException;
import org.ymmy.todo_chat.mcp.service.McpTaskService;
import org.ymmy.todo_chat.util.ErrorMessageEnum;
import org.ymmy.todo_chat.mcp.tool.dto.DeleteTaskResult;
import org.ymmy.todo_chat.mcp.tool.dto.TaskDetail;
import org.ymmy.todo_chat.mcp.tool.dto.TaskStatusSummary;
import org.ymmy.todo_chat.model.dto.TaskCreateDto;
import org.ymmy.todo_chat.model.dto.TaskEditDto;

/**
 * MCP @McpTool エンドポイント。{@link McpTaskService} に処理を委譲し、
 * {@code search_tasks} / {@code create_task} は {@link CallToolResult} を組み立て、
 * {@code delete_task} は Elicitation でユーザに削除確認を求める。
 */
@Component
@RequiredArgsConstructor
public class TaskTools {

  private final McpTaskService mcpTaskService;

  @McpTool(name = "search_tasks", description = """
      現在のユーザに紐づくタスクを条件検索します。タイトル部分一致、含めるステータスID群、
      除外するステータスID群、開始/終了日時の範囲でフィルタでき、ページネーション可能です。
      ステータスIDが未知の場合はエラーになるので、未知の場合は list_task_statuses を先に呼んでください。
      日時は ISO-8601 形式 (例: 2026-05-20T09:00:00) で指定してください。""")
  public CallToolResult searchTasks(
      @McpToolParam(description = "タイトル部分一致 (LIKE)", required = false) final String title,
      @McpToolParam(description = "含めるステータスID (例: [1, 2])", required = false) final List<Long> includeStatusIds,
      @McpToolParam(description = "除外するステータスID", required = false) final List<Long> excludedStatusIds,
      @McpToolParam(description = "開始日時の下限 (ISO-8601)", required = false) final LocalDateTime startDateTimeFrom,
      @McpToolParam(description = "開始日時の上限 (ISO-8601)", required = false) final LocalDateTime startDateTimeTo,
      @McpToolParam(description = "終了日時の下限 (ISO-8601)", required = false) final LocalDateTime endDateTimeFrom,
      @McpToolParam(description = "終了日時の上限 (ISO-8601)", required = false) final LocalDateTime endDateTimeTo,
      @McpToolParam(description = "ページ番号 (1 始まり、未指定なら 1)", required = false) final Long page,
      @McpToolParam(description = "1 ページの件数 (未指定なら 10)", required = false) final Long pageSize) {

    final var actualPage = page == null ? 1L : page;
    final var actualPageSize = pageSize == null ? 10L : pageSize;

    final var result = mcpTaskService.searchTasks(title, includeStatusIds, excludedStatusIds,
        startDateTimeFrom, startDateTimeTo, endDateTimeFrom, endDateTimeTo, actualPage,
        actualPageSize);

    final var message = buildSearchMessage(result.totalCount(), result.tasks().size(), actualPage,
        actualPageSize);

    return CallToolResult.builder() //
        .addTextContent(message) //
        .structuredContent(Map.of("tasks", result.tasks())) //
        .build();
  }

  @McpTool(name = "get_task", description = "タスクIDを指定して、タスクの詳細 (description 含む) を取得します。")
  public TaskDetail getTask(
      @McpToolParam(description = "取得するタスクのID") final Long taskId) {
    return mcpTaskService.getTaskDetail(taskId);
  }

  @McpTool(name = "create_task", description = """
      新しいタスクを登録します。開始日時は終了日時より前である必要があります。日時は ISO-8601 形式
      (例: 2026-05-20T09:00:00) で指定してください。""")
  public CallToolResult createTask(
      @McpToolParam(description = "タスクのタイトル") final String title,
      @McpToolParam(description = "開始日時 (ISO-8601)") final LocalDateTime startDateTime,
      @McpToolParam(description = "終了日時 (ISO-8601)") final LocalDateTime endDateTime,
      @McpToolParam(description = "タスクの説明", required = false) final String description) {

    final var dto = TaskCreateDto.builder() //
        .title(title) //
        .startDateTime(startDateTime) //
        .endDateTime(endDateTime) //
        .description(description) //
        .build();
    final var taskId = mcpTaskService.createTask(dto);

    final var message = String.format(
        "タスク「%s」を新規作成しました (ID: %d、開始: %s、終了: %s)。", //
        title, taskId, startDateTime, endDateTime);

    return CallToolResult.builder() //
        .addTextContent(message) //
        .structuredContent(Map.of("taskId", taskId)) //
        .build();
  }

  @McpTool(name = "update_task", description = """
      既存のタスクを更新します。statusId に 3 を指定すると完了扱いとなり、完了コメントが記録されます。
      他ユーザのタスクは更新できません。""")
  public TaskDetail updateTask(
      @McpToolParam(description = "更新対象のタスクID") final Long taskId,
      @McpToolParam(description = "ステータスID (3=完了)") final Long statusId,
      @McpToolParam(description = "タスクのタイトル") final String title,
      @McpToolParam(description = "開始日時 (ISO-8601)") final LocalDateTime startDateTime,
      @McpToolParam(description = "終了日時 (ISO-8601)") final LocalDateTime endDateTime,
      @McpToolParam(description = "タスクの説明", required = false) final String description) {

    final var dto = TaskEditDto.builder() //
        .taskId(taskId) //
        .statusId(statusId) //
        .title(title) //
        .startDateTime(startDateTime) //
        .endDateTime(endDateTime) //
        .description(description) //
        .build();
    return mcpTaskService.updateTask(dto);
  }

  public record ApprovalInput(boolean confirmed) {}

  @McpTool(name = "delete_task", description = """
      タスクを削除します。実行前にユーザへ削除確認 (Elicitation) を求め、承認された場合のみ削除します。
      ユーザに拒否された場合は deleted=false が返ります。""")
  public DeleteTaskResult deleteTask(
      final McpSyncRequestContext context,
      @McpToolParam(description = "削除対象のタスクID") final Long taskId) {

    if (!context.elicitEnabled()) {
      throw new BadRequestException(ErrorMessageEnum.ELICITATION_NOT_SUPPORTED);
    }

    final var taskDto = mcpTaskService.verifyForDelete(taskId);

    final var elicitResult = context.elicit(
        spec -> spec.message(String.format("タスク「%s」を削除します。よろしいですか?", taskDto.getTitle())),
        ApprovalInput.class
    );

    if (elicitResult.action() != Action.ACCEPT || !elicitResult.structuredContent().confirmed()) {
      return new DeleteTaskResult(taskId, false, "user_declined");
    }

    mcpTaskService.deleteConfirmed(taskDto);
    return new DeleteTaskResult(taskId, true, null);
  }

  @McpTool(name = "list_task_statuses", description = """
      タスクのステータス一覧を返します。デフォルトでは statusId / name / remarks のみ返します。
      colorCode や isReadOnly を含めたい場合は対応するフラグを true にしてください。""")
  public List<TaskStatusSummary> listTaskStatuses(
      @McpToolParam(description = "true にすると colorCode を含める", required = false) final Boolean includeColorCode,
      @McpToolParam(description = "true にすると isReadOnly を含める", required = false) final Boolean includeIsReadOnly) {
    return mcpTaskService.listStatuses( //
        Boolean.TRUE.equals(includeColorCode), //
        Boolean.TRUE.equals(includeIsReadOnly));
  }

  /** 検索結果の自然言語メッセージを組み立てる。次ページがある場合は呼び出し方を案内する。 */
  private static String buildSearchMessage(final Long totalCount, final int returnedCount,
      final Long page, final Long pageSize) {
    if (totalCount == null || totalCount == 0L) {
      return "条件に一致するタスクはありませんでした。";
    }
    final long from = (page - 1) * pageSize + 1;
    final long to = from + returnedCount - 1;
    final long totalPages = (totalCount + pageSize - 1) / pageSize;
    final var base = String.format("%d 件のタスクが見つかりました (全 %d 件中 %d〜%d 件目を表示)。",
        returnedCount, totalCount, from, to);
    if (page < totalPages) {
      return base + String.format(" 次のページを取得するには search_tasks を page=%d, pageSize=%d で呼び出してください。",
          page + 1, pageSize);
    }
    return base + " すべて表示済みです。";
  }
}
