package ch.yax.connect.quickstart.sink;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.yax.connect.quickstart.common.ConnectMetadataUtil.getVersion;
import static ch.yax.connect.quickstart.sink.LogSinkConfig.TASK_ID;
import static ch.yax.connect.quickstart.sink.LogSinkConfig.TASK_MAX;

@Slf4j
public class LogSinkConnector extends SinkConnector {

    private Map<String, String> configProps;

    @Override
    public void start(final Map<String, String> properties) {
        log.info("Starting LogSinkConnector with properties {}", properties);
        configProps = properties;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return LogSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(final int maxTasks) {
        log.info("Setting task configurations for {} workers.", maxTasks);
        final List<Map<String, String>> configs = new ArrayList<>(maxTasks);
        for (int i = 0; i < maxTasks; ++i) {
            final Map<String, String> taskConfig = new HashMap<>(configProps);
            // add task specific values
            taskConfig.put(TASK_ID, String.valueOf(i));
            taskConfig.put(TASK_MAX, String.valueOf(maxTasks));
            configs.add(taskConfig);
        }
        return configs;
    }

    @Override
    public void stop() {
        log.info("Stopping LogSinkConnector.");
    }

    @Override
    public ConfigDef config() {
        return LogSinkConfig.CONFIG_DEF;
    }

    @Override
    public String version() {
        return getVersion();
    }
}
