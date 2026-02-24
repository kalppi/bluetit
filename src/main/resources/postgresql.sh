docker run --name clip-postgres \
  -e POSTGRES_USER=clip \
  -e POSTGRES_PASSWORD=clip \
  -e POSTGRES_DB=clipdb \
  -p 5432:5432 \
  -d postgres:16