package ch.yax.connect.quickstart.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.json.JsonConverter;

@Slf4j
public class JsonDebugConverter extends JsonConverter {

    @Override
    public byte[] fromConnectData(final String topic, final Schema schema, final Object value) {
        log.info("Topic {}, got connect data '{}' and schema '{}'", topic, value, schema);
        return super.fromConnectData(topic, schema, value);
    }

    @Override
    public byte[] fromConnectData(final String topic, final Headers headers, final Schema schema, final Object value) {
        log.info("Topic {}, got connect data '{}' and schema '{}'", topic, value, schema);
        return super.fromConnectData(topic, headers, schema, value);
    }

    @Override
    public SchemaAndValue toConnectData(final String topic, final byte[] value) {
        final SchemaAndValue obj = super.toConnectData(topic, value);
        log.info("Topic {}, created connect data '{}'", topic, obj);
        return obj;
    }

    @Override
    public SchemaAndValue toConnectData(final String topic, final Headers headers, final byte[] value) {
        final SchemaAndValue obj = super.toConnectData(topic, headers, value);
        log.info("Topic {}, created connect data '{}'", topic, obj);
        return obj;
    }
}
