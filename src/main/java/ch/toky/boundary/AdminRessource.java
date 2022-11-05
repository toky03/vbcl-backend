package ch.toky.boundary;

import ch.toky.control.AdminService;
import ch.toky.dto.BatchTask;
import ch.toky.dto.User;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/admin")
@Tag(name = "Admin", description = "Ressource fuer Administrative Taetigkeiten")
public class AdminRessource {

  @Inject AdminService adminService;

  @POST
  @Path("insert")
  @RolesAllowed("tkAdmin")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public void insertTasks(List<BatchTask> tasks) {
    this.adminService.insertTasks(tasks);
  }

  @PUT
  @Path("updateUserIds")
  @RolesAllowed("tkAdmin")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public void createTask(List<User> users) {
    this.adminService.updateUsers(users);
  }
}
