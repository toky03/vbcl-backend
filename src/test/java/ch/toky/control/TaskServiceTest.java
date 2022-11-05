package ch.toky.control;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.toky.dto.Ordering;
import ch.toky.dto.Task;
import ch.toky.entity.TaskEntity;
import ch.toky.integration.TaskRepository;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import java.security.Principal;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

@QuarkusTest
class TaskServiceTest {

  private static final Long ID = 1L;

  @Inject TaskService taskService;

  @InjectMock TaskRepository taskRepository;

  @Inject JsonWebToken jsonWebToken;

  @BeforeEach
  void setup() {
    JsonWebToken mock = Mockito.mock(JsonWebToken.class);
    Mockito.when(mock.getName()).thenReturn("mocked User");
    QuarkusMock.installMockForType(mock, JsonWebToken.class);
  }

  @Test
  @TestSecurity(user = "tester", roles = "standart")
  @JwtSecurity(claims = {@Claim(key = "email", value = "username")})
  void filterBestaetigtForNonAdmin() {
    // setup

    Mockito.when(taskRepository.findFilteredWithSorting(anyString(), anyString(), eq(Boolean.TRUE)))
        .thenReturn(List.of(TaskEntity.builder().build()));

    MockSecurityContext context = mock(MockSecurityContext.class);
    when(context.isUserInRole("vorstand")).thenReturn(Boolean.FALSE);

    // execute
    List<Task> tasks = taskService.readTasks(context, "datum", Ordering.ASC);

    // assert
    assertThat(tasks, hasSize(0));
    Mockito.verify(taskRepository).findFilteredWithSorting(eq(null), anyString(), eq(Boolean.TRUE));
  }

  class MockSecurityContext implements SecurityContext {

    @Override
    public Principal getUserPrincipal() {
      return null;
    }

    @Override
    public boolean isUserInRole(String role) {
      return true;
    }

    @Override
    public boolean isSecure() {
      return false;
    }

    @Override
    public String getAuthenticationScheme() {
      return null;
    }
  }
}
