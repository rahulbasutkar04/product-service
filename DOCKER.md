# Docker – Build and Run

## Prerequisites
- [Docker](https://docs.docker.com/get-docker/) installed and running.

---

## 0. Quick start with Docker Compose (app + MySQL)

From the project root:

```bash
docker-compose up -d
```

This starts MySQL and the product-service app. The app waits for MySQL to be healthy before starting.

- **Application:** http://localhost:8081  
- **Swagger UI:** http://localhost:8081/swagger-ui.html  

**Docker Compose commands:**

| Action | Command |
|--------|---------|
| Start (detached) | `docker-compose up -d` |
| Stop and remove | `docker-compose down` |
| Rebuild and start | `docker-compose up -d --build` |
| View logs | `docker-compose logs -f` or `docker-compose logs -f app` |
| List services | `docker-compose ps` |

---

## 1. Build the image (manual run)

From the project root (where `Dockerfile` and `build.gradle` are):

```bash
docker build -t product-service:latest .
```

- `-t product-service:latest` tags the image as `product-service` with tag `latest`.
- Use a different name/tag if you prefer, e.g. `docker build -t product-service:1.0 .`

---

## 2. Run the container

**Minimal run (app only):**

```bash
docker run -p 8081:8081 --name product-service product-service:latest
```

- App will use the **dev** profile (port **8081**, MySQL URL from `application-dev.yml`).
- If MySQL is not reachable from inside the container (e.g. `localhost` in the URL), the app will fail to connect. Use one of the options below.

**Run with host network (e.g. DB on host):**

```bash
docker run -p 8081:8081 --name product-service --add-host=host.docker.internal:host-gateway -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/zest_india_task product-service:latest
```

**Run with env overrides (DB URL, JWT, etc.):**

```bash
docker run -p 8081:8081 --name product-service \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/zest_india_task \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=rahul \
  -e JWT_SECRET_KEY=your-secret-key \
  product-service:latest
```

**Run in background (detached):**

```bash
docker run -d -p 8081:8081 --name product-service product-service:latest
```

---

## 3. Useful commands (manual single-container run)

| Action        | Command |
|---------------|--------|
| Stop          | `docker stop product-service` |
| Start again   | `docker start product-service` |
| Remove container | `docker rm -f product-service` |
| View logs     | `docker logs -f product-service` |
| Shell into container | `docker exec -it product-service sh` |

For Docker Compose commands, see [Quick start with Docker Compose](#0-quick-start-with-docker-compose-app--mysql) above.

---

## 4. Access the app

- **Application:** http://localhost:8081  
- **Swagger UI:** http://localhost:8081/swagger-ui.html  
- **API docs:** http://localhost:8081/v3/api-docs  

---

## 5. Notes

- The image uses **Java 17** and runs the app as a **non-root** user.
- Default profile is **dev** (port **8081**). Override with `-e SPRING_PROFILES_ACTIVE=prod` (and a `application-prod.yml`) if needed.
- For production, pass secrets (DB password, JWT key) via env or a secrets manager, not hardcoded in the image.
