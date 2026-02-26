#!/bin/bash

CONTAINER_NAME="clip-postgres"

# Check if container exists
if [ "$(docker ps -aq -f name=^${CONTAINER_NAME}$)" ]; then
    echo "Container ${CONTAINER_NAME} exists. Starting it..."
    docker start ${CONTAINER_NAME}
else
    echo "Container ${CONTAINER_NAME} does not exist. Creating and starting it..."
    docker run --name ${CONTAINER_NAME} \
      -e POSTGRES_USER=clip \
      -e POSTGRES_PASSWORD=clip \
      -e POSTGRES_DB=clipdb \
      -p 5432:5432 \
      -d postgres:16
fi
