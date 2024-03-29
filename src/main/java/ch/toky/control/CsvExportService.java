package ch.toky.control;

import ch.toky.dto.Ordering;
import ch.toky.entity.TaskEntity;
import ch.toky.integration.TaskRepository;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

@RequestScoped
public class CsvExportService {

  @Inject TaskRepository taskRepository;

  @Inject TimeProvider timeProvider;

  public ByteArrayInputStream createCsvFile(String sortColumn, Ordering ordering) {
    return tasksToCsv(
        taskRepository.findWithSorting(null, sortColumn, Ordering.ASC.equals(ordering)));
  }

  private ByteArrayInputStream tasksToCsv(List<TaskEntity> tasks) {
    final CSVFormat format =
        CSVFormat.EXCEL
            .builder()
            .setDelimiter(";")
            .setHeader("Event", "Id", "Datum", "Dauer", "Beschreibung", "Reservierung")
            .build();

    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter csvPrinter =
            new CSVPrinter(new PrintWriter(out, false, StandardCharsets.ISO_8859_1), format); ) {
      for (TaskEntity task : tasks) {
        List<String> data =
            Arrays.asList(
                task.getEventName(),
                String.valueOf(task.id),
                timeProvider.formatToPretty(task.getStartDatum()),
                task.getDauer().toString(),
                task.getBeschreibung(),
                task.getNameReservation());
        csvPrinter.printRecord(data);
      }

      csvPrinter.flush();
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
    }
  }
}
