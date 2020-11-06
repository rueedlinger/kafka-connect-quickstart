package ch.yax.connect.quickstart.source;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.utils.SystemTime;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static ch.yax.connect.quickstart.common.ConnectMetadataUtil.getVersion;
import static ch.yax.connect.quickstart.source.RandomSourceConfig.TASK_ID;

@Slf4j
public class RandomSourceTasks extends SourceTask {

    private final static String POSITION_NAME = "position";
    private final static long DEFAULT_WAIT_MS = 500;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Time time;
    private long lastProcessedOffset;
    private RandomSourceConfig config;
    private long lastPollMs;
    private String taskId;


    public RandomSourceTasks() {
        time = new SystemTime();
        lastPollMs = time.milliseconds();
    }

    @Override
    public String version() {
        return getVersion();
    }

    @Override
    public void start(final Map<String, String> properties) {
        log.info("Starting Random source task with config: {}", properties);

        config = new RandomSourceConfig(properties);
        taskId = properties.get(TASK_ID);
        running.set(true);

        // get offsets for a specific task id
        final Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(TASK_ID, taskId));
        log.info("context offsets: {}", offset);

        if (offset != null) {
            final Long currentOffset = (Long) offset.get(POSITION_NAME);
            if (currentOffset != null) {
                lastProcessedOffset = currentOffset;
            } else {
                // first time
                lastProcessedOffset = 0L;
            }
        } else {
            throw new ConfigException("offset reader was not set");
        }
        log.info("Started Random source task lastProcessedOffset: {}", lastProcessedOffset);

    }

    @Override
    public List<SourceRecord> poll() {
        log.debug("Polling for new data");

        final long timeSinceLastPollMs = time.milliseconds() - lastPollMs;


        if (timeSinceLastPollMs < config.getPollInterval()) {
            log.debug("Sleep, time since last poll = {}", timeSinceLastPollMs);
            time.sleep(DEFAULT_WAIT_MS);
            return null;
        }

        if (!running.get()) {
            // stopped
            log.debug("task was stopped");
            return null;
        }

        // next
        lastProcessedOffset += 1;

        final RandomData random = RandomData.builder().count(lastProcessedOffset)
                .value(new Random().nextLong()).timestamp(Instant.now())
                .message("Task Id: " + taskId).build();
        log.info("polling data {}", random);

        final List<SourceRecord> records = new ArrayList<>();

        final SourceRecord record = new SourceRecord(Collections.singletonMap(TASK_ID,
                taskId),
                Collections.singletonMap(POSITION_NAME, lastProcessedOffset),
                config.getTopicName(),
                Schema.STRING_SCHEMA, UUID.randomUUID().toString(), random.toSchema(), random.toStruct());

        records.add(record);

        lastPollMs = time.milliseconds();

        return records;

    }

    @Override
    public void stop() {
        log.info("Stopping Random source task");
        running.set(false);
    }
}
