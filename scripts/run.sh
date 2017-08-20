#!/bin/sh

scripts_folder=$(dirname $(readlink -f $0))
base_folder=$(dirname $scripts_folder)
target_folder=$base_folder/target
jar_file=$target_folder/kafka-streams-processor-1.0-SNAPSHOT-jar-with-dependencies.jar


. $scripts_folder/ENV.inc

java -Xms1024m -Xmx4096m -jar $jar_file &
