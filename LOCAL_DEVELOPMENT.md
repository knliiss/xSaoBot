Local development with Docker Compose

1) Copy example env:

   cp .env.example .env
   # Edit .env and set BOT_TOKEN and any other secrets

2) Build and run services:

   docker compose build
   docker compose up

   This will start Postgres, MongoDB and the app (the app image is built from your Dockerfile).

3) Iterate during development:
   - If you want hot-reload, change `docker-compose.yml` to mount the source and run `./gradlew bootRun` in `app` service (commented in file).
   - Example: uncomment volumes and command in `docker-compose.yml` for `app`.

4) Database access:
   - Postgres: localhost:5432 (user: postgres, password: postgres)
   - Mongo: localhost:27017 (user: mongo, password: mongo)

One-command redeploy (build jar + recreate app container)

If you want a single command that builds the application and restarts only the `app` service (leaving DBs running), use the helper script `restart-app.sh`:

```bash
./restart-app.sh
```

What the script does:
- runs `./gradlew bootJar -x test` to build the fat jar
- runs `docker compose up --build --force-recreate --no-deps -d app` to recreate only the `app` service (doesn't touch DB services)

To follow logs after restart:

```bash
docker compose logs -f app
```

Notes
- Use `docker compose down -v` to tear down and remove volumes (clears DBs).
- Do NOT commit your real `.env` to VCS — keep secrets in `.env` or use local secret manager.
- For running tests and debugging, prefer `./gradlew bootRun` locally with IDE and remote DBs.
