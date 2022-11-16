package ch.toky.boundary;

import static ch.toky.Constants.ROLE_VORSTAND;

import ch.toky.control.CsvExportService;
import ch.toky.control.IcalCreator;
import ch.toky.control.IcalCreator.IcalEntry;
import ch.toky.dto.Ordering;
import ch.toky.entity.TaskEntity;
import ch.toky.integration.TaskRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/export")
@Tag(name = "File Export", description = "Datei export resource")
public class ExportRessource {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("YYYYMMdd_HHmmss");
  private static final String PREFIX = "vbcl_arbeitsliste";

  @Inject CsvExportService csvExportService;
  @Inject TaskRepository taskRepository;

  @GET
  @RolesAllowed(ROLE_VORSTAND)
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response readTasks(
      @QueryParam("sortColumn") String sortColumn, @QueryParam("ordering") Ordering ordering) {
    String dateTimeFormatted = LocalDateTime.now().format(FORMATTER);
    String filename = String.format("%s_%s.csv", PREFIX, dateTimeFormatted);
    return Response.ok(csvExportService.createCsvFile(sortColumn, ordering))
        .header("content-disposition", "attachment; filename = " + filename)
        .build();
  }

  @GET
  @Path("calendar/{taskId}")
  @RolesAllowed("**")
  @Produces("text/calendar")
  public Response readCalendar(@PathParam("taskId") Long taskid) {
    TaskEntity taskEntity = taskRepository.findById(taskid);
    if (taskEntity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    byte[] calendarEntry =
        IcalCreator.createIcalEntry(
            IcalEntry.builder()
                .uuid(
                    taskEntity.getCalendarId() == null
                        ? UUID.randomUUID()
                        : UUID.fromString(taskEntity.getCalendarId()))
                .sequence(
                    taskEntity.getCalendarSequence() == null ? 0 : taskEntity.getCalendarSequence())
                .summary(taskEntity.getBeschreibung())
                .description(taskEntity.getBeschreibung())
                .status("CONFIRMED")
                .method("REQUEST")
                .start(taskEntity.getStartDatum())
                .end(
                    taskEntity
                        .getStartDatum()
                        .plusHours(taskEntity.getDauer() == null ? 2 : taskEntity.getDauer()))
                .build());
    return Response.ok(calendarEntry).build();
  }
}
