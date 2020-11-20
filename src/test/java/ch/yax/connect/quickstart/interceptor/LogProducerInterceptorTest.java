package ch.yax.connect.quickstart.interceptor;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LogProducerInterceptorTest {

    @Mock
    private ProducerRecord record;

    @Mock
    private RecordMetadata metadata;


    @Test
    void testLogging() {

        final LogProducerInterceptor logProducerInterceptor = new LogProducerInterceptor();
        logProducerInterceptor.configure(new HashMap<>());
        logProducerInterceptor.onSend(record);

        verify(record, times(1)).topic();
        verify(record, times(1)).partition();
        verify(record, times(1)).timestamp();

        logProducerInterceptor.onAcknowledgement(metadata, null);

        logProducerInterceptor.close();

    }

}
