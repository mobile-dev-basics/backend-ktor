To run docker image use:
```bash
docker run -d --name redis-stack-server -p 6379:6379 redis/redis-stack-server:latest
```

To connect with redis cli:
```bash
docker exec -it redis-stack-server redis-cli
```