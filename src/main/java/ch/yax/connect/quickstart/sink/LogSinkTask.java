package ch.yax.connect.quickstart.sink;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.util.Collection;
import java.util.Map;

import static ch.yax.connect.quickstart.common.ConnectMetadataUtil.getVersion;

@Slf4j
public class LogSinkTask extends SinkTask {

    private static final String LOG_PATTERN_FORMAT = "{}: {}";

    private LogSinkConfig.LogLevel logLevel;
    private LogSinkConfig.LogContent logContent;
    private String logMessagePrefix;

    @Override
    public String version() {
        return getVersion();
    }

    @Override
    public void start(final Map<String, String> properties) {
        final LogSinkConfig config = new LogSinkConfig(properties);
        logLevel = config.getLogLevel();
        logContent = config.getLogContent();
        logMessagePrefix = config.getLogMessagePrefix();
        log.info("Starting LogSinkTask with properties {}", properties);
    }

    @Override
    public void put(final Collection<SinkRecord> records) {
        switch (logLevel) {
            case INFO:
                records.forEach(record -> log.info(LOG_PATTERN_FORMAT,
                        getLoggingArgs(logContent, logMessagePrefix, record)));
                break;
            case WARN:
                records.forEach(record -> log.warn(LOG_PATTERN_FORMAT,
                        getLoggingArgs(logContent, logMessagePrefix, record)));
                break;
            case DEBUG:
                records.forEach(record -> log.debug(LOG_PATTERN_FORMAT,
                        getLoggingArgs(logContent, logMessagePrefix, record)));
                break;
            case ERROR:
                records.forEach(record -> log.error(LOG_PATTERN_FORMAT,
                        getLoggingArgs(logContent, logMessagePrefix, record)));
                break;
        }
    }

    private Object[] getLoggingArgs(final LogSinkConfig.LogContent logContent, final String prefix, final SinkRecord record) {
        switch (logContent) {
            case ALL:
                return new Object[]{prefix, record};
            case KEY:
                return new Object[]{prefix, record.key()};
            case VALUE:
                return new Object[]{prefix, record.value()};
        }
        return new Object[]{prefix, record};
    }

    @Override
    public void stop() {
        log.info("Stopping LogSinkTask.");
    }
}
