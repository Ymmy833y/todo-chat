package org.ymmy.todo_chat.model.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationDto implements Serializable {

  private Long currentPage;
  @Builder.Default
  private Map<Long, Boolean> pageList = new HashMap<>();

}
