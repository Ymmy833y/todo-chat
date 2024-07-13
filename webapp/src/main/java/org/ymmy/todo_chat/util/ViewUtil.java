package org.ymmy.todo_chat.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ViewUtil {

  @Value("${app.version}")
  private String appVersion;
  
  public String getVersion() {
    return appVersion;
  }

  public String formatDate(LocalDateTime dateTime) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");
    return dateTime.format(formatter);
  }

}
