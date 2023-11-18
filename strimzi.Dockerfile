ARG STRIMZI_VERSION=0.38.0
ARG KAFKA_VERSION=3.6.0
#########################################################
# Maven builder image to build the custom connectors
#########################################################
FROM maven:3.6.3-openjdk-11 as build-stage
COPY . /app
WORKDIR /app
RUN mvn -B clean package --file pom.xml

#########################################################
# Custom Kafka Connect Docker image
#########################################################
FROM quay.io/strimzi/kafka:${STRIMZI_VERSION}-kafka-${KAFKA_VERSION}

WORKDIR /tmp

USER root:root
RUN mkdir /opt/kafka/plugins/

# Install confluent-hub-client
RUN curl -LO https://client.hub.confluent.io/confluent-hub-client-latest.tar.gz
RUN tar -xvzf confluent-hub-client-latest.tar.gz --directory /usr/local/

# Confluent JDBC Connector
RUN confluent-hub install confluentinc/kafka-connect-jdbc:latest --component-dir /opt/kafka/plugins/ --worker-configs /opt/kafka/config/connect-distributed.properties --no-prompt

# Kafka Connect JSON Schema Converter
RUN confluent-hub install confluentinc/kafka-connect-json-schema-converter:7.5.2 --component-dir /opt/kafka/plugins/ --worker-configs /opt/kafka/config/connect-distributed.properties --no-prompt

# Kafka Connect Avro Converter
RUN confluent-hub install confluentinc/kafka-connect-avro-converter:7.5.2 --component-dir /opt/kafka/plugins/ --worker-configs /opt/kafka/config/connect-distributed.properties --no-prompt

# Kafka Connect Protobuf Converter
RUN confluent-hub install confluentinc/kafka-connect-protobuf-converter:7.5.2 --component-dir /opt/kafka/plugins/ --worker-configs /opt/kafka/config/connect-distributed.properties --no-prompt

# Add the Maven build target to the Kafka Connect plugin path.
RUN mkdir /opt/kafka/plugins/quickstart
COPY --from=build-stage /app/target/connect-quickstart-*.jar /opt/kafka/plugins/quickstart

# cleanup
RUN rm -rf *

WORKDIR /opt/kafka
USER 1001