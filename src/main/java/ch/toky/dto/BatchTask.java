package ch.toky.dto;


import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDate;
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
public class BatchTask {

  @JsonbDateFormat(value = "yyyy-MM-dd HH.mm")
  LocalDateTime datum;

  String beschreibung;
  Integer dauer;
  String name;
  Boolean bestaetigt;

}
