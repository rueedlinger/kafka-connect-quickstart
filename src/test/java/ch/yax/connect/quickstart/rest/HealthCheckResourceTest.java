package ch.yax.connect.quickstart.rest;

import org.apache.kafka.connect.health.ConnectClusterState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

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


}
