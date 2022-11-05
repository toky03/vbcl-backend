package ch.toky.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
  LocalDate datum;

  @Column(name = "BESCHREIBUNG")
  String beschreibung;

  @Column(name = "DAUER")
  String dauer;

  @Column(name = "NAME_RESERVATION")
  String nameReservation;

  @Column(name = "ID_RESERVATION")
  String idReservation;

  @Column(name = "BESTAETIGT")
  Boolean bestaetigt;

  @Column(name="START_TIME")
  LocalTime startTime;

}
