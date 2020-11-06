package ch.yax.connect.quickstart.sink;

import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LogSinkTaskTest {

    private final static String VALUE = "value";
    private final static String KEY = "key";


    @ParameterizedTest
    @EnumSource(LogSinkConfig.LogLevel.class)
    void testLogLevels(final LogSinkConfig.LogLevel level) {

        final LogSinkTask task = new LogSinkTask();
        task.start(Map.of(LogSinkConfig.LOG_LEVEL, level.toString()));
        task.put(List.of(createRecord()));
        task.stop();
    }

    @ParameterizedTest
    @EnumSource(LogSinkConfig.LogContent.class)
    void testLogContent(final LogSinkConfig.LogContent content) {

        final LogSinkTask task = new LogSinkTask();
        task.start(Map.of(LogSinkConfig.LOG_CONTENT, content.toString()));
        task.put(List.of(createRecord()));
        task.stop();
    }

    @ParameterizedTest
    @ValueSource(strings = {LogSinkConfig.LOG_CONTENT, LogSinkConfig.LOG_CONTENT, LogSinkConfig.LOG_LEVEL})
    void testInvalidConfig(final String config) {
        final LogSinkTask task = new LogSinkTask();
        assertThatThrownBy(() -> {
            task.start(Map.of(config, "invalid"));
        }).isInstanceOf(ConfigException.class);
    }

    @Test
    void testValidConfig() {
        final LogSinkTask task = new LogSinkTask();
        task.start(Map.of("unknown", "foo"));
        task.start(Map.of(LogSinkConfig.LOG_FORMAT, "unknown"));
    }

    private SinkRecord createRecord() {
        return new SinkRecord("topic", 0, null, KEY, null, VALUE, 1);
    }

}
