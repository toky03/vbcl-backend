package ch.toky.control;

import ch.toky.dto.BatchTask;
import ch.toky.dto.User;
import ch.toky.entity.TaskEntity;
import ch.toky.integration.TaskRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class AdminService {

  @Inject TaskRepository taskRepository;

  @Transactional
  public void insertTasks(List<BatchTask> taskList) {
    List<TaskEntity> taskEntities =
        taskList.stream()
            .map(
                task ->
                    TaskEntity.builder()
                        .beschreibung(task.getBeschreibung())
                        .dauer(task.getDauer())
                        .nameReservation(task.getName())
                        .idReservation(task.getName())
                        .datum(task.getDatum())
                        .bestaetigt(task.getName() != null && !task.getName().equals(""))
                        .build())
            .collect(Collectors.toUnmodifiableList());

    taskRepository.persist(taskEntities.stream());
  }

  @Transactional
  public void updateUsers(List<User> users) {
    users.forEach(
        user -> {
          TaskEntity.update(
              "update from TaskEntity set idReservation = ?1 where nameReservation = ?2",
              user.getId(),
              user.getName());
        });
  }
}
