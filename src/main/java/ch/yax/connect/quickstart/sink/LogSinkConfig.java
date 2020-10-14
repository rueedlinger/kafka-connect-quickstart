package ch.yax.connect.quickstart.sink;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.Map;

public class LogSinkConfig extends AbstractConfig {

    public static final String LOG_LEVEL = "log.level";
    public static final String LOG_CONTENT = "log.content";
    public static final String LOG_MESSAGE_PREFIX = "log.prefix";
    public static final String TASK_ID = "task.id";
    public static final String TASK_MAX = "task.max";

    public static final ConfigDef CONFIG_DEF =
            new ConfigDef()
                    .define(LOG_LEVEL,
                            ConfigDef.Type.STRING,
                            "info",
                            ConfigDef.Importance.HIGH,
                            "Log level.")
                    .define(LOG_MESSAGE_PREFIX,
                            ConfigDef.Type.STRING,
                            "record",
                            ConfigDef.Importance.HIGH,
                            "Log message prefix.")
                    .define(LOG_CONTENT,
                            ConfigDef.Type.STRING,
                            "all",
                            ConfigDef.Importance.HIGH,
                            "Log content.");

    public LogSinkConfig(final Map<?, ?> properties) {
        super(CONFIG_DEF, properties);
    }

    public LogLevel getLogLevel() {
        try {
            return LogLevel.valueOf(getString(LOG_LEVEL).toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw new ConfigException("Configuration error.", ex);
        }
    }

    public LogContent getLogContent() {
        try {
            return LogContent.valueOf(getString(LOG_CONTENT).toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw new ConfigException("Configuration error.", ex);
        }
    }

    public String getLogMessagePrefix() {
        return getString(LOG_MESSAGE_PREFIX);
    }

    public enum LogLevel {
        INFO,
        DEBUG,
        WARN,
        ERROR
    }

    public enum LogContent {
        ALL,
        KEY,
        VALUE
    }
}
