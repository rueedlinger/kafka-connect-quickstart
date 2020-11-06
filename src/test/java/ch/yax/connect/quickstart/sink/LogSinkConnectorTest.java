package ch.yax.connect.quickstart.sink;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class LogSinkConnectorTest {

    @Test
    void testStartAndStop() {
        final LogSinkConnector connector = new LogSinkConnector();
        connector.start(new HashMap<>());
        assertThat(connector.taskConfigs(1)).hasSize(1);
        assertThat(connector.taskConfigs(10)).hasSize(10);
        connector.stop();
    }

    @Test
    void testConfig() {
        final LogSinkConnector connector = new LogSinkConnector();
        connector.start(Map.of(LogSinkConfig.LOG_LEVEL, "foo"));

        assertThat(connector.taskConfigs(1).get(0)).contains(
                entry(LogSinkConfig.LOG_LEVEL, "foo"),
                entry("task.max", "1"),
                entry("task.id", "0"));

        assertThat(connector.taskConfigs(2).get(1)).contains(
                entry(LogSinkConfig.LOG_LEVEL, "foo"),
                entry("task.max", "2"),
                entry("task.id", "1"));

    }

}
