package org.ymmy.todo_chat.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DateTool {

  final DateTimeFormatter formatterWithTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
  final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

  @Tool("Get the current date and time")
  String getCurrentDateTime() {
    return LocalDateTime.now().format(formatterWithTime);
  }

  @Tool("Retrieve the date of a specified day of the week within the week that includes the given date")
  String getDayOfWeekForGivenDate(
      @P(value = "Reference date", required = false) String date,
      @P("day of week") DayOfWeek dayOfWeek
  ) {
    final var currentDateTime = date.isEmpty() ? LocalDateTime.now() : convertToLocalDateTime(date);
    LocalDateTime nextWeekday = currentDateTime.with(ChronoUnit.DAYS.addTo(currentDateTime,
        (dayOfWeek.getValue() - currentDateTime.getDayOfWeek().getValue() + 7) % 7));
    return nextWeekday.format(formatterWithTime);
  }

  public LocalDateTime convertToLocalDateTime(final String localDateTime) {
    try {
      return LocalDateTime.parse(localDateTime);
    } catch (DateTimeParseException e) {
      return switch (localDateTime.length()) {
        case 10 -> LocalDateTime.parse(localDateTime + " 00:00", formatter);
        case 16 -> LocalDateTime.parse(localDateTime, formatterWithTime);
        default -> throw new DateTimeParseException("Unsupported date format: " + localDateTime,
            localDateTime, 0);
      };
    }
  }
}
