#!/bin/bash

app_folder=$(dirname $(readlink -f $0))

jar_file=kafka-streams-processor-1.0-SNAPSHOT-jar-with-dependencies.jar

[[ -z "${KAFKA_CONFIG}" ]] && { echo "KAFKA_CONFIG required"; exit 1; }

export KAFKA_CONFIG=$KAFKA_CONFIG
echo "Starting kafka-streams with KAFKA_CONFIG: ${KAFKA_CONFIG}"

debug_switch=""
if [ ! -z "$DEBUG_PORT" ]; then
	debug_switch="-Xms1024m -Xmx4096m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$DEBUG_PORT"
fi

java $debug_switch -jar $app_folder/$jar_file