package ch.yax.connect.quickstart.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.Map;

@Slf4j
public class LogConsumerInterceptor<K, V> implements ConsumerInterceptor<K, V> {

    @Override
    public ConsumerRecords<K, V> onConsume(final ConsumerRecords<K, V> records) {

        records.forEach(record -> {
            log.info("topic={}, partition={} offset={}, timestamp={},",
                    record.topic(),
                    record.partition(),
                    record.offset(),
                    record.timestamp())
            ;
        });

        return records;
    }

    @Override
    public void close() {
        log.info("closing");
    }

    @Override
    public void onCommit(final Map offsets) {
        log.info("commit offsets={}", offsets);
    }

    @Override
    public void configure(final Map<String, ?> configs) {
        log.info("configure with configs {}", configs);
    }
}
