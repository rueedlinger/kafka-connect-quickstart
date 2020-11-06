package ch.yax.connect.quickstart.source;

import org.apache.kafka.connect.connector.ConnectorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@ExtendWith(MockitoExtension.class)
public class RandomSourceConnectorTest {

    @Mock
    private ConnectorContext context;

    @Test
    void testConfig() {
        final RandomSourceConnector connector = new RandomSourceConnector();
        connector.initialize(context);
        connector.start(new HashMap<>());

        assertThat(connector.taskConfigs(1).get(0)).contains(
                entry("task.max", "1"),
                entry("task.id", "0"));

        assertThat(connector.taskConfigs(2).get(1)).contains(
                entry("task.max", "2"),
                entry("task.id", "1"));


        connector.stop();

    }

}
