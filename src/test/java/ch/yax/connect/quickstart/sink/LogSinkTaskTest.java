package ch.yax.connect.quickstart.sink;

import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.Map;

public class LogSinkTaskTest {

    private final static String VALUE = "value";
    private final static String KEY = "key";


    @ParameterizedTest
    @EnumSource(LogSinkConfig.LogLevel.class)
    void logLevels(final LogSinkConfig.LogLevel level) {

        final LogSinkTask task = new LogSinkTask();
        task.start(Map.of(LogSinkConfig.LOG_LEVEL, level.toString()));
        task.put(List.of(createRecord()));
        task.stop();
    }

    @ParameterizedTest
    @EnumSource(LogSinkConfig.LogContent.class)
    void logContent(final LogSinkConfig.LogContent content) {

        final LogSinkTask task = new LogSinkTask();
        task.start(Map.of(LogSinkConfig.LOG_CONTENT, content.toString()));
        task.put(List.of(createRecord()));
        task.stop();
    }


    private SinkRecord createRecord() {
        return new SinkRecord("topic", 0, null, KEY, null, VALUE, 1);
    }

}
