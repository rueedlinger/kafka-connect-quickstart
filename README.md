# Kafka Connect Quickstart
This is an example project to play around with [Apache Kafka Connect](https://kafka.apache.org/documentation/#connect). 
This quickstart example uses the following versions to startup Docker containers with Docker Compose:
- Confluent Platform 6.0.0 
- Kafka 2.6
- Kafdrop 3.27.0

## Getting Started
### Build and Startup the Environment
First build and start all Docker containers. 

```
docker-compose up --build
```

his will start the following Docker containers:
- `zookeeper` => Apache Zookeeper (`confluentinc/cp-zookeeper`)
- `broker` => Apache Kafka (`confluentinc/cp-kafka`)
- `schema-registry`=> Schema Registry (`confluentinc/cp-schema-registry`)
- `connect`=> Kafka Connect. This services uses a [custom Docker image](Dockerfile) which is based on `confluentinc/cp-kafka-connect-base`.
- `kafdrop`=> Kafdrop – Kafka Web UI  (`obsidiandynamics/kafdrop`)

> **[Kafdrop](https://github.com/obsidiandynamics/kafdrop)** is a web UI for viewing Kafka topics and browsing consumer groups. 

When all containers are started you can access different services like 
- **Kafka Connect Rest API** => http://localhost:8083/
- **Schema Registry** => http://localhost:8081/
- **Kafdrop** => http://localhost:8082/


By default Apache Avro is used as convertor when nothing else is set for value or key convertor in the connector settings. 
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


In the [connectors](connectors) directory we have tow configuration files for a JSON and Avro source connector which uses the [Kafka Connect Datagen](https://github.com/confluentinc/kafka-connect-datagen) connectors to create some mock data for testing. 

The following example shows the `source-users-json` connector JSON configuration file which can be deployed with the Kafka Connect REST API.

```
{
    "name": "source-users-json",
    "config": {
      "connector.class": "io.confluent.kafka.connect.datagen.DatagenConnector",
      "kafka.topic": "users-json",
      "quickstart": "users",
      "key.converter": "org.apache.kafka.connect.storage.StringConverter",
      "value.converter": "org.apache.kafka.connect.json.JsonConverter",
      "value.converter.schemas.enable": "false",
      "max.interval": 1000,
      "iterations": 10000000,
      "tasks.max": "1"
    }
  }
```


This will install the source connector `source-users-json` [(users_json.json)](connectors/users_json.json) which publishes JSON data in the Kafka topic `users-json`.

```
curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" --data @connectors/users_json.json
```

With the following command we install the Avro source connector `source-users-avro` [(users_avro.json)](connectors/users_avro.json) which will publish Avro data in the Kafka topic `users-avro`.

```
curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" --data @connectors/users_json.json
```



> A detail description of the Kafka Connect Rest API can be found here, https://docs.confluent.io/current/connect/references/restapi.html


## How to Install Other Connectors

If you want a special Connect plugin installed you have two options:

1. Download the JAR file and copy it in the [mount](mount) directory. This directory is automatically mounted 
as Docker volume and configured as Connect plugin path (`CONNECT_PLUGIN_PATH`).

```
services:
  
  connect:
   
    environment:
      CONNECT_PLUGIN_PATH: "/usr/share/java,/usr/share/confluent-hub-components,/etc/kafka-connect/jars"
    
    volumes:
      - ./mount:/etc/kafka-connect/jars

```


2. Adapted the [Dockerfile](Dockerfile) and install a plugin with the `confluent-hub` CLI.

```
FROM confluentinc/cp-kafka-connect-base:6.0.0

RUN confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:0.4.0
```

## References

- [Apache Kafka Connect](https://kafka.apache.org/documentation/#connect)
- [Confluent Platform Kafka Connect](https://docs.confluent.io/current/connect/index.html)
- [Docker images for Kafka](https://github.com/confluentinc/kafka-images)