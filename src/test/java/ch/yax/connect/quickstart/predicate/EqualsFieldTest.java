package ch.yax.connect.quickstart.predicate;

import ch.yax.connect.quickstart.predicates.EqualsField;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class EqualsFieldTest {

    private static final String RECORD_NAME = "ch.yax.connect.quickstart.Record";
    private static final String FIELD_1_NAME = "value-string";
    private static final String FIELD_1_VALUE = "value";
    private static final Schema FIELD_1_SCHEMA = Schema.STRING_SCHEMA;

    private static final String FIELD_2_NAME = "value-int";
    private static final int FIELD_2_VALUE = 123;
    private static final Schema FIELD_2_SCHEMA = Schema.INT32_SCHEMA;

    private static final String CONFIG_FIELD_NAME = "field";
    private static final String CONFIG_EXPECTED_VALUE = "expected.value";


    @Test
    void testValueSchemaless() {
        final EqualsField<SourceRecord> equalsField = new EqualsField.Value<>();

        Map<String, Object> values = Map.of(FIELD_1_NAME, FIELD_1_VALUE, FIELD_2_NAME, FIELD_2_VALUE);
        SourceRecord record = crateSourceRecord(null, values, null, null);
        equalsField.configure(Map.of(CONFIG_FIELD_NAME, FIELD_1_NAME, CONFIG_EXPECTED_VALUE, "no match"));
        assertThat(equalsField.test(record)).isFalse();


        values = Map.of(FIELD_1_NAME, FIELD_1_VALUE, FIELD_2_NAME, FIELD_2_VALUE);
        record = crateSourceRecord(null, values, null, null);
        equalsField.configure(Map.of(CONFIG_FIELD_NAME, FIELD_1_NAME, CONFIG_EXPECTED_VALUE, FIELD_1_VALUE));
        assertThat(equalsField.test(record)).isTrue();

    }

    @Test
    void testKeySchemaless() {
        final EqualsField<SourceRecord> equalsField = new EqualsField.Key<>();

        Map<String, Object> key = Map.of(FIELD_1_NAME, FIELD_1_VALUE, FIELD_2_NAME, FIELD_2_VALUE);
        SourceRecord record = crateSourceRecord(null, null, null, key);
        equalsField.configure(Map.of(CONFIG_FIELD_NAME, FIELD_1_NAME, CONFIG_EXPECTED_VALUE, "no match"));
        assertThat(equalsField.test(record)).isFalse();


        key = Map.of(FIELD_1_NAME, FIELD_1_VALUE, FIELD_2_NAME, FIELD_2_VALUE);
        record = crateSourceRecord(null, null, null, key);
        equalsField.configure(Map.of(CONFIG_FIELD_NAME, FIELD_1_NAME, CONFIG_EXPECTED_VALUE, FIELD_1_VALUE));
        assertThat(equalsField.test(record)).isTrue();

    }

    @Test
    void testValueWithSchema() {
        final EqualsField<SourceRecord> equalsField = new EqualsField.Value<>();


        equalsField.configure(Map.of(CONFIG_FIELD_NAME, FIELD_1_NAME, CONFIG_EXPECTED_VALUE, "no match"));
        SourceRecord record = crateSourceRecord(createSchema(), createStruct(FIELD_1_VALUE, FIELD_2_VALUE), null, null);
        assertThat(equalsField.test(record)).isFalse();


        equalsField.configure(Map.of(CONFIG_FIELD_NAME, FIELD_1_NAME, CONFIG_EXPECTED_VALUE, FIELD_1_VALUE));
        record = crateSourceRecord(createSchema(), createStruct(FIELD_1_VALUE, FIELD_2_VALUE), null, null);
        assertThat(equalsField.test(record)).isTrue();

    }

    @Test
    void testKeyWithSchema() {
        final EqualsField<SourceRecord> equalsField = new EqualsField.Key<>();


        equalsField.configure(Map.of(CONFIG_FIELD_NAME, FIELD_1_NAME, CONFIG_EXPECTED_VALUE, "foo"));
        SourceRecord record = crateSourceRecord(null, null, createSchema(), createStruct(FIELD_1_VALUE, FIELD_2_VALUE));
        assertThat(equalsField.test(record)).isFalse();

        equalsField.configure(Map.of(CONFIG_FIELD_NAME, FIELD_2_NAME, CONFIG_EXPECTED_VALUE, "foo"));
        record = crateSourceRecord(null, null, createSchema(), createStruct(FIELD_1_VALUE, FIELD_2_VALUE));
        assertThat(equalsField.test(record)).isFalse();

        equalsField.configure(Map.of(CONFIG_FIELD_NAME, FIELD_1_NAME, CONFIG_EXPECTED_VALUE, FIELD_1_VALUE));
        record = crateSourceRecord(null, null, createSchema(), createStruct(FIELD_1_VALUE, FIELD_2_VALUE));
        assertThat(equalsField.test(record)).isTrue();

        equalsField.configure(Map.of(CONFIG_FIELD_NAME, FIELD_2_NAME, CONFIG_EXPECTED_VALUE, String.valueOf(FIELD_2_VALUE)));
        record = crateSourceRecord(null, null, createSchema(), createStruct(FIELD_1_VALUE, FIELD_2_VALUE));
        assertThat(equalsField.test(record)).isTrue();

    }


    private Schema createSchema() {
        return SchemaBuilder.struct()
                .name(RECORD_NAME)
                .field(FIELD_1_NAME, FIELD_1_SCHEMA)
                .field(FIELD_2_NAME, FIELD_2_SCHEMA)
                .build();
    }

    private Struct createStruct(final String value1, final Integer value2) {
        return new Struct(createSchema())
                .put(FIELD_1_NAME, value1)
                .put(FIELD_2_NAME, value2);
    }

    private SourceRecord crateSourceRecord(final Schema schemaValue, final Object value, final Schema schemaKey, final Object key) {
        return new SourceRecord(new HashMap<>(), new HashMap<>(), "topic", 101, schemaKey, key, schemaValue, value);
    }

}
