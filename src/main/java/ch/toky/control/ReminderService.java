package ch.toky.control;

import ch.toky.dto.Task;
import ch.toky.dto.Task.TaskBuilder;
import ch.toky.entity.TaskEntity;
import ch.toky.integration.TaskRepository;
import io.quarkus.scheduler.Scheduled;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ReminderService {

  private static final Integer DAYS_UNTIL_EVENT = 3;

  @Inject TimeProvider timeProvider;

  @Inject TaskRepository taskRepository;

  @Inject MailService mailService;

  @Scheduled(cron = "0 0 7 * * ?")
  public void remindUsers() {
    List<TaskEntity> taskEntities =
        taskRepository.findByDate(
            timeProvider.now().plusDays(DAYS_UNTIL_EVENT).toLocalDate().atStartOfDay());
    groupByMail(taskEntities)
        .forEach(
            (key, value) -> {
              mailService.sendReminder(key, value);
            });
  }

  private Map<String, List<Task>> groupByMail(List<TaskEntity> entities) {
    return entities.stream()
        .map(Task::from)
        .map(TaskBuilder::build)
        .collect(Collectors.groupingBy(task -> task.getReservation().getId()));
  }
}
