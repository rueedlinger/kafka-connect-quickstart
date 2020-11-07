package ch.yax.connect.quickstart.transform;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@ExtendWith(MockitoExtension.class)
public class UUIDFieldTest {

    private static final String RECORD_NAME = "ch.yax.connect.quickstart.Record";
    private static final String FIELD_1_NAME = "foo";
    private static final String FIELD_1_VALUE = "value";
    private static final Schema FIELD_1_SCHEMA = Schema.STRING_SCHEMA;
    private static final String FIELD_2_NAME = "bar";
    private static final int FIELD_2_VALUE = 123;
    private static final Schema FIELD_2_SCHEMA = Schema.INT32_SCHEMA;
    private static final String FIELD_UUID = "uuid";


    @Test
    void testValueWithSchema() {

        final SourceRecord record = crateSourceRecord(createSchema(), createStruct(), null, null);

        final UUIDField.Value<SourceRecord> transform = new UUIDField.Value<>();
        transform.configure(new HashMap<>());

        final SourceRecord newRecord = transform.apply(record);
        final Struct newValue = (Struct) newRecord.value();

        assertThat(newValue.get(FIELD_1_NAME)).isEqualTo(FIELD_1_VALUE);
        assertThat(newValue.get(FIELD_2_NAME)).isEqualTo(FIELD_2_VALUE);
        assertThat(newValue.get(FIELD_UUID)).isNotNull();

    }

    @Test
    void testKeyWithSchema() {

        final SourceRecord record = crateSourceRecord(null, null, createSchema(), createStruct());

        final UUIDField.Key<SourceRecord> transform = new UUIDField.Key<>();
        transform.configure(new HashMap<>());

        final SourceRecord newRecord = transform.apply(record);
        final Struct newKey = (Struct) newRecord.key();

        assertThat(newKey.get(FIELD_1_NAME)).isEqualTo(FIELD_1_VALUE);
        assertThat(newKey.get(FIELD_2_NAME)).isEqualTo(FIELD_2_VALUE);
        assertThat(newKey.get(FIELD_UUID)).isNotNull();

    }

    @Test
    void testValueSchemaless() {

        final Map<String, Object> values = Map.of(FIELD_1_NAME, FIELD_1_VALUE, FIELD_2_NAME, FIELD_2_VALUE);

        final SourceRecord record = crateSourceRecord(null, values, null, null);

        final UUIDField.Value<SourceRecord> transform = new UUIDField.Value<>();
        transform.configure(new HashMap<>());

        final SourceRecord newRecord = transform.apply(record);
        final Map<String, Object> newValues = (Map<String, Object>) newRecord.value();
        assertThat(newValues.size()).isEqualTo(3);
        assertThat(newValues).contains(entry(FIELD_1_NAME, FIELD_1_VALUE));
        assertThat(newValues).contains(entry(FIELD_2_NAME, FIELD_2_VALUE));

        assertThat(newValues).containsKey(FIELD_UUID);
        assertThat(newValues.get(FIELD_UUID)).isNotNull();
    }

    @Test
    void testKeySchemaless() {

        final Map<String, Object> key = Map.of(FIELD_1_NAME, FIELD_1_VALUE, FIELD_2_NAME, FIELD_2_VALUE);
        final SourceRecord record = crateSourceRecord(null, null, null, key);

        final UUIDField.Key<SourceRecord> transform = new UUIDField.Key<>();
        transform.configure(new HashMap<>());

        final SourceRecord newRecord = transform.apply(record);
        final Map<String, Object> newKey = (Map<String, Object>) newRecord.key();
        assertThat(newKey.size()).isEqualTo(3);
        assertThat(newKey).contains(entry(FIELD_1_NAME, FIELD_1_VALUE));
        assertThat(newKey).contains(entry(FIELD_2_NAME, FIELD_2_VALUE));

        assertThat(newKey).containsKey(FIELD_UUID);
        assertThat(newKey.get(FIELD_UUID)).isNotNull();
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
