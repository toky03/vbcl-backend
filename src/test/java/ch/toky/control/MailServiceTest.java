package ch.toky.control;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import java.time.LocalDateTime;
import java.util.List;
import javax.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class MailServiceTest {

  private static final String RECEIVER = "marco.jakob3@gmail.com";
  private static final String SUBJECT = "VBC Helfereinsatz";

  @Inject MailService mailService;

  @Inject MockMailbox mockMailbox;

  @BeforeEach
  void setup() {
    mockMailbox.clear();
  }

  @Test
  void sendMail() {
    String description = "Beschreibung";
    String name = "Marco";
    String uid =
        mailService.sendMailWithCalendarEntry(
            RECEIVER,
            name,
            "Test",
            description,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(2));

    assertThat(uid, not(nullValue()));
    List<Mail> sent = mockMailbox.getMessagesSentTo(RECEIVER);
    assertThat(sent, hasSize(1));
    Mail actual = sent.get(0);
    assertThat(actual.getHtml(), containsString(name));
    assertThat(actual.getSubject(), equalTo(SUBJECT));
  }

  @Test
  void cancelMeeting() {

    mailService.cancelCalendarEntry(
        "d9d588c3-2180-4e60-8008-6f48045188c4",
        "marco.jakob3@gmail.com",
        "Beschreibung",
        LocalDateTime.now(),
        LocalDateTime.now().plusHours(2),
        1);
  }
}
