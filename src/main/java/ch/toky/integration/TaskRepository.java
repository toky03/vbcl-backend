package ch.toky.integration;

import ch.toky.entity.TaskEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import java.time.LocalDateTime;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TaskRepository implements PanacheRepository<TaskEntity> {

  public List<TaskEntity> findWithSorting(String eventName, String column, boolean ascending) {
    Sort sorting =
        ascending
            ? Sort.ascending(column, "beschreibung")
            : Sort.descending(column, "beschreibung");
    return list("?1 is null or eventName = cast(?1 as text)", sorting, eventName);
  }

  public List<TaskEntity> findFilteredWithSorting(
      String eventName, String userName, String column, boolean ascending) {
    Sort sorting =
        ascending
            ? Sort.ascending(column, "beschreibung")
            : Sort.descending(column, "beschreibung");
    return list(
        "(?1 is null or eventName =  cast(?1 as text)) and (bestaetigt = ?2 or idReservation = ?3)",
        sorting,
        eventName,
        Boolean.FALSE,
        userName);
  }

  public List<TaskEntity> findByDate(LocalDateTime dateTime) {
    return list(
        "bestaetigt = ?1 and startDatum between ?2 and ?3",
        Boolean.TRUE,
        dateTime,
        dateTime.plusHours(24));
  }
}
