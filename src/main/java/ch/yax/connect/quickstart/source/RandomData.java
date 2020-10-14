package ch.yax.connect.quickstart.source;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Timestamp;

import java.time.Instant;
import java.util.Date;

@Builder
@Getter
@ToString
public class RandomData {
    private static final String FIELD_NAME_VALUE = "value";
    private static final String FIELD_NAME_COUNT = "count";
    private static final String FIELD_NAME_MESSAGE = "message";
    private static final String FIELD_NAME_TIMESTAMP = "timestamp";

    private final long value;
    private final long count;
    private final String message;
    private final Instant timestamp;

    public Struct toStruct() {
        return new Struct(toSchema())
                .put(FIELD_NAME_VALUE, value)
                .put(FIELD_NAME_COUNT, count)
                .put(FIELD_NAME_MESSAGE, message)
                .put(FIELD_NAME_TIMESTAMP, timestamp == null ? new Date() : Date.from(timestamp));
    }

    public Schema toSchema() {
        return SchemaBuilder.struct().name(RandomData.class.getName())
                .field(FIELD_NAME_VALUE, Schema.INT64_SCHEMA)
                .field(FIELD_NAME_COUNT, Schema.INT64_SCHEMA)
                .field(FIELD_NAME_MESSAGE, Schema.OPTIONAL_STRING_SCHEMA)
                .field(FIELD_NAME_TIMESTAMP, Timestamp.SCHEMA).build();
    }
}
