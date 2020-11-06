package ch.yax.connect.quickstart.sink;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.Map;

public class LogSinkConfig extends AbstractConfig {

    public static final String LOG_LEVEL = "log.level";
    public static final String LOG_CONTENT = "log.content";
    public static final String LOG_FORMAT = "log.format";
    public static final String TASK_ID = "task.id";
    public static final String TASK_MAX = "task.max";

    public static final String LOG_FORMAT_DEFAULT = "{} {}";
    public static final String LOG_CONTENT_DEFAULT = "all";
    public static final String LOG_LEVEL_DEFAULT = "info";

    public static final ConfigDef CONFIG_DEF =
            new ConfigDef()
                    .define(LOG_LEVEL,
                            ConfigDef.Type.STRING,
                            LOG_LEVEL_DEFAULT,
                            ConfigDef.Importance.HIGH,
                            "Log level.")
                    .define(LOG_FORMAT,
                            ConfigDef.Type.STRING,
                            LOG_FORMAT_DEFAULT,
                            ConfigDef.Importance.HIGH,
                            "Log pattern format.")
                    .define(LOG_CONTENT,
                            ConfigDef.Type.STRING,
                            LOG_CONTENT_DEFAULT,
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

    public String getLogPatternFormat() {
        final String format = getString(LOG_FORMAT);

        if (StringUtils.isEmpty(format)) {
            return LOG_FORMAT_DEFAULT;
        } else {
            return format;
        }
    }

    public enum LogLevel {
        INFO,
        DEBUG,
        WARN,
        ERROR,
        TRACE
    }

    public enum LogContent {
        ALL,
        KEY,
        VALUE,
        KEY_VALUE
    }


}
