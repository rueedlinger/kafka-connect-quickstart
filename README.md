# Ready, Steady, Connect - A Kafka Connect Quickstart

*'Ready, Steady, Connect - A Kafka Connect Quickstart'* is an example project to play around
with [Apache Kafka Connect](https://kafka.apache.org/documentation/#connect). This project contains examples and tools
to develop, inspect and deploy Kafka Connect plugins (connectors, transforms, etc.) from a Java Maven project.

This project uses the following versions:

- Confluent Platform 6.2.1 (Docker images)
- Kafka 2.8
- Java 11

The following components are part of the quickstart project:

- The **Docker image** ([Dockerfile](Dockerfile)) has two parts. A *builder image* to build the example connector from
  the Java source code and the *main image* to run a Kafka Connect container with all the Kafka Connect plugin examples
  and some plugins from conlfuent-hub.
- The **Java source code** ([src](src)) of all the Kafka Connect plugin (connectors, transforms, etc.) examples.
- The *Docker Compose* ([docker-compose.yml](docker-compose.yml)) for setting up and running the whole infrastructure (
  Kafka broker, zookeeper, etc).

The following tools are available when you run the whole infrastructure with Docker Compose:

- **[Kafdrop](https://github.com/obsidiandynamics/kafdrop) (Obsidian Dynamics)** is a web UI for viewing Kafka topics
  and browsing consumer groups.
- **[Kafka Connect UI](https://github.com/lensesio/kafka-connect-ui) (Lenses.io)** is a web tool for Kafka Connect for
  setting up and managing connectors for multiple connect clusters.
- **[Kafka UI](https://github.com/provectus/kafka-ui) (Provectus)** is a web UI for monitoring and management of Apache
  Kafka clusters.

CI Build:

- Builds the Java code and Docker
  image. ![Build Java & Docker](https://github.com/rueedlinger/kafka-connect-quickstart/workflows/Build%20Java%20&%20Docker/badge.svg)

## Getting Started

### Build and Startup the Environment

To use the custom sink and source connectors we have to build and start the Docker containers.

```bash
docker-compose up --build
```

This will start the following Docker containers:

- `zookeeper` => Apache Zookeeper (`confluentinc/cp-zookeeper`)
- `broker` => Apache Kafka (`confluentinc/cp-kafka`)
- `schema-registry`=> Confluent Schema Registry (`confluentinc/cp-schema-registry`)
- `connect`=> Kafka Connect. This services uses a [custom Docker image](Dockerfile) which is based
  on `confluentinc/cp-kafka-connect-base`.
- `kafdrop`=> [Kafdrop](https://github.com/obsidiandynamics/kafdrop) – Kafka Web UI  (`obsidiandynamics/kafdrop`)
- `connect-ui` => [Kafka Connect UI](https://github.com/lensesio/kafka-connect-ui) from
  Lenses.io (`landoop/kafka-connect-ui`)
- `kafka-ui` => [Kafka UI](https://github.com/provectus/kafka-ui) from Provectus (`provectuslabs/kafka-ui`)

> **Note** The docker containers have all the prefix *quickstart*. To get
> the logs from the container *connect* just run `docker logs quickstart-connect`


When all containers are started you can access different services like

- **Kafka Connect Rest API** => http://localhost:8083/
- **Kafdrop** => http://localhost:8082/
- **Schema Registry** => http://localhost:8081/
- **Kafka UI** from Provectus => http://localhost:8080/
- **Kafka Connect UI** from Lenses.io => http://localhost:8000/

As default [Avro](https://avro.apache.org/) will be used as value and key convertor. If you want to change the default
settings just adapt the [docker-compose.yml](docker-compose.yml)
file for the Kafka Connect service or override the settings in connector config.

```yaml
environment:
  CONNECT_KEY_CONVERTER: io.confluent.connect.avro.AvroConverter
  CONNECT_KEY_CONVERTER_SCHEMA_REGISTRY_URL: http://schema-registry:8081

  CONNECT_VALUE_CONVERTER: io.confluent.connect.avro.AvroConverter
  CONNECT_VALUE_CONVERTER_SCHEMA_REGISTRY_URL: http://schema-registry:8081
```

Or set the converter in connector config:

```json
{
  "name": "random-source-schemaless",
  "config": {
    "key.converter": "org.apache.kafka.connect.storage.StringConverter",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter"
  }
}
```

### Let’s Deploy Some Connectors

First we have to check if Kafka Connect is available.

```bash
curl http://localhost:8083/
```

When Kafka Connect is up and running you should see a response like this.

```json
{
  "version": "6.1.0-ccs",
  "commit": "5496d92defc9bbe4",
  "kafka_cluster_id": "614erc9tQx6LxGXtzBvh9w"
}
```

In the [config](config) directory are the connector configuration files for the custom source and sink connector.

- [LogSinkConnector](src/main/java/ch/yax/connect/quickstart/sink)
- [RandomSourceConnector](src/main/java/ch/yax/connect/quickstart/source)

#### Source Connector

##### Avro

This will install the `RandomSourceConnector` [(random-source-avro.json)](config/random-source-avro.json)
which publishes random data in the Kafka topic `random-data-avro`.

> **Note** In our configuration Avro is set as default. So we don't have to set the `value.converter` in the connector configuration.

```bash
curl -X POST http://localhost:8083/connectors  \
    -H "Content-Type: application/json" \
    --data @config/random-source-avro.json
```

##### JSON (embedded schema)

The same source can be deployed with a JSON converter.

```properties
"value.converter":"org.apache.kafka.connect.json.JsonConverter",
"value.converter.schemas.enable":"true",
```

Here the message will contain the `schema` and `payload` as top-level elements in the JSON.

```bash
curl -X POST http://localhost:8083/connectors  \
    -H "Content-Type: application/json" \
    --data @config/random-source-json.json
```

##### JSON (schemaless)

Or as JSON without a schema.

```properties
"value.converter":"org.apache.kafka.connect.json.JsonConverter",
"value.converter.schemas.enable":"false",
```

This will only contain the raw JSON message.

```bash
curl -X POST http://localhost:8083/connectors  \
    -H "Content-Type: application/json" \
    --data @config/random-source-schemaless.json
```

#### Sink Connector

##### Avro

With the following command we install the `LogSinkConnector` [(log-sink-avro.json)](config/log-sink-avro.json)
which will log the data from the Kafka topic `random-data` to the console.

> **Note** In our configuration Avro is set as default. So we don't have to set the `value.converter` in the connector configuration.

```bash
curl -X POST http://localhost:8083/connectors \
    -H "Content-Type: application/json" \
    --data @config/log-sink-avro.json
```

##### JSON (embedded schema)

The same sink can be deployed with JSON converter. Here the message will contain the `schema` and `payload` top-level
elements in the JSON.

```bash
curl -X POST http://localhost:8083/connectors \
    -H "Content-Type: application/json" \
    --data @config/log-sink-json.json
```

##### JSON (schemaless)

Or as JSON without a schema.

```bash
curl -X POST http://localhost:8083/connectors \
    -H "Content-Type: application/json" \
    --data @config/log-sink-schemaless.json
```

#### Let’s See What We Got!

With the following command you should se all the connectors we have deployed so fare.

```bash
curl http://localhost:8083/connectors
```

When everything went well yo should see an output like this:

```json
[
  "random-source-json",
  "random-source-schemaless",
  "random-source-avro",
  "log-sink-json",
  "log-sink-avro",
  "log-sink-schemaless"
]
```

We can also display the state and configuration of all connectors with one simple command.

```bash
curl "http://localhost:8083/connectors?expand=status&expand=info"
```

A detail description of the Kafka Connect Rest API can be found here

- https://kafka.apache.org/documentation/#connect_rest
- https://docs.confluent.io/current/connect/references/restapi.html

### How to Install Other Connectors

If you want to install a special Connect plugin you have three options:

1. Download the JAR file and copy it in the [mount](mount) directory. This directory will be automatically mounted as
   Docker volume to the Kafka Connect plugin path `/etc/kafka-connect/jars`
   (`CONNECT_PLUGIN_PATH`).

```yaml
services:

  connect:

    environment:
      CONNECT_PLUGIN_PATH: "/usr/share/java,/usr/share/confluent-hub-components,/etc/kafka-connect/jars"

    volumes:
      - ./mount:/etc/kafka-connect/jars

```

2. Modify the [Dockerfile](Dockerfile) and install the plugin with the `confluent-hub` CLI.

```dockerfile
FROM confluentinc/cp-kafka-connect-base:6.0.1

RUN confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:0.4.0
```

>

3. Build a connector plugin fat jar with Maven and add it to Kafka Connect plugin path.

```dockerfile
FROM confluentinc/cp-kafka-connect-base:6.0.1

COPY target/connect-quickstart-*.jar /usr/share/java/quickstart
```

## Example Plugins (Java)

Here are some examples of **Kafka Connect Plugins** which can be used to build your own plugins:

- **Sink Connector** - loading data from kafka and store it into an external system (eg. database).
- **Source Connector** - loading data from an external system and store it into kafka.
- **Single Message Transforms (SMTs)** - transforms a message when processed with a connector.
- **Predicates** - Transforms can be configured with a predicate so that transforms only applies when the condition
  is `true` ([KIP-585](https://cwiki.apache.org/confluence/display/KAFKA/KIP-585%3A+Filter+and+Conditional+SMTs)).
- **Config Providers** - can load configurations for the connectors from external resources.
- **Rest Extensions** - with the Connect Rest Extension
  Plugin ([KIP-285](https://cwiki.apache.org/confluence/display/KAFKA/KIP-285%3A+Connect+Rest+Extension+Plugin)) you can
  extend the existing Rest API.
- **Converters** - provide support for translating between Kafka Connect's runtime data format and the raw payload of
  the Kafka messages.

Here are some examples of the general **Kafka Plugins** which can be used with Kafka Connect:

- **Kafka Consumer** - the Producer / Consumer
  Interceptors ([KIP-42](https://cwiki.apache.org/confluence/display/KAFKA/KIP-42%3A+Add+Producer+and+Consumer+Interceptors))
  can be used to intercept Kafka messages. These are part of the Kafka Client API and not Connect Plugins, but can be
  used to extend Kafka Connect.
- **Producer Interceptors** - the Producer / Consumer
  Interceptors ([KIP-42](https://cwiki.apache.org/confluence/display/KAFKA/KIP-42%3A+Add+Producer+and+Consumer+Interceptors))
  can be used to intercept Kafka messages. These are part of the Kafka Client API and not Connect Plugins, but can be
  used to extend Kafka Connect.
- **Metrics Reporter** - can be configured to report metrics from Kafka Connect.

### Source Connector

The [`RandomSourceConnector`](src/main/java/ch/yax/connect/quickstart/source) will create random data. The output data
could look like this:

```json
{
  "value": 5860906703091898043,
  "count": 34,
  "message": "Task Id: 0",
  "timestamp": "2020-11-06T18:28:31.314616Z"
}
```

The following configuration options are possible.

- `topic` - the kafka topic where the data will be published.
- `poll.interval.ms` - the interval in milliseconds the polling of data should happen. The default is 1000 milliseconds.

### Sink Connector

#### LogSinkConnector

The [`LogSinkConnector`](src/main/java/ch/yax/connect/quickstart/sink) will log the output of a message. The following
configuration options exists.

- `log.level: [INFO, 'DEBUG', 'WARN', 'ERROR', 'TRACE']`. The log level which should be used. Default is `INFO`.
- `log.content: [ALL, 'KEY', 'VALUE', 'KEY_VALUE']`. Which part of the message should be logged the key, the value, key
  & value or the whole record (ALL). Default is `ALL`.
- `log.format`. The format of the log message. Default is `{} {}`.

### Single Message Transforms (SMTs)

Single Message Transformations (SMTs) are applied to messages as they go through Connect.

#### UUIDField

The [`UUIDField`](src/main/java/ch/yax/connect/quickstart/transforms) transforms adds a UUID field to the record. This
transform can be used to add a UUID as key or value to the Kafka message.

```properties
"transforms":"UUIDField",
"transforms.UUIDField.type":"ch.yax.connect.quickstart.transforms.UUIDField$Value",
"transforms.UUIDField.field":"my-uuid"
```

Before the transformation the message might look like this.

```json
{
  "value": 5860906703091898043,
  "count": 34
}
```

After the transform the message contains the new field `my-uuid` with a generated uuid as value.

```json
{
  "value": 5860906703091898043,
  "count": 34,
  "my-uuid": "de32f3bf-b65a-41ab-a9c3-db4956a4e7db"
}
```

### Predicates

####   

Transformations can be configured with predicates so that the transformation is applied only to records which satisfy a
condition. The [`EqualsField`](src/main/java/ch/yax/connect/quickstart/predicates) Predicate will test a spefic vale of
field and apply the transformation only when the value is equal to value set by `expected.value`.

For example to enable the Predicate for the existing transform `UUIDField` you add the following configuration to your
connector. This will add the `UUIDField` transform only when the value in the `message` field ìs equals to the expected
value `task id: 0`. With `ignore.case=true` set to true lowercase and uppercase letters are treated as equivalent.

```properties
transforms.UUIDField.predicate=EqualsField
predicates=EqualsField
predicates.EqualsField.type=ch.yax.connect.quickstart.predicates.EqualsField$Value
predicates.EqualsField.field=message
predicates.EqualsField.expected.value=task id: 0
predicates.EqualsField.ignore.case=true
```

### Config Provider

#### EnvironmentConfigProvider

The [`EnvironmentConfigProvider`](src/main/java/ch/yax/connect/quickstart/config/provider/EnvironmentConfigProvider.java)
can be used to access Environment variables from the connector config.

> **Note:** to register a Kafka Connect Config Provider you need to add the file [`org.apache.kafka.common.config.provider.ConfigProvider`](src/main/resources/META-INF/services)
in `META-INF/services` which contains the full class name of your config provider.

The config provider `EnvironmentConfigProvider` supports the following config parameters:

- `BLACKLIST` a list of env variables which should be filtered out. When set to `"foo,bar"`

```yaml
CONNECT_CONFIG_PROVIDERS: "env"
CONNECT_CONFIG_PROVIDERS_ENV_CLASS: "ch.yax.connect.quickstart.config.provider.EnvironmentConfigProvider"
CONNECT_CONFIG_PROVIDERS_ENV_PARAM_BLACKLIST: "foo,bar"
```

With the pattern `${<CONFIG_RROVIDER>:<PATH>:<KEY>}` you can access the config values from your config
with `${env:my-path:my-value}`. Note the `path` is ignored by `EnvironmentConfigProvider` and has no effect.

You could use the env `CONFIG_POLL_INTERVAL_MS` to set the configuration property `poll.interval.ms` in
the `RandomSourceConnector`
configuration.

```properties
connector.class=ch.yax.connect.quickstart.source.RandomSourceConnector
max.interval.ms=${env:CONFIG_POLL_INTERVAL_MS}
tasks.max=1
topic=foo
```

### Rest Extension

#### HealthExtension

The [`HealthExtension`](src/main/java/ch/yax/connect/quickstart/rest) is Rest extension which provides a health
API (http://localhost:8083/health)
to Kafka Connect.

The response from the health API (`/health`) might look something like this:

```json
{
  "status": "UP",
  "connectors": [
    {
      "status": "UP",
      "connectorName": "log-sink",
      "connectorType": "sink",
      "connectorState": "RUNNING",
      "connectorWorkerId": "connect:8083",
      "connectorErrors": null,
      "tasks": [
        {
          "taskId": 0,
          "status": "UP",
          "taskState": "RUNNING",
          "tasksWorkerId": "connect:8083",
          "taskErrors": null
        }
      ]
    },
    {
      "status": "UP",
      "connectorName": "random-source",
      "connectorType": "source",
      "connectorState": "RUNNING",
      "connectorWorkerId": "connect:8083",
      "connectorErrors": null,
      "tasks": [
        {
          "taskId": 0,
          "status": "UP",
          "taskState": "RUNNING",
          "tasksWorkerId": "connect:8083",
          "taskErrors": null
        }
      ]
    }
  ]
}
```

> **Note:** to register a Kafka Connect Rest Extension you need to add the file [`org.apache.kafka.connect.rest.ConnectRestExtension`](src/main/resources/META-INF/services)
in `META-INF/services` which contains the full class name of your rest extension.


To enable the Connect Rest Extensions add the following configuration:

```yaml
CONNECT_REST_EXTENSION_CLASSES: "ch.yax.connect.quickstart.rest.HealthExtension"
```

### Converter

#### AvroDebugConvertor

The [`AvroDebugConvertor`](src/main/java/ch/yax/connect/quickstart/converter) is a Wrapper around
the `io.confluent.connect.avro.AvroConverter`. The `AvroDebugConvertor` will just log the internal connect data
structure which was created or received.

```yaml
CONNECT_VALUE_CONVERTER: ch.yax.connect.quickstart.converter.AvroDebugConverter
```

This will display a log output the data structure which was created.

```bash
quickstart-connect | [2021-02-10 22:09:22,907] INFO Topic random-data-avro, created connect data 'SchemaAndValue{schema=Schema{ch.yax.connect.quickstart.source.RandomData:STRUCT}, value=Struct{value=7476346340551272290,count=632,message=Task Id: 1,timestamp=Wed Feb 10 22:09:22 GMT 2021}}' (ch.yax.connect.quickstart.converter.AvroDebugConverter)
```

It will also log the data structure which was received.

```bash
quickstart-connect | [2021-02-10 22:09:28,377] INFO Topic random-data-avro, got connect data 'Struct{value=6085501730739912467,count=633,message=Task Id: 1,timestamp=Wed Feb 10 22:09:28 GMT 2021}' and schema 'Schema{ch.yax.connect.quickstart.source.RandomData:STRUCT}' (ch.yax.connect.quickstart.converter.AvroDebugConverter)
```

#### JsonDebugConverter

A similar converter (`JsonDebugConverter`) exists also for the`org.apache.kafka.connect.json.JsonConverter`.

```properties
value.converter=ch.yax.connect.quickstart.converter.JsonDebugConverter
```

### Consumer / Producer Interceptor

#### LogConsumerInterceptor

The [`LogConsumerInterceptor`](src/main/java/ch/yax/connect/quickstart/interceptor) logs part of the Kafka message
(topic, timestamp, partition and offset).

To enable the `LogConsumerInterceptor` add the following configuration.

> *Note*: In some cases you might add the JAR with your Interceptor into the `CLASSPATH` environment variable.
> Because adding your Interceptors into the `CONNECT_PLUGIN_PATH` might not work.

```yaml
CONNECT_CONSUMER_INTERCEPTOR_CLASSES: "ch.yax.connect.quickstart.interceptor.LogConsumerInterceptor"
```

#### LogProducerInterceptor

The [`LogProducerInterceptor`](src/main/java/ch/yax/connect/quickstart/interceptor)  logs part of the Kafka message
topic, timestamp and partition) before it's stored in the topic.

To enable the `LogProducerInterceptor` add the following configuration.

```yaml
CONNECT_PRODUCER_INTERCEPTOR_CLASSES: "ch.yax.connect.quickstart.interceptor.LogProducerInterceptor"
```

*Note*: In some cases you might add the JAR with your Interceptor into the `CLASSPATH` environment variable. Because
adding your Interceptors to the `CONNECT_PLUGIN_PATH` might not work.

```dockerfile
ENV CLASSPATH=/usr/share/java/quickstart/*
```

### Metric Reporter

#### LogMetricsReporter

The [`LogMetricsReporter`](src/main/java/ch/yax/connect/quickstart/metrics) will log various metrics from Kafka Connect.

To enable the `LogMetricsReporter` add the following configuration.

```yaml
CONNECT_METRIC_REPORTERS: "ch.yax.connect.quickstart.metrics.LogMetricsReporter"
```

You have to add the JAR of the Metrics Report in the Java classpath. Because adding your JAR to
the `CONNECT_PLUGIN_PATH` will not work. If you don't put the JAR in the classpath you get a `ClassNotFoundException`.
For a production case you should separate Kafka Connect plugins from the `MetricsReporter`s.

```dockerfile
ENV CLASSPATH=/usr/share/java/quickstart/*
```

When you have some connectors running you might see some log statement like this.

```bash
[2021-02-10 23:09:41,815] INFO metricChange: name: MetricName [name=record-send-total, group=producer-topic-metrics, description=The total number of records sent for a topic., tags={client-id=producer-1, topic=docker-connect-offsets}], value: 0.0 (ch.yax.connect.quickstart.metrics.LogMetricsReporter)
```

## References

- [Apache Kafka Connect](https://kafka.apache.org/documentation/#connect)
- [Confluent Platform Kafka Connect](https://docs.confluent.io/current/connect/index.html)
- [Docker images for Kafka](https://github.com/confluentinc/kafka-images)
- [Confluent Supported Versions and Interoperability](https://docs.confluent.io/platform/current/installation/versions-interoperability.html)
