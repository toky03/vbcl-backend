package ch.toky.dto;

import ch.toky.entity.TaskEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDate;
import java.time.LocalTime;
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

  @JsonbDateFormat(value = "yyyy-MM-dd")
  LocalDate datum;
  @JsonbDateFormat(value = "HH:mm")
  LocalTime startZeit;

  String beschreibung;
  String dauer;
  User reservation;
  Boolean bestaetigt;

  public static Task from(TaskEntity taskEntity) {
    return Task.builder()
        .id(String.valueOf(taskEntity.id))
        .datum(taskEntity.getDatum())
        .beschreibung(taskEntity.getBeschreibung())
        .dauer(taskEntity.getDauer())
        .reservation(
            User.builder()
                .id(taskEntity.getIdReservation())
                .name(taskEntity.getNameReservation())
                .build())
        .bestaetigt(taskEntity.getBestaetigt())
        .startZeit(taskEntity.getStartTime())
        .build();
  }

  public TaskEntity create() {
    return TaskEntity.builder()
        .datum(datum)
        .beschreibung(beschreibung)
        .dauer(dauer)
        .bestaetigt(Boolean.FALSE)
        .startTime(startZeit)
        .build();
  }
}
