package ch.toky.boundary;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api")
@PermitAll
@Tag(name = "Preflight", description = "Ressource fuer Preflight und readiness check")
public class PreflightResource {

  @OPTIONS
  @Path("{.*}")
  @PermitAll
  public Response preflightCheck() {
    return Response.ok().build();
  }

  @GET
  @PermitAll
  public Response ping() {
    return Response.ok("Pong").build();
  }

  @HEAD
  @PermitAll
  public Response head() {
    return Response.noContent().build();
  }
}
