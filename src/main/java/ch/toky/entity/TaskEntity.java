package ch.toky.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_TASK")
public class TaskEntity extends PanacheEntity {

  @Column(name = "DATUM")
  LocalDateTime startDatum;

  @Column(name = "BESCHREIBUNG")
  String beschreibung;

  @Column(name = "DAUER")
  Integer dauer;

  @Column(name = "NAME_RESERVATION")
  String nameReservation;

  @Column(name = "ID_RESERVATION")
  String idReservation;

  @Column(name = "CALENDAR_ID")
  String calendarId;

  @Column(name = "CALENDAR_SEQUENCE")
  Integer calendarSequence;

  @Column(name = "BESTAETIGT")
  Boolean bestaetigt;
}
