docker run --name clip-rabbitmq \
  -e RABBITMQ_DEFAULT_USER=clip \
  -e RABBITMQ_DEFAULT_PASS=clip \
  -p 5672:5672 \
  -p 15672:15672 \
  -d rabbitmq:3-management

