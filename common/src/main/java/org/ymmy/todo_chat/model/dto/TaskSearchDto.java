package org.ymmy.todo_chat.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskSearchDto implements Serializable {

  /* 取得対象のステータス */
  @Builder.Default
  private List<Long> includeStatus = new ArrayList<>();
  /* 取得対象外のステータス */
  @Builder.Default
  private List<Long> excludedStatus = new ArrayList<>();
  /* 取得対象の開始日時の開始日時 */
  private LocalDateTime startDateTimeFrom;
  /* 取得対象の開始日時の終了日時 */
  private LocalDateTime startDateTimeTo;
  /* 取得対象の終了日時の開始日時 */
  private LocalDateTime endDateTimeFrom;
  /* 取得対象の終了日時の開始日時 */
  private LocalDateTime endDateTimeTo;
  /* 取得対象のタイトル */
  private String includeTitle;
  /* 取得対象の件数 */
  private Long includeCount;
  /* 取得対象の開始位置 */
  private Long includeStartPosition;
  /* 取得対象のユーザーID */
  private Long includeCreatedBy;

}
