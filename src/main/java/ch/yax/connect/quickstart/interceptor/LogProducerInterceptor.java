package ch.yax.connect.quickstart.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

@Slf4j
public class LogProducerInterceptor<K, V> implements ProducerInterceptor<K, V> {

    @Override
    public ProducerRecord<K, V> onSend(final ProducerRecord<K, V> record) {
        log.info("topic={}, partition={}, timestamp={}",
                record.topic(),
                record.partition(),
                record.timestamp()
        );
        return record;
    }

    @Override
    public void onAcknowledgement(final RecordMetadata metadata, final Exception exception) {
        log.info("ack meta={}, exception={}", metadata, exception);
    }

    @Override
    public void close() {
        log.info("closing");
    }

    @Override
    public void configure(final Map<String, ?> configs) {
        log.info("configure with configs {}", configs);
    }
}
