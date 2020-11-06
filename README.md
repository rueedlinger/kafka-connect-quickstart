# Kafka Connect Quickstart
This is an example project to play around with [Apache Kafka Connect](https://kafka.apache.org/documentation/#connect) 
and to deploy a Kafka Connect source and sink connector from a Java Maven project. This quickstart example uses the following 
versions:
- Confluent Platform 6.0.0 
- Kafka 2.6
- Java 11

The following components are part of the quickstart project:
- The *Docker* image ([Dockerfile](Dockerfile)) will be used to run a Kafka Connect container 
with all the required Kafka Connect plugins.
- The Java project with source code ([src](src)) examples for a custom source and sink connector.
- With *Docker Compose* ([docker-compose.yml](docker-compose.yml)) the whole infrastructure 
(Kafka broker, zookeeper, etc) can be easily run to play around with the custom Kafka Connect image
and the source and sink connectors from the Java project.

## CI Build
- Builds the Java code and Docker image. ![Build Java & Docker](https://github.com/rueedlinger/kafka-connect-quickstart/workflows/Build%20Java%20&%20Docker/badge.svg)

## Getting Started
### Build and Startup the Environment
To use the custom sink and source connectors we have to build and start the Docker containers. 

```
docker-compose up --build
```

This will start the following Docker containers:
- `zookeeper` => Apache Zookeeper (`confluentinc/cp-zookeeper`)
- `broker` => Apache Kafka (`confluentinc/cp-kafka`)
- `schema-registry`=> Schema Registry (`confluentinc/cp-schema-registry`)
- `connect`=> Kafka Connect. This services uses a [custom Docker image](Dockerfile) which is based on `confluentinc/cp-kafka-connect-base`.
- `kafdrop`=> Kafdrop – Kafka Web UI  (`obsidiandynamics/kafdrop`)
- `connect-ui` => Kafka Connect UI from Lenses.io (`landoop/kafka-connect-ui`)

When all containers are started you can access different services like 
- **Kafka Connect Rest API** => http://localhost:8083/
- **Kafdrop** => http://localhost:8082/
- **Schema Registry** => http://localhost:8081/
- **kafka-connect-ui** from Lenses.io  => http://localhost:8000/


By default, Apache Avro convertor will be used when nothing else is set for value or key convertor in the connector settings. 
If you want to change the default settings just adapt the [docker-compose.yml](docker-compose.yml ) file for the Kafka Connect service.

```
environment:
  CONNECT_KEY_CONVERTER: io.confluent.connect.avro.AvroConverter
  CONNECT_KEY_CONVERTER_SCHEMA_REGISTRY_URL:  http://schema-registry:8081

  CONNECT_VALUE_CONVERTER: io.confluent.connect.avro.AvroConverter
  CONNECT_VALUE_CONVERTER_SCHEMA_REGISTRY_URL: http://schema-registry:8081
```


### Let’s Deploy Some Connectors
First we have to check if Kafka Connect is available.
```
curl http://localhost:8083/
```

When Kafka Connect is up and running you should see a response like this.

```json
{
  "version": "6.0.0-ccs",
  "commit": "17b744c31e00868b",
  "kafka_cluster_id": "nj5o6tIHQIawd0muagtQmQ"
}
```


In the [config](config) directory are the configuration files for the custom source and sink connector. 
- [LogSinkConnector](src/main/java/ch/yax/connect/quickstart/sink)
- [RandomSourceConnector](src/main/java/ch/yax/connect/quickstart/source)


This will install the `RandomSourceConnector` [(random-source.json)](config/random-source.json) 
which publishes random data in the Kafka topic `random-data`.

```
curl -X POST http://localhost:8083/connectors  \
    -H "Content-Type: application/json" \
    --data @config/random-source.json
```


With the following command we install the `LogSinkConnector` [(log-sink.json)](config/log-sink.json) 
which will log the data from the Kafka topic `random-data` to the console.

```
curl -X POST http://localhost:8083/connectors \
    -H "Content-Type: application/json" \
    --data @config/log-sink.json
```

> A detail description of the Kafka Connect Rest API can be found here, https://docs.confluent.io/current/connect/references/restapi.html


### How to Install Other Connectors

If you want a special Connect plugin installed you have three options:

1. Download the JAR file and copy it in the [mount](mount) directory. This directory will be 
automatically mounted as Docker volume to the Kafka Connect plugin path `/etc/kafka-connect/jars` 
(`CONNECT_PLUGIN_PATH`).

```
services:
  
  connect:
   
    environment:
      CONNECT_PLUGIN_PATH: "/usr/share/java,/usr/share/confluent-hub-components,/etc/kafka-connect/jars"
    
    volumes:
      - ./mount:/etc/kafka-connect/jars

```


2. Modify the [Dockerfile](Dockerfile) and install a plugin with the `confluent-hub` CLI.

```
FROM confluentinc/cp-kafka-connect-base:6.0.0

RUN confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:0.4.0
```
> 

3. Build a connector plugin fat jar with Maven and add it to Kafka Connect plugin path.
```
FROM confluentinc/cp-kafka-connect-base:6.0.0

COPY target/connect-quickstart-*.jar /usr/share/java
```

## Examples
Here are some examples of Kafka Connect Plugins which can be used to build your own plugins:
- **Sink Connector** - loading data from kafka and store it into an external system (eg. database).
- **Source Connector** - loading data from an external system and store it into kafka.
- **Single Message Transforms (SMTs)** - transforms a message when processing with a connector.
- **Predicates** - Transforms cn be configured with a predicate so that transforms only applies when the 
condition was satisfied.
- **Config Providers** - loads configurations for the connector from external resources.

### Source Connector
The `RandomSourceConnector` will create random data. The output data could look like this:

```json
{"value":  5860906703091898043, "count":  34, "message":  "Task Id: 0", "timestamp": "2020-11-06T18:28:31.314616Z"}
```
The following configuration options are possible.
- `topic` - the kafka topic where the data will be published.
- `poll.interval.ms` - the interval in milliseconds the polling of data should happen. The default is 1000 milliseconds. 

### Sink Connector
#### LogSinkConnector
The `LogSinkConnector` will log the output of a message. The following configuration options exists.
- `log.level: [INFO, 'DEBUG', 'WARN', 'ERROR', 'TRACE']`. The log level which should be used. Default is `INFO`.
- `log.content: [ALL, 'KEY', 'VALUE', 'KEY_AVLUE']`. Which part of the message should be logged the key, the value, key & value or the 
whole record (ALL). Default is `ALL`.
- `log.format`. The format of the log message. Default is `{} {}`. 

### Single Message Transforms (SMTs)
Single Message Transformations (SMTs) are applied to messages as they go through Connect.

tbd

### Predicates
Transformations can be configured with predicates so that the transformation is applied only to records which satisfy a condition.

tbd


### Config Provider

#### EnvironmentConfigProvider
The [`EnvironmentConfigProvider`](src/main/java/ch/yax/connect/quickstart/config/provider/EnvironmentConfigProvider.java) can be used to access Environment variables from the connector config.

> **Note:** to register a Kafka Connect Config Provider you need to add the file [`org.apache.kafka.common.config.provider.ConfigProvider`](src/main/resources/META-INF/services)
in `META-INF/services` which contains the full class name of your config provider. 

The config provider `EnvironmentConfigProvider` supports the following config parameters:
- `BLACKLIST` a list of env variables which should be filtered out. When set to `"foo,bar"` 


```bash
CONNECT_CONFIG_PROVIDERS: "env"
CONNECT_CONFIG_PROVIDERS_ENV_CLASS: "ch.yax.connect.quickstart.config.provider.EnvironmentConfigProvider"
CONNECT_CONFIG_PROVIDERS_ENV_PARAM_BLACKLIST: "foo,bar"
```

With the pattern `${<CONFIG_RROVIDER>:<PATH>:<KEY>}` you can access the config values from your
config with `${env:my-path:my-value}`. Note the `path` is ignored by `EnvironmentConfigProvider` and has no effect. 

You could use the env `CONFIG_POLL_INTERVAL_MS` to set the configuration property `poll.interval.ms` in the `RandomSourceConnector`
configuration.

```properties
connector.class=ch.yax.connect.quickstart.source.RandomSourceConnector
max.interval.ms=${env:CONFIG_POLL_INTERVAL_MS}
tasks.max=1
topic=foo
```







## References

- [Apache Kafka Connect](https://kafka.apache.org/documentation/#connect)
- [Confluent Platform Kafka Connect](https://docs.confluent.io/current/connect/index.html)
- [Docker images for Kafka](https://github.com/confluentinc/kafka-images)
