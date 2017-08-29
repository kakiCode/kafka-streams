#!/bin/sh

scripts_folder=$(dirname $(readlink -f $0))
base_folder=$(dirname $scripts_folder)

. $scripts_folder/ENV.inc

debug_config=""
if [ ! -z "$DEBUG_PORT" ]; then
	debug_config="--env DEBUG_PORT=$DEBUG_PORT -p $DEBUG_PORT:$DEBUG_PORT"
fi

docker stop $CONTAINER
docker rm $CONTAINER

docker run -d -h $HOST --name $CONTAINER --link $ZK_CONTAINER:$ZK_HOST  \
	--link $KAFKA_CONTAINER:$KAFKA_HOST $debug_config \
	--env KAFKA_CONFIG=$KAFKA_CONFIG $DOCKER_HUB_IMG:$IMAGE_VERSION
#docker logs -f $CONTAINER