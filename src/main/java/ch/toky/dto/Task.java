package ch.toky.dto;

import ch.toky.entity.TaskEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDateTime;
import javax.json.bind.annotation.JsonbDateFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class Task {
  String id;

  @JsonbDateFormat(value = "yyyy-MM-dd HH:mm")
  LocalDateTime startDatum;

  String beschreibung;
  Integer dauer;
  User reservation;
  Boolean bestaetigt;

  public static Task.TaskBuilder from(TaskEntity taskEntity) {
    return Task.builder()
        .id(String.valueOf(taskEntity.id))
        .startDatum(taskEntity.getStartDatum())
        .beschreibung(taskEntity.getBeschreibung())
        .dauer(taskEntity.getDauer())
        .reservation(
            User.builder()
                .id(taskEntity.getIdReservation())
                .name(taskEntity.getNameReservation())
                .build())
        .bestaetigt(taskEntity.getBestaetigt());
  }

  public TaskEntity create() {
    return TaskEntity.builder()
        .startDatum(startDatum)
        .beschreibung(beschreibung)
        .dauer(dauer)
        .bestaetigt(Boolean.FALSE)
        .build();
  }
}
