package ch.yax.connect.quickstart.transform;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UUIDFieldTest {

    private static final String RECORD_NAME = "ch.yax.connect.quickstart.Record";
    private static final String FIELD_1_NAME = "foo";
    private static final String FIELD_1_VALUE = "value";
    private static final Schema FIELD_1_SCHEMA = Schema.STRING_SCHEMA;
    private static final String FIELD_2_NAME = "bar";
    private static final int FIELD_2_VALUE = 123;
    private static final Schema FIELD_2_SCHEMA = Schema.INT32_SCHEMA;


    @Test
    void testValueWithSchema() {

        final SourceRecord record = crateSourceRecord(createSchema(), createStruct(), null, null);

        final UUIDField.Value<SourceRecord> value = new UUIDField.Value<>();
        value.configure(new HashMap<>());

        final SourceRecord newRecord = value.apply(record);
        assertThat(newRecord.value().toString()).contains("Struct{foo=value,bar=123,uuid=");

    }

    private Schema createSchema() {
        return SchemaBuilder.struct()
                .name(RECORD_NAME)
                .field(FIELD_1_NAME, FIELD_1_SCHEMA)
                .field(FIELD_2_NAME, FIELD_2_SCHEMA)
                .build();

    }

    private Struct createStruct() {
        return new Struct(createSchema())
                .put(FIELD_1_NAME, FIELD_1_VALUE)
                .put(FIELD_2_NAME, FIELD_2_VALUE);
    }

    private SourceRecord crateSourceRecord(final Schema schemaValue, final Object value, final Schema schemaKey, final Object key) {
        return new SourceRecord(new HashMap<>(), new HashMap<>(), "topic", 101, schemaKey, key, schemaValue, value);
    }

}
