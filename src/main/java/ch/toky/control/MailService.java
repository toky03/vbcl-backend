package ch.toky.control;

import static net.fortuna.ical4j.model.Property.TZID;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;

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

    UidGenerator ug = new RandomUidGenerator();
    Property calendarId = ug.generateUid();
    Property method = new Method(Method.VALUE_REQUEST);
    Property status = new Status(Status.VALUE_CONFIRMED);
    Property sequence = new Sequence(0);

    mailer.send(
        Mail.withHtml(receiver, "VBC Helfereinsatz", body)
            .addAttachment(
                "einsatz.ics",
                createEntry(calendarId, from, to, description, method, sequence, status),
                "text/calendar"));
    return calendarId.getValue();
  }

  public void cancelCalendarEntry(
      String id,
      String receiver,
      String description,
      LocalDateTime from,
      LocalDateTime to,
      Integer currentSequence) {
    Property calendarId = new Uid(id);
    Property method = new Method(Method.VALUE_CANCEL);
    Property status = new Status(Status.VALUE_CANCELLED);
    Property sequence = new Sequence(currentSequence);

    String body = String.format("<p>%s</p>", "Helfereinsatz abgesagt");

    mailer.send(
        Mail.withHtml(receiver, "VBC Helfereinsatz", body)
            .addAttachment(
                "einsatz.ics",
                createEntry(calendarId, from, to, description, method, sequence, status),
                "text/calendar"));
  }

  public void updateCalendarEntry(
      String id,
      String receiver,
      String message,
      LocalDateTime from,
      LocalDateTime to,
      Integer currentSequence) {
    Property calendarId = new Uid(id);
    Property method = new Method(Method.VALUE_REFRESH);
    Property sequence = new Sequence(currentSequence);
    Property status = new Status(Status.VALUE_CONFIRMED);
    String body = String.format("<p>%s</p>", message);

    mailer.send(
        Mail.withHtml(receiver, "VBC Helfereinsatz", body)
            .addAttachment(
                "einsatz.ics",
                createEntry(calendarId, from, to, message, method, sequence, status),
                "text/calendar"));
  }

  private byte[] createEntry(
      Property id,
      LocalDateTime from,
      LocalDateTime to,
      String description,
      Property method,
      Property sequence,
      Property status) {
    TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
    TimeZone timezone = registry.getTimeZone("Europe/Zurich");
    VTimeZone tz = timezone.getVTimeZone();
    Property timeZone = tz.getProperty(TZID).orElse(null);

    VEvent meeting = null;
    try {
      meeting =
          new VEvent(from, to, description)
              .withProperty(timeZone)
              .withProperty(id)
              .withProperty(sequence)
              .withProperty(status)
              .withProperty(new Description(description))
              .withProperty(new Organizer("info@vbclyss.ch"))
              .getFluentTarget();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    net.fortuna.ical4j.model.Calendar icsCalendar =
        new net.fortuna.ical4j.model.Calendar()
            .withProdId("-//Helfereinsatz VBCLyss")
            .withProperty(method)
            .withDefaults()
            .withComponent(meeting)
            .getFluentTarget();
    return icsCalendar.toString().getBytes();
  }
}
