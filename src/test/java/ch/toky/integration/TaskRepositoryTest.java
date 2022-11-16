package ch.toky.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TaskRepositoryTest {

  @InjectMock TaskRepository taskRepository;

  @Test
  void readTasks() {
    assertThat(taskRepository.findByDate(LocalDateTime.MIN), hasSize(0));
  }
}
