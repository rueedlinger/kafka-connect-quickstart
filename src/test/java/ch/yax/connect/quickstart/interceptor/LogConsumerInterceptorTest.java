package ch.yax.connect.quickstart.interceptor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LogConsumerInterceptorTest {


    @Mock
    private ConsumerRecord record;

    @Mock
    private TopicPartition topicPartition;


    @Test
    void testLogging() {

        final LogConsumerInterceptor<String, String> consumerInterceptor = new LogConsumerInterceptor<>();
        consumerInterceptor.configure(new HashMap<>());

        final ConsumerRecords records = new ConsumerRecords(Map.of(topicPartition, List.of(record)));
        consumerInterceptor.onConsume(records);

        verify(record, times(1)).topic();
        verify(record, times(1)).partition();
        verify(record, times(1)).timestamp();
        verify(record, times(1)).offset();

        consumerInterceptor.onCommit(new HashMap());
        consumerInterceptor.close();

    }

}
