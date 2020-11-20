package ch.yax.connect.quickstart.predicates;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.transforms.predicates.Predicate;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

import java.util.Map;

import static org.apache.kafka.connect.transforms.util.Requirements.requireMap;
import static org.apache.kafka.connect.transforms.util.Requirements.requireStruct;

@Slf4j
public abstract class EqualsField<R extends ConnectRecord<R>> implements Predicate<R> {

    private static final String FIELD_CONFIG = "field";
    private static final String VALUE_CONFIG = "expected.value";
    private static final String IGNORE_CASE = "ignore.case";

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(FIELD_CONFIG,
                    ConfigDef.Type.STRING,
                    ConfigDef.Importance.HIGH,
                    "The name of the field")
            .define(VALUE_CONFIG,
                    ConfigDef.Type.STRING,
                    ConfigDef.Importance.HIGH,
                    "The expected value in the field.")
            .define(IGNORE_CASE,
                    ConfigDef.Type.BOOLEAN,
                    Boolean.FALSE,
                    ConfigDef.Importance.MEDIUM,
                    "ignore case.");

    private final String PURPOSE = "equals predicate";

    private String fieldName;

    private StringTestEquals testEquals;


    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public boolean test(final R record) {
        final Schema schema = operatingSchema(record);

        log.debug("test equals predicate on record {} with schema {}", record, schema);

        if (schema == null) {
            // no schema
            final Map<String, Object> value = requireMap(operatingValue(record), PURPOSE);
            return testEquals.test(value.get(fieldName));
        } else {
            // with schema
            final Struct value = requireStruct(operatingValue(record), PURPOSE);
            return testEquals.test(value.get(fieldName));
        }
    }

    @Override
    public void close() {
        log.debug("closed");
    }

    @Override
    public void configure(final Map<String, ?> props) {
        final SimpleConfig config = new SimpleConfig(CONFIG_DEF, props);
        log.debug("configure transform with properties {}", props);
        fieldName = config.getString(FIELD_CONFIG);
        final String expectedValue = config.getString(VALUE_CONFIG);
        final boolean ignoreCase = config.getBoolean(IGNORE_CASE);
        testEquals = new StringTestEquals(expectedValue, ignoreCase);
    }


    protected abstract Schema operatingSchema(R record);

    protected abstract Object operatingValue(R record);


    public static class Key<R extends ConnectRecord<R>> extends EqualsField<R> {
        @Override
        protected Schema operatingSchema(final R record) {
            return record.keySchema();
        }

        @Override
        protected Object operatingValue(final R record) {
            return record.key();
        }

    }

    public static class Value<R extends ConnectRecord<R>> extends EqualsField<R> {
        @Override
        protected Schema operatingSchema(final R record) {
            return record.valueSchema();
        }

        @Override
        protected Object operatingValue(final R record) {
            return record.value();
        }
    }

}
