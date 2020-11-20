package ch.yax.connect.quickstart.rest;

import org.apache.kafka.connect.health.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HealthCheckResourceTest {

    @Mock
    private ConnectClusterState connectClusterState;

    @Test
    void testHealth() {
        final HealthCheckResource resource = new HealthCheckResource(new HashMap<>(), connectClusterState);
        final Response response = resource.health();
        assertThat(response.getStatus()).isEqualTo(200);
        final HealthResponse payload = (HealthResponse) response.getEntity();
        assertThat(payload.getStatus()).isEqualTo(HealthState.UP);
    }

    @Test
    void testAllOk() {

        when(connectClusterState.connectors()).thenReturn(List.of("foo", "bar"));
        when(connectClusterState.connectorHealth("foo")).thenReturn(createConnectorHealth("foo", "RUNNING"));
        when(connectClusterState.connectorHealth("bar")).thenReturn(createConnectorHealth("bar", "RUNNING"));

        final HealthCheckResource resource = new HealthCheckResource(new HashMap<>(), connectClusterState);
        final Response response = resource.health();
        assertThat(response.getStatus()).isEqualTo(200);
        final HealthResponse payload = (HealthResponse) response.getEntity();
        assertThat(payload.getStatus()).isEqualTo(HealthState.UP);

        assertThat(payload.getConnectors()).hasSize(2);


    }

    @Test
    void testAllFailed() {

        when(connectClusterState.connectors()).thenReturn(List.of("foo", "bar"));
        when(connectClusterState.connectorHealth("foo")).thenReturn(createConnectorHealth("foo", "RUNNING"));
        when(connectClusterState.connectorHealth("bar")).thenReturn(createConnectorHealth("bar", "FAILED"));

        final HealthCheckResource resource = new HealthCheckResource(new HashMap<>(), connectClusterState);
        final Response response = resource.health();
        assertThat(response.getStatus()).isEqualTo(200);
        final HealthResponse payload = (HealthResponse) response.getEntity();
        assertThat(payload.getStatus()).isEqualTo(HealthState.DOWN);

        assertThat(payload.getConnectors()).hasSize(2);


    }

    private ConnectorHealth createConnectorHealth(final String name, final String state) {
        final ConnectorState connectorState = new ConnectorState(state, "workerId", null);
        final Map<Integer, TaskState> taskState = new HashMap<>();
        taskState.put(0, new TaskState(0, state, "workerId", null));
        return new ConnectorHealth(name, connectorState, taskState, ConnectorType.SINK);
    }


}
