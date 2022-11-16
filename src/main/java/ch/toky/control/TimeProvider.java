package ch.toky.control;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TimeProvider {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("YYYYMMdd_HHmmss");

  private static final DateTimeFormatter PRETTY_DATE_TIME_FORMAT =
      DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm");

  public LocalDateTime now() {
    return LocalDateTime.now();
  }

  public String nowForFileName() {
    return now().format(FORMATTER);
  }

  public String formatToPretty(LocalDateTime localDateTime) {
    return localDateTime.format(PRETTY_DATE_TIME_FORMAT);
  }
}
