package ch.yax.connect.quickstart.transforms;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SchemaUtil;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.kafka.connect.transforms.util.Requirements.requireMap;
import static org.apache.kafka.connect.transforms.util.Requirements.requireStruct;

@Slf4j
public abstract class UUIDField<R extends ConnectRecord<R>> implements Transformation<R> {

    private static final String FIELD_DEFAULT_NAME = "uuid";
    private static final String FIELD_CONFIG = "field";
    private static final String PURPOSE = "insert UUID field";

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(FIELD_CONFIG,
                    ConfigDef.Type.STRING,
                    FIELD_DEFAULT_NAME,
                    ConfigDef.Importance.MEDIUM,
                    "The name of the UUID field to inject.");


    private String fieldName;

    @Override
    public void configure(final Map<String, ?> props) {
        final SimpleConfig config = new SimpleConfig(CONFIG_DEF, props);
        log.debug("configure transform with config {}", props);
        fieldName = config.getString(FIELD_CONFIG);
    }

    @Override
    public R apply(final R record) {
        final Schema schema = operatingSchema(record);
        final String uudi = UUID.randomUUID().toString();
        log.debug("add uuid '{}' to record {} with schema {}", uudi, record, schema);
        if (schema == null) {
            // no schema
            final Map<String, Object> value = requireMap(operatingValue(record), PURPOSE);
            final Map<String, Object> updatedValue = new HashMap<>(value);
            updatedValue.put(fieldName, uudi);
            return newRecord(record, null, updatedValue);
        } else {
            // with schema
            final Struct value = requireStruct(operatingValue(record), PURPOSE);
            final SchemaBuilder builder = SchemaUtil.copySchemaBasics(schema, SchemaBuilder.struct());
            for (final Field field : schema.fields()) {
                builder.field(field.name(), field.schema());
            }
            builder.field(fieldName, Schema.STRING_SCHEMA);

            final Schema updatedSchema = builder.build();
            final Struct updatedValue = new Struct(updatedSchema);
            updatedValue.put(fieldName, uudi);

            for (final Field field : value.schema().fields()) {
                updatedValue.put(field.name(), value.get(field));
            }

            return newRecord(record, updatedSchema.schema(), updatedValue);
        }
    }

    @Override
    public void close() {
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    protected abstract Schema operatingSchema(R record);

    protected abstract Object operatingValue(R record);

    protected abstract R newRecord(R record, Schema updatedSchema, Object updatedValue);


    public static class Key<R extends ConnectRecord<R>> extends UUIDField<R> {
        @Override
        protected Schema operatingSchema(final R record) {
            return record.keySchema();
        }

        @Override
        protected Object operatingValue(final R record) {
            return record.key();
        }

        @Override
        protected R newRecord(final R record, final Schema updatedSchema, final Object updatedValue) {
            return record.newRecord(record.topic(), record.kafkaPartition(), updatedSchema, updatedValue, record.valueSchema(), record.value(), record.timestamp());
        }
    }

    public static class Value<R extends ConnectRecord<R>> extends UUIDField<R> {
        @Override
        protected Schema operatingSchema(final R record) {
            return record.valueSchema();
        }

        @Override
        protected Object operatingValue(final R record) {
            return record.value();
        }

        @Override
        protected R newRecord(final R record, final Schema updatedSchema, final Object updatedValue) {
            return record.newRecord(record.topic(), record.kafkaPartition(), record.keySchema(), record.key(), updatedSchema, updatedValue, record.timestamp());
        }
    }

}
