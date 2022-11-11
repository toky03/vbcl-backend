package ch.toky.control;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

public class IcalCreator {

  private IcalCreator() {}

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
  private static final DateTimeFormatter FORMATTER_TIMESTAMP =
      DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

  public static byte[] createIcalEntry(IcalEntry entry) {
    String formattedStart = entry.getStart().format(FORMATTER);
    String formattedEnd = entry.getEnd().format(FORMATTER);
    String timestamp = LocalDateTime.now().format(FORMATTER_TIMESTAMP);

    return String.format(
            "BEGIN:VCALENDAR\n"
                + "PRODID:-//Helfereinsatz VBCLyss\n"
                + "METHOD:%s\n"
                + "CALSCALE:GREGORIAN\n"
                + "VERSION:2.0\n"
                + "BEGIN:VEVENT\n"
                + "DTSTAMP:%s\n"
                + "DTSTART:%s\n"
                + "DTEND:%s\n"
                + "SUMMARY:%s\n"
                + "UID:%s\n"
                + "SEQUENCE:%d\n"
                + "STATUS:%s\n"
                + "DESCRIPTION:%s\n"
                + "ORGANIZER:info@vbclyss.ch\n"
                + "END:VEVENT\n"
                + "END:VCALENDAR",
            entry.getMethod(),
            timestamp,
            formattedStart,
            formattedEnd,
            entry.getSummary(),
            entry.getUuid().toString(),
            entry.getSequence(),
            entry.getStatus(),
            entry.getDescription())
        .getBytes();
  }

  @Builder
  @Value
  public static class IcalEntry {
    String method;
    String status;
    LocalDateTime start;
    LocalDateTime end;
    String description;
    Integer sequence;
    UUID uuid;
    String summary;
  }
}
