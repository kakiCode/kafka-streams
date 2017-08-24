# kafka-streams
## kafka-streams docker image

- hostname: streams
- requires an environment variable named KAFKA_CONFIG with kafka-host:kafka-port, normally KAFKA_CONFIG=kafka:9092
- expects to resolve 
  - zookeeper on "zookeeper"
  - kafka on "kafka"
- provides java debug port if DEBUG_PORT variable is set

### usage:

- edit ENV.inc accordingly:
  ```
  NAME=streams
  IMAGE=$NAME
  IMAGE_VERSION=latest
  CONTAINER=$NAME
  HOST=$NAME
  JAR=kafka-streams-processor-1.0-SNAPSHOT-jar-with-dependencies.jar

  KAFKA_CONTAINER=kafka
  KAFKA_HOST=kafka
  KAFKA_PORT=9092

  ZK_CONTAINER=zookeeper
  ZK_HOST=zookeeper

  export KAFKA_CONFIG=$KAFKA_HOST:$KAFKA_PORT
  export DEBUG_PORT=6068

  BLUEMIX_CONTAINER_MEMORY=128
  REGISTRY=registry.ng.bluemix.net/mynodeappbue
  BLUEMIX_IMG=$REGISTRY/$IMAGE
  DOCKER_HUB_IMG=kakicode/$NAME
  ```
- scripts/buildAndPushImage.sh - build docker image and push it to dockerHub (/kakicode/kafka) and private bluemix registry (registry.ng.bluemix.net/mynodeappbue/kafka)
- scripts/runLocalContainer.sh - run on local docker engine
- scripts/run.sh - run jar on local jvm
- scripts/debug.sh - debug jar on local jvm
