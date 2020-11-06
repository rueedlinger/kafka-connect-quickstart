package ch.yax.connect.quickstart.sink;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.util.Collection;
import java.util.Map;

import static ch.yax.connect.quickstart.common.ConnectMetadataUtil.getVersion;

@Slf4j
public class LogSinkTask extends SinkTask {

    private LogSinkConfig.LogLevel logLevel;
    private LogSinkConfig.LogContent logContent;
    private String logPatternFormat;

    @Override
    public String version() {
        return getVersion();
    }

    @Override
    public void start(final Map<String, String> properties) {
        final LogSinkConfig config = new LogSinkConfig(properties);
        logLevel = config.getLogLevel();
        logContent = config.getLogContent();
        logPatternFormat = config.getLogPatternFormat();
        log.info("Starting LogSinkTask with properties {}", properties);
    }

    @Override
    public void put(final Collection<SinkRecord> records) {
        switch (logLevel) {
            case INFO:
                records.forEach(record -> log.info(logPatternFormat,
                        getLoggingArgs(logContent, record)));
                break;
            case WARN:
                records.forEach(record -> log.warn(logPatternFormat,
                        getLoggingArgs(logContent, record)));
                break;
            case DEBUG:
                records.forEach(record -> log.debug(logPatternFormat,
                        getLoggingArgs(logContent, record)));
                break;

            case TRACE:
                records.forEach(record -> log.trace(logPatternFormat,
                        getLoggingArgs(logContent, record)));
                break;

            case ERROR:
                records.forEach(record -> log.error(logPatternFormat,
                        getLoggingArgs(logContent, record)));
                break;

        }
    }

    private Object[] getLoggingArgs(final LogSinkConfig.LogContent logContent, final SinkRecord record) {
        switch (logContent) {
            case KEY:
                return new Object[]{record.key(), StringUtils.EMPTY};
            case VALUE:
                return new Object[]{record.value(), StringUtils.EMPTY};
            case KEY_VALUE:
                return new Object[]{record.key(), record.value()};
            default:
                // case ALL
                return new Object[]{record, StringUtils.EMPTY};
        }
    }


    @Override
    public void stop() {
        log.info("Stopping LogSinkTask.");
    }
}
