package ch.toky.control;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.eq;

import ch.toky.dto.Task;
import ch.toky.dto.User;
import ch.toky.entity.TaskEntity;
import ch.toky.integration.TaskRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class ReminderServiceTest {

  private static final LocalDateTime MOCK_NOW = LocalDate.of(2020, 1, 1).atStartOfDay();
  private static final String USER_MAIL = "mail@test.ch";

  @Inject ReminderService reminderService;
  @InjectMock TimeProvider timeProvider;
  @InjectMock MailService mailService;

  @Inject TaskRepository taskRepository;

  @BeforeEach
  @Transactional
  void setup() {
    Mockito.when(timeProvider.now()).thenReturn(MOCK_NOW);
    taskRepository.persist(
        List.of(
            TaskEntity.builder()
                .beschreibung("lower bound not in scope")
                .idReservation(USER_MAIL)
                .bestaetigt(Boolean.TRUE)
                .startDatum(MOCK_NOW.plusDays(2))
                .build(),
            TaskEntity.builder()
                .beschreibung("lower bound in scope")
                .idReservation(USER_MAIL)
                .bestaetigt(Boolean.TRUE)
                .startDatum(MOCK_NOW.plusDays(3))
                .build(),
            TaskEntity.builder()
                .beschreibung("upper bound in scope")
                .idReservation(USER_MAIL)
                .bestaetigt(Boolean.TRUE)
                .startDatum(MOCK_NOW.plusDays(4))
                .build(),
            TaskEntity.builder()
                .beschreibung("filtered because not bestaetigt")
                .idReservation(USER_MAIL)
                .bestaetigt(Boolean.FALSE)
                .startDatum(MOCK_NOW.plusDays(4))
                .build(),
            TaskEntity.builder()
                .beschreibung("upper bound not in scope")
                .idReservation(USER_MAIL)
                .bestaetigt(Boolean.TRUE)
                .startDatum(MOCK_NOW.plusDays(4).plusMinutes(1))
                .build()));
  }

  @AfterEach
  @Transactional
  void cleanup() {
    taskRepository.deleteAll();
  }

  @Test
  @Transactional
  void findDatesInThreeDays() {
    // setup
    List<Task> expectedRemindItems =
        List.of(
            Task.builder()
                .beschreibung("lower bound in scope")
                .reservation(User.builder().id(USER_MAIL).build())
                .bestaetigt(Boolean.TRUE)
                .startDatum(MOCK_NOW.plusDays(3))
                .build(),
            Task.builder()
                .beschreibung("upper bound in scope")
                .reservation(User.builder().id(USER_MAIL).build())
                .bestaetigt(Boolean.TRUE)
                .startDatum(MOCK_NOW.plusDays(4))
                .build());
    // act
    reminderService.remindUsers();
    // validate
    Mockito.verify(mailService).sendReminder(eq(USER_MAIL), match(expectedRemindItems));
  }

  private List<Task> match(List<Task> expectedRemindItems) {
    return Mockito.argThat(
        actual -> {
          assertThat(
              actual,
              contains(
                  (expectedRemindItems.stream()
                      .map(
                          expectedItem ->
                              allOf(
                                  hasProperty(
                                      "beschreibung", equalTo(expectedItem.getBeschreibung()))))
                      .collect(Collectors.toUnmodifiableList()))));
          return true;
        });
  }
}
