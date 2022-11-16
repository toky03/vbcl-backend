package ch.toky.integration;

import ch.toky.entity.TaskEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import java.time.LocalDateTime;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TaskRepository implements PanacheRepository<TaskEntity> {

  public List<TaskEntity> findWithSorting(String column, boolean ascending) {
    Sort sorting = ascending ? Sort.ascending(column) : Sort.descending(column);
    return listAll(sorting);
  }

  public List<TaskEntity> findFilteredWithSorting(
      String userName, String column, boolean ascending) {
    Sort sorting = ascending ? Sort.ascending(column) : Sort.descending(column);
    return list("bestaetigt = ?1 or idReservation = ?2", sorting, Boolean.FALSE, userName);
  }

  public List<TaskEntity> findByDate(LocalDateTime dateTime) {
    return list(
        "bestaetigt = ?1 and startDatum between ?2 and ?3",
        Boolean.TRUE,
        dateTime,
        dateTime.plusHours(24));
  }
}
