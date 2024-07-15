package org.ymmy.todo_chat.model.form;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.ymmy.todo_chat.model.dto.TaskSearchDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class TaskSearchForm {

  /* 取得対象のステータス */
  private List<Long> includeStatus = new ArrayList<>();
  /* 取得対象外のステータス */
  private List<Long> excludedStatus = new ArrayList<>();
  /* 取得対象の開始日時の開始日時 */
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime startDateTimeFrom;
  /* 取得対象の開始日時の終了日時 */
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime startDateTimeTo;
  /* 取得対象の終了日時の開始日時 */
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime endDateTimeFrom;
  /* 取得対象の終了日時の開始日時 */
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime endDateTimeTo;
  /* 取得対象のタイトル */
  private String includeTitle;
  /* 取得対象の件数 */
  private Long includeCount = 10L;
  /* 取得対象の開始位置 */
  private Long includeStartPosition = 1L;

  public TaskSearchDto convertToDto() {
    return TaskSearchDto.builder() //
        .includeStatus(includeStatus) //
        .excludedStatus(excludedStatus) //
        .startDateTimeFrom(startDateTimeFrom) //
        .startDateTimeTo(startDateTimeTo) //
        .endDateTimeFrom(endDateTimeFrom) //
        .endDateTimeTo(endDateTimeTo) //
        .includeTitle(includeTitle) //
        .includeCount(includeCount) //
        .includeStartPosition(includeStartPosition) //
        .build();
  }

  public TaskSearchDto convertToDto(final Long userId) {
    return TaskSearchDto.builder() //
        .includeStatus(includeStatus) //
        .excludedStatus(excludedStatus) //
        .startDateTimeFrom(startDateTimeFrom) //
        .startDateTimeTo(startDateTimeTo) //
        .endDateTimeFrom(endDateTimeFrom) //
        .endDateTimeTo(endDateTimeTo) //
        .includeTitle(includeTitle) //
        .includeCount(includeCount) //
        .includeStartPosition(includeStartPosition) //
        .includeCreatedBy(userId) //
        .build();
  }

  public TaskSearchForm(final TaskSearchForm searchForm) {
    includeStatus = searchForm.getIncludeStatus();
    excludedStatus = searchForm.getExcludedStatus();
    startDateTimeFrom = searchForm.getStartDateTimeFrom();
    startDateTimeTo = searchForm.getStartDateTimeTo();
    endDateTimeFrom = searchForm.getEndDateTimeFrom();
    endDateTimeTo = searchForm.getEndDateTimeTo();
    includeTitle = searchForm.getIncludeTitle();
    includeCount = searchForm.getIncludeCount();
    includeStartPosition = 1L;
  }
}
