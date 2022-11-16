package ch.toky.control;

import ch.toky.control.IcalCreator.IcalEntry;
import ch.toky.dto.Task;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MailService {
  @Inject Mailer mailer;

  public String sendMailWithCalendarEntry(
      String receiver,
      String name,
      String message,
      String description,
      LocalDateTime from,
      LocalDateTime to) {
    String body = String.format("<strong>Hallo %s</strong>" + "\r\n" + "<p>%s</p>", name, message);

    UUID uuid = UUID.randomUUID();

    mailer.send(
        Mail.withHtml(receiver, "VBC Helfereinsatz", body)
            .addAttachment(
                "einsatz.ics",
                IcalCreator.createIcalEntry(
                    IcalEntry.builder()
                        .start(from)
                        .end(to)
                        .description(description)
                        .method("REQUEST")
                        .status("CONFIRMED")
                        .summary(description)
                        .sequence(0)
                        .uuid(uuid)
                        .build()),
                "text/calendar"));
    return uuid.toString();
  }

  public void cancelCalendarEntry(
      String id,
      String receiver,
      String description,
      LocalDateTime from,
      LocalDateTime to,
      Integer currentSequence) {

    String body = String.format("<p>%s</p>", "Helfereinsatz abgesagt");

    mailer.send(
        Mail.withHtml(receiver, "VBC Helfereinsatz", body)
            .addAttachment(
                "einsatz.ics",
                IcalCreator.createIcalEntry(
                    IcalEntry.builder()
                        .start(from)
                        .end(to)
                        .description(description)
                        .method("CANCEL")
                        .status("CANCELLED")
                        .summary(description)
                        .sequence(currentSequence)
                        .uuid(UUID.fromString(id))
                        .build()),
                "text/calendar"));
  }

  public void updateCalendarEntry(
      String id,
      String receiver,
      String message,
      LocalDateTime from,
      LocalDateTime to,
      Integer currentSequence) {
    String body = String.format("<p>%s</p>", message);

    mailer.send(
        Mail.withHtml(receiver, "VBC Helfereinsatz", body)
            .addAttachment(
                "einsatz.ics",
                IcalCreator.createIcalEntry(
                    IcalEntry.builder()
                        .start(from)
                        .end(to)
                        .description(message)
                        .method("REFRESH")
                        .status("CONFIRMED")
                        .summary(message)
                        .sequence(currentSequence)
                        .uuid(UUID.fromString(id))
                        .build()),
                "text/calendar"));
  }

  public void sendReminder(String receiver, List<Task> taskList) {
    StringBuilder bodyBuilder = new StringBuilder();
    bodyBuilder.append("<h1>Deine Anstehende Helfereinsätze</h1>");
    bodyBuilder.append("<ul>");
    taskList.forEach(
        task -> {
          String item =
              String.format(
                  "<li>%02d:%02d %s</li>",
                  task.getStartDatum().getHour(),
                  task.getStartDatum().getMinute(),
                  task.getBeschreibung());
          bodyBuilder.append(item);
        });
    mailer.send(Mail.withHtml(receiver, "Helfereinsatz Erinnerung", bodyBuilder.toString()));
  }
}
