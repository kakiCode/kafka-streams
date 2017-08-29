#!/bin/sh

echo "going to build image "

scripts_folder=$(dirname $(readlink -f $0))
base_folder=$(dirname $scripts_folder)
docker_folder=$base_folder/docker
target_folder=$base_folder/target

. $scripts_folder/ENV.inc

_pwd=`pwd`
cd $base_folder

mvn clean package
built=$?
while [ "$built" -ne "0" ]
do
        echo "build was broken !!!  going to exit !!!"
        cd $_pwd
        exit 1
done

cp $target_folder/$JAR $docker_folder/

echo "going to build image $IMAGE and push it to docker hub and bluemix repository..."

cd $docker_folder

docker rmi $IMAGE:$IMAGE_VERSION
docker build -t $IMAGE:$IMAGE_VERSION $docker_folder
docker tag $IMAGE $DOCKER_HUB_IMG
docker tag $IMAGE $BLUEMIX_IMG
docker push $DOCKER_HUB_IMG
docker push $BLUEMIX_IMG

cd $_pwd

echo "... done."