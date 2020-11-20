package ch.yax.connect.quickstart.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.health.ConnectClusterState;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class HealthCheckResource {

    private final Map<String, ?> configs;
    private final ConnectClusterState clusterState;

    public HealthCheckResource(final Map<String, ?> configs, final ConnectClusterState clusterState) {
        //initialize resource
        this.configs = configs;
        this.clusterState = clusterState;
        log.info("initialize with configs {} and clusterState {}", configs, clusterState);
    }

    @GET
    @Path("/")
    public Response health() {
        return Response.ok(HealthResponse.from(clusterState)).build();
    }
}
