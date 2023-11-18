ARG CONFLUENT_VERSION=7.5.2
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
FROM confluentinc/cp-kafka-connect-base:${CONFLUENT_VERSION}

# Install connector plugins with the confluent-hub cli
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:latest
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-s3:latest
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:latest
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-elasticsearch:latest

RUN confluent-hub install --no-prompt mongodb/kafka-connect-mongodb:latest
RUN confluent-hub install --no-prompt jcustenborder/kafka-connect-spooldir:latest

# Add the Maven build target to the Kafka Connect plugin path.
RUN mkdir /usr/share/java/quickstart
COPY --from=build-stage /app/target/connect-quickstart-*.jar /usr/share/java/quickstart
