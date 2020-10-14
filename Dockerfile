FROM confluentinc/cp-kafka-connect-base:6.0.0

# Install connector plugins with the confluent-hub cli
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:0.4.0
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:5.5.1
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:5.5.1
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-s3:5.5.1
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-elasticsearch:10.0.0
RUN confluent-hub install --no-prompt jcustenborder/kafka-connect-spooldir:2.0.46

# Adds the Maven build target to the Kafka Connect plugin path.
COPY target/*.jar /usr/share/java