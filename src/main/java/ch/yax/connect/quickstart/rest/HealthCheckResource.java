package ch.yax.connect.quickstart.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.health.ConnectClusterState;
import org.apache.kafka.connect.health.ConnectorHealth;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class HealthCheckResource {

    private static final String FIELD_STATUS = "status";
    private static final String STATE_RUNNING = "RUNNING";

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
        final Map<String, Object> healthResponse = new HashMap<>();
        healthResponse.put(FIELD_STATUS, HealthState.UP);

        clusterState.connectors().forEach(connectorName -> {
            final ConnectorHealth connectorHealth = clusterState.connectorHealth(connectorName);

            final Map<Integer, Object> taskHealth = new HashMap<>();

            connectorHealth.tasksState().forEach((id, taskState) -> {
                if (STATE_RUNNING.equalsIgnoreCase(taskState.state())) {
                    taskHealth.put(id, Map.of(FIELD_STATUS, HealthState.UP));
                } else {
                    taskHealth.put(id, Map.of(FIELD_STATUS, HealthState.UP));
                    healthResponse.put(FIELD_STATUS, HealthState.DOWN);
                }
            });

            if (STATE_RUNNING.equalsIgnoreCase(connectorHealth.connectorState().state())) {
                healthResponse.put(connectorName, Map.of(FIELD_STATUS, HealthState.UP, "tasks", taskHealth));
            } else {
                healthResponse.put(connectorName, Map.of(FIELD_STATUS, HealthState.DOWN, "tasks", taskHealth));
                healthResponse.put(FIELD_STATUS, HealthState.DOWN);
            }

        });

        return Response.ok(healthResponse).build();
    }
}
