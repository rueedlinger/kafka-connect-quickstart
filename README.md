# Kafka Connect Quickstart
This is an example project to play with [Apache Kafka Connect](https://kafka.apache.org/documentation/#connect). This quickstart example uses the following versions:
- Confluent Platform 6.0.0 (Docker images)
- Kafka 2.6

## Getting started
### Build and Startup the Environment
Just build and start all Docker containers.

```
docker-compose up --build
```
When all containers are started you can access different services like 
- Kafka Connect Rest API => http://localhost:8083/
- Schema Registry => http://localhost:8081/
- Kafdrop => http://localhost:8082/

> **[Kafdrop](https://github.com/obsidiandynamics/kafdrop)** is a web UI for viewing Kafka topics and browsing consumer groups. 

By default Avro is used as convertor when nothing else is set for value or key convertor in the connector settings. 
If you want change the default settings just adapt the [docker-compose.yml](docker-compose.yml ) file for the Kafka Connect service.


### Let’s Deploy Some Connectors
First we have to check if Kafka Connect is available.
```
curl http://localhost:8083/
```

You should see a response like this.

```json
{
  "version": "6.0.0-ccs",
  "commit": "17b744c31e00868b",
  "kafka_cluster_id": "nj5o6tIHQIawd0muagtQmQ"
}
```

Next we will install the [Kafka Connect Datagen](https://github.com/confluentinc/kafka-connect-datagen) source connectors. This is connector which creates mock data for testing.

This will install a source connector which create JSON data.
```
curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" --data @connectors/users_json.json
````

With the following command we install Avro source connector.
```
curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" --data @connectors/users_json.json
```


> A detail list of the Kafka Connect Rest API can be found here, https://docs.confluent.io/current/connect/references/restapi.html


## How to Install Other Connectors

If you want a special Connect plugin installed you have two options:

1. Download the JAR file and copy it in the [mount](mount) directory. This directory is automatically mounted 
as Docker volume and configured as Connect plugin path (`CONNECT_PLUGIN_PATH`).
2. Adapted the [Dockerfile](Dockerfile) and install a plugin with the `confluent-hub` CLI.