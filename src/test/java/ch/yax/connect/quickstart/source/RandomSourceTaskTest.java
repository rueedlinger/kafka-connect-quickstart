package ch.yax.connect.quickstart.source;

import org.apache.kafka.connect.source.SourceTaskContext;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RandomSourceTaskTest {

    @Mock
    private SourceTaskContext context;

    @Mock
    private OffsetStorageReader offsetStorageReader;


    @Test
    void testPoll() {
        when(context.offsetStorageReader()).thenReturn(offsetStorageReader);

        final RandomSourceTasks tasks = createRandomSourceTasks();
        tasks.start(Map.of("topic", "foo", RandomSourceConfig.POLL_INTERVAL_CONFIG, "500"));

        // at least after one second we must have a result
        await().atMost(Durations.TWO_SECONDS)
                .until(() -> hasElements(tasks.poll()));

    }

    @Test
    void testStop() throws InterruptedException {
        when(context.offsetStorageReader()).thenReturn(offsetStorageReader);

        final RandomSourceTasks tasks = createRandomSourceTasks();
        tasks.start(Map.of("topic", "foo", RandomSourceConfig.POLL_INTERVAL_CONFIG, "100"));

        tasks.stop();

        assertThat(tasks.poll()).isNull();
        Thread.sleep(500);

        assertThat(tasks.poll()).isNull();

    }

    private RandomSourceTasks createRandomSourceTasks() {
        final RandomSourceTasks tasks = new RandomSourceTasks();
        tasks.initialize(context);
        return tasks;
    }

    private boolean hasElements(final List<?> values) {
        if (values == null) {
            return false;
        }
        return values.size() > 0;
    }

}
