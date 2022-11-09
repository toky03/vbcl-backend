package ch.toky.control;

import ch.toky.dto.Ordering;
import ch.toky.dto.Task;
import ch.toky.entity.TaskEntity;
import ch.toky.integration.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
public class TaskService {

  @Inject TaskRepository taskRepository;

  @Inject MailService mailService;

  @Inject JsonWebToken jsonWebToken;

  @Inject
  @Claim(standard = Claims.preferred_username)
  String userName;

  @Inject
  @Claim(standard = Claims.given_name)
  String givenName;

  @Inject
  @Claim(standard = Claims.family_name)
  String familyname;

  public List<Task> readTasks(SecurityContext ctx, String orderColumn, Ordering ordering) {
    String column = orderColumn == null ? "startDatum" : orderColumn;
    boolean orderAscending = Ordering.ASC.equals(ordering);
    return createQuery(userName, ctx, column, orderAscending).stream()
        .map(Task::from)
        .collect(Collectors.toUnmodifiableList());
  }

  private List<TaskEntity> createQuery(
      String userName, SecurityContext ctx, String column, boolean orderAscending) {
    return !ctx.isUserInRole("vorstand")
        ? taskRepository.findFilteredWithSorting(userName, column, orderAscending)
        : taskRepository.findWithSorting(column, orderAscending);
  }

  @Transactional
  public void createTask(Task task) {
    taskRepository.persist(task.create());
  }

  @Transactional
  public void update(Long id, Task taskDto) {
    TaskEntity task = taskRepository.findById(id);
    task.setStartDatum(taskDto.getStartDatum());
    task.setBeschreibung(taskDto.getBeschreibung());
    task.setDauer(taskDto.getDauer());

    if (task.getIdReservation() != null && task.getCalendarId() != null) {
      Integer increasedCalendarSequence = task.getCalendarSequence() +1;
      mailService.updateCalendarEntry(
          task.getCalendarId(),
          task.getIdReservation(),
          "Der Helfereinsatz wurde geändert",
          task.getStartDatum(),
          readToTimeWithDefault(task.getStartDatum(), task.getDauer()),
          increasedCalendarSequence);
      task.setCalendarSequence(increasedCalendarSequence);
    }
  }

  @Transactional
  public void delete(Long id) {
    TaskEntity task = taskRepository.findById(id);
    if (task.getIdReservation() != null && task.getCalendarId() != null) {
      Integer increasedCalendarSequence = task.getCalendarSequence() +1;
      mailService.cancelCalendarEntry(
          task.getCalendarId(),
          task.getIdReservation(),
          "Der Helfereinsatz wurde abgesagt",
          task.getStartDatum(),
          readToTimeWithDefault(task.getStartDatum(), task.getDauer()),
          increasedCalendarSequence);
      task.setCalendarSequence(increasedCalendarSequence);
    }

    taskRepository.delete(task);
  }

  @Transactional
  public void reservateTask(Long id, SecurityContext ctx) {
    TaskEntity task = taskRepository.findById(id);
    task.setIdReservation(userName);
    task.setNameReservation(String.format("%s %s", givenName, familyname));
  }

  @Transactional
  public void confirmTask(Long id) {
    TaskEntity task = taskRepository.findById(id);
    if (task.getIdReservation() == null) {
      throw new WebApplicationException(
          "Aufgabe kann noch nicht bestätigt werden, da noch kein benutzer zugewiesen wurd",
          Status.BAD_REQUEST);
    }
    task.setBestaetigt(Boolean.TRUE);
    String message =
        String.format(
            "Der Helfereinsatz \"%s\" wurde vom Tk Admin bestätigt", task.getBeschreibung());
    LocalDateTime fromTime = task.getStartDatum();
    LocalDateTime toTime = readToTimeWithDefault(fromTime, task.getDauer());
    String calendarId =
        mailService.sendMailWithCalendarEntry(
            task.getIdReservation(),
            task.getNameReservation(),
            message,
            task.getBeschreibung(),
            fromTime,
            toTime);
    task.setCalendarId(calendarId);
    task.setCalendarSequence(0);
  }

  @Transactional
  public void revokeReservation(Long id) {
    TaskEntity task = taskRepository.findById(id);
    if (task.getIdReservation() == null) {
      throw new WebApplicationException(
          "Aufgabe wurde noch niemandem zugewiesen", Status.BAD_REQUEST);
    }
    if (!task.getIdReservation().equals(userName)) {
      // TODO Vanessa fragen, ob auch Admin die Reservation entfernen können
      throw new WebApplicationException(
          "Nur der Benutzer, der die Reservation vorgenommen hat kann diese entfernen",
          Status.FORBIDDEN);
    }
    task.setBestaetigt(Boolean.FALSE);
    task.setIdReservation(null);
    task.setNameReservation(null);
  }

  @Transactional
  public void revokeConfirmation(Long id) {

    TaskEntity task = taskRepository.findById(id);

    if (task.getIdReservation() != null && task.getCalendarId() != null) {
      Integer increasedSequence = task.getCalendarSequence() +1 ;
      mailService.cancelCalendarEntry(
          task.getCalendarId(),
          task.getIdReservation(),
          "Die Bestätigung wurde vom TkAdmin entfernt",
          task.getStartDatum(),
          readToTimeWithDefault(task.getStartDatum(), task.getDauer()),
          increasedSequence);
      task.setCalendarSequence(increasedSequence);
    }

    if (task.getIdReservation() == null || !task.getBestaetigt()) {
      throw new WebApplicationException(
          "Aufgabe wurde noch niemandem zugewiesen oder noch nicht bestätigt", Status.BAD_REQUEST);
    }
    task.setBestaetigt(Boolean.FALSE);
  }

  private LocalDateTime readToTimeWithDefault(LocalDateTime from, Integer dauer) {
    return dauer != null ? from.plusHours(dauer) : from.plusHours(5);
  }
}
