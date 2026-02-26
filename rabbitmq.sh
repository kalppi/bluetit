#!/bin/bash

CONTAINER_NAME="clip-rabbitmq"

# Check if container exists
if [ "$(docker ps -aq -f name=^${CONTAINER_NAME}$)" ]; then
    echo "Container ${CONTAINER_NAME} exists. Starting it..."
    docker start ${CONTAINER_NAME}
else
    echo "Container ${CONTAINER_NAME} does not exist. Creating and starting it..."
    docker run --name ${CONTAINER_NAME} \
      -e RABBITMQ_DEFAULT_USER=clip \
      -e RABBITMQ_DEFAULT_PASS=clip \
      -p 5672:5672 \
      -p 15672:15672 \
      -d rabbitmq:3-management
fi

