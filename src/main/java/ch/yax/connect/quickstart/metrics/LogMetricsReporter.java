package ch.yax.connect.quickstart.metrics;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.metrics.MetricsReporter;

import java.util.List;
import java.util.Map;

@Slf4j
public class LogMetricsReporter implements MetricsReporter {
    @Override
    public void init(final List<KafkaMetric> metrics) {
        for (final KafkaMetric metric : metrics) {
            log.info("init: {}", toString(metric));
        }
    }

    @Override
    public void metricChange(final KafkaMetric metric) {
        log.info("metricChange: {}", toString(metric));
    }

    @Override
    public void metricRemoval(final KafkaMetric metric) {
        log.info("metricRemoval: {}", toString(metric));
    }

    @Override
    public void close() {
        log.info("close");
    }

    @Override
    public void configure(final Map<String, ?> configs) {
        log.info("configs: {}", configs);
    }

    private String toString(final KafkaMetric metric) {
        return "name: " + metric.metricName() + ", value: " + metric.metricValue();
    }
}
