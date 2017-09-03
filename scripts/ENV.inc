NAME=streams
IMAGE=$NAME
IMAGE_VERSION=latest
CONTAINER=$NAME
HOST=$NAME
JAR=kafka-streams-processor-1.0-SNAPSHOT-jar-with-dependencies.jar

KAFKA_CONTAINER=kafka
KAFKA_HOST=kafka
KAFKA_PORT=9092

export SOURCE_TOPIC=raw-tweets
export SINK_TOPIC=raw-tweets-s
export PROCESSOR_CLASS=org.aprestos.labs.data.kafka.streams.processor.topologies.TwitterProcessor
export INTERVAL_IN_MILLIS=10000

export KAFKA_CONFIG=$KAFKA_HOST:$KAFKA_PORT
export DEBUG_PORT=6068

BLUEMIX_CONTAINER_MEMORY=128
REGISTRY=registry.ng.bluemix.net/mynodeappbue
BLUEMIX_IMG=$REGISTRY/$IMAGE
DOCKER_HUB_IMG=kakicode/$NAME
