package org.ymmy.todo_chat.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ymmy.todo_chat.logic.TaskLogic;
import org.ymmy.todo_chat.model.dto.HomeDto;
import org.ymmy.todo_chat.model.dto.TaskDto;
import org.ymmy.todo_chat.model.dto.TaskSearchDto;

@Service
@RequiredArgsConstructor
public class HomeService {

  private final TaskLogic taskLogic;

  private final Long COMPLETED_TASK_STATUS = 3L;

  public HomeDto getHomeDto(final Long userId) {
    return HomeDto.builder() //
        .todayTaskList(getTodayTasks(userId)) //
        .dueInAWeekTaskList(getDueInAWeekTasks(userId)) //
        .build();
  }

  /**
   * 指定したユーザーの本日が終了期限となるタスク一覧を取得する
   *
   * @param userId ユーザーID
   * @return タスク一覧
   */
  public List<TaskDto> getTodayTasks(final Long userId) {
    final var searchDto = TaskSearchDto.builder() //
        .endDateTimeFrom(taskLogic.getStartDateTimeAfterDays(0)) //
        .endDateTimeTo(taskLogic.getEndDateTimeAfterDays(0)) //
        .includeCreatedBy(userId) //
        .build();
    return taskLogic.getTaskDtoList(searchDto);
  }

  /**
   * 本日+7日後が終了期限となるタスク一覧を取得する ただし、ステータスが完了となっているタスクは除外する
   *
   * @param userId ユーザーID
   * @return タスク一覧
   */
  public List<TaskDto> getDueInAWeekTasks(final Long userId) {
    final var searchDto = TaskSearchDto.builder() //
        .excludedStatus(List.of(COMPLETED_TASK_STATUS)) //
        .endDateTimeFrom(taskLogic.getStartDateTimeAfterDays(0)) //
        .endDateTimeTo(taskLogic.getEndDateTimeAfterDays(7)) //
        .includeCreatedBy(userId) //
        .build();
    return taskLogic.getTaskDtoList(searchDto);
  }
}
