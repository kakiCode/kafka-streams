#!/bin/bash

app_folder=$(dirname $(readlink -f $0))

jar_file=kafka-streams-processor-1.0-SNAPSHOT-jar-with-dependencies.jar

[[ -z "${KAFKA_CONFIG}" ]] && { echo "KAFKA_CONFIG required"; exit 1; }
[[ -z "${SOURCE_TOPIC}" ]] && { echo "SOURCE_TOPIC required"; exit 1; }
[[ -z "${SINK_TOPIC}" ]] && { echo "SINK_TOPIC required"; exit 1; }
[[ -z "${PROCESSOR_CLASS}" ]] && { echo "PROCESSOR_CLASS required"; exit 1; }
[[ -z "${INTERVAL_IN_MILLIS}" ]] && { echo "INTERVAL_IN_MILLIS required"; exit 1; }

export KAFKA_CONFIG=$KAFKA_CONFIG
echo "Starting kafka-streams with KAFKA_CONFIG: ${KAFKA_CONFIG}"
export SOURCE_TOPIC=$SOURCE_TOPIC
echo "Starting kafka-streams with SOURCE_TOPIC: ${SOURCE_TOPIC}"
export SINK_TOPIC=$SINK_TOPIC
echo "Starting kafka-streams with SINK_TOPIC: ${SINK_TOPIC}"
export PROCESSOR_CLASS=$PROCESSOR_CLASS
echo "Starting kafka-streams with PROCESSOR_CLASS: ${PROCESSOR_CLASS}"
export INTERVAL_IN_MILLIS=$INTERVAL_IN_MILLIS
echo "Starting kafka-streams with INTERVAL_IN_MILLIS: ${INTERVAL_IN_MILLIS}"

debug_switch=""
if [ ! -z "$DEBUG_PORT" ]; then
	debug_switch="-Xms1024m -Xmx4096m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$DEBUG_PORT"
fi

java $debug_switch -jar $app_folder/$jar_file