package ch.toky.boundary;

import static ch.toky.Constants.ROLE_VORSTAND;

import ch.toky.control.CsvExportService;
import ch.toky.dto.Ordering;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/export")
@Tag(name = "File Export", description = "Datei export resource")
public class ExportRessource {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("YYYYMMdd_HHmmss");
  private static final String PREFIX = "vbcl_arbeitsliste";

  @Inject CsvExportService csvExportService;

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
}
