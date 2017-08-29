FROM java:openjdk-8-jre

ENV APP_DIR /opt/app

RUN apt-get update

RUN mkdir -p $APP_DIR

ADD kafka-streams-processor-1.0-SNAPSHOT-jar-with-dependencies.jar $APP_DIR/kafka-streams-processor-1.0-SNAPSHOT-jar-with-dependencies.jar
ADD start.sh $APP_DIR/start.sh

WORKDIR $APP_DIR

# don't forget to provide KAFKA_CONFIG env var on run
CMD [ "./start.sh" ]
