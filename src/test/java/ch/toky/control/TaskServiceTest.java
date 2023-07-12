package ch.toky.control;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import ch.toky.dto.Ordering;
import ch.toky.dto.Task;
import ch.toky.entity.TaskEntity;
import ch.toky.integration.TaskRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import java.util.List;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class TaskServiceTest {

  private static final Long ID = 1L;

  @Inject TaskService taskService;

  @InjectMock TaskRepository taskRepository;

  @Test
  void filterBestaetigtForNonAdmin() {
    // setup
    String userName = "user";
    String orderColumn = "datum";
    String eventName = "A";
    Mockito.when(
            taskRepository.findFilteredWithSorting(
                anyString(), anyString(), anyString(), eq(Boolean.TRUE)))
        .thenReturn(List.of());

    // execute
    List<Task> tasks =
        taskService.readTasks(eventName, userName, Boolean.FALSE, orderColumn, Ordering.ASC);

    // assert
    assertThat(tasks, hasSize(0));
    Mockito.verify(taskRepository)
        .findFilteredWithSorting(eq(eventName), eq(userName), eq(orderColumn), eq(Boolean.TRUE));
  }

  @Test
  void shouldNotSendConfidentialInformationIfUserIsUnknown() {
    // setup
    String userName = "";
    String orderColumn = "datum";
    Mockito.when(
            taskRepository.findFilteredWithSorting(
                eq(null), anyString(), anyString(), eq(Boolean.TRUE)))
        .thenReturn(
            List.of(
                TaskEntity.builder()
                    .bestaetigt(Boolean.TRUE)
                    .idReservation("confidentialId")
                    .nameReservation("confidential name")
                    .build()));

    // execute
    List<Task> tasks =
        taskService.readTasks(null, userName, Boolean.FALSE, orderColumn, Ordering.ASC);

    // assert
    assertThat(tasks, hasSize(1));
    Task task = tasks.get(0);
    assertThat(task.getReservation(), nullValue());
    assertThat(task.getBestaetigt(), nullValue());
    Mockito.verify(taskRepository)
        .findFilteredWithSorting(eq(null), eq(""), eq(orderColumn), eq(Boolean.TRUE));
  }

  @Test
  void shouldNotFilterForAdmin() {
    // setup
    String userName = "user";
    String orderColumn = "datum";
    String eventname = "eventName";
    Mockito.when(taskRepository.findWithSorting(anyString(), anyString(), eq(Boolean.TRUE)))
        .thenReturn(List.of());

    // execute
    List<Task> tasks =
        taskService.readTasks(eventname, userName, Boolean.TRUE, orderColumn, Ordering.ASC);

    // assert
    assertThat(tasks, hasSize(0));
    Mockito.verify(taskRepository)
        .findWithSorting(eq(eventname), eq(orderColumn), eq(Boolean.TRUE));
  }
}
