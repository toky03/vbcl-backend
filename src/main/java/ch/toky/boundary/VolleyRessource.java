package ch.toky.boundary;

import ch.toky.control.TaskService;
import ch.toky.dto.Ordering;
import ch.toky.dto.Task;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/tasks")
@Tag(name = "Volley", description = "Generelle Taetigkeiten")
public class VolleyRessource {

  @Inject TaskService taskService;

  @GET
  @RolesAllowed("**")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Task> readTasks(
      @QueryParam("sortColumn") String sortColumn,
      @QueryParam("sorting") Ordering ordering,
      @Context SecurityContext ctx) {
    return taskService.readTasks(ctx, sortColumn, ordering);
  }

  @POST
  @RolesAllowed("tkAdmin")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public void createTask(Task task) {
    this.taskService.createTask(task);
  }

  @PUT
  @Path("{taskId}/reservate")
  @RolesAllowed("**")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public void reservate(@PathParam("taskId") Long id, @Context SecurityContext ctx) {
    this.taskService.reservateTask(id, ctx);
  }

  @PUT
  @Path("{taskId}")
  @RolesAllowed("tkAdmin")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public void update(@PathParam("taskId") Long id, Task task) {
    this.taskService.update(id, task);
  }

  @DELETE
  @Path("{taskId}")
  @RolesAllowed("tkAdmin")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public void delete(@PathParam("taskId") Long id) {
    this.taskService.delete(id);
  }

  @PUT
  @Path("{taskId}/revoke-reservation")
  @RolesAllowed("**")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public void revokeReservation(@PathParam("taskId") Long id) {
    this.taskService.revokeReservation(id);
  }

  @PUT
  @Path("{taskId}/confirm")
  @RolesAllowed("tkAdmin")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public void confirm(@PathParam("taskId") Long id, @Context SecurityContext ctx) {
    this.taskService.confirmTask(id);
  }

  @PUT
  @Path("{taskId}/revoke-confirm")
  @RolesAllowed("tkAdmin")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public void revokeConfirm(@PathParam("taskId") Long id, @Context SecurityContext ctx) {
    this.taskService.revokeConfirmation(id);
  }
}
