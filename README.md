# Product Service

A REST API for a store: manage **products** (catalog) and **items** (e.g. cart/wishlist). Two roles: **User** (shopper) and **Admin** (manage catalog). Secured with JWT.

**Tech:** Java 17 · Spring Boot 3 · Spring Security (JWT) · MySQL · Swagger/OpenAPI

---

## Quick start (try it in 2 minutes)

If you have [Docker](https://docs.docker.com/get-docker/) installed:

```bash
docker-compose up -d
```

Then open:

- **API (Swagger UI):** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **Base URL:** http://localhost:8081

To stop: `docker-compose down`

---

## Table of contents

| Section | What you'll find |
|--------|-------------------|
| [What is this?](#what-is-this) | Overview and main concepts |
| [How to use the API](#how-to-use-the-api) | Register → Login → Call APIs (simple flow) |
| [Authentication](#authentication) | How JWT works and which endpoints are public |
| [Running the service](#running-the-service) | Docker Compose, Docker only, or local dev |
| [API reference](#api-reference) | All endpoints with method, path, and access |
| [Swagger](#swagger) | Interactive docs and OpenAPI link |

---

## What is this?

This service powers a **store** with:

| Concept | Who uses it | What it does |
|--------|--------------|--------------|
| **User** | Shoppers | Register, login, see products, add items (e.g. to cart) |
| **Admin** | Store staff | Register (with a secret), manage product catalog, view items by product |
| **Product** | Catalog | Name, category, quantity, availability. Admins create/edit; users see only available ones |
| **Item** | User’s list | Links a product + quantity. Users add/list their own; admins can list by product |

Everything except registration and login requires a **JWT** (token) in the request.

---

## How to use the API

In short: **register → login → send the token with every request.**

### Step 1: Register

- **As a user (shopper):**  
  `POST /user/register/opn` with `name`, `email`, `contact`, `password`.
- **As an admin:**  
  Same body, but call `POST /admin/register/opn` and add header  
  `X-ADMIN-SECRET: <value from config>`.

### Step 2: Login

- **Anyone:**  
  `POST /auth/login/opn` with `email` and `password`.  
  Response gives you `token` (access) and `refreshToken`.

### Step 3: Call other APIs

- Send the **access token** in every request:
  - **Header:** `Authorization: Bearer <token>`
  - Or **cookie:** `jwt=<token>`
- Use **user** endpoints when logged in as User; **admin** endpoints when logged in as Admin.

When the access token expires, use the refresh token (in your app flow) or log in again.

---

## Authentication

| Item | Detail |
|------|--------|
| **Type** | JWT (Bearer token) |
| **Where to send** | Header `Authorization: Bearer <token>` or cookie `jwt=<token>` |
| **Access token** | Valid for **15 minutes** |
| **Refresh token** | Valid for **7 days** |

**No token needed (public):**

- `POST /user/register/opn` — user registration  
- `POST /admin/register/opn` — admin registration (needs `X-ADMIN-SECRET`)  
- `POST /auth/login/opn` — login  

All other endpoints need a valid JWT and the right role (USER or ADMIN).

---

## Running the service

### Option 1: Docker Compose (easiest)

Runs the app **and** MySQL. No need to install Java or MySQL.

**Prerequisite:** [Docker](https://docs.docker.com/get-docker/) installed.

| What you want | Command |
|---------------|--------|
| Start app + DB | `docker-compose up -d` |
| Stop everything | `docker-compose down` |
| Rebuild after code changes | `docker-compose up -d --build` |
| View logs | `docker-compose logs -f` or `docker-compose logs -f app` |
| See running containers | `docker-compose ps` |

Then use: **http://localhost:8081** and **http://localhost:8081/swagger-ui.html**.

---

### Option 2: Docker (app only)

Use this if MySQL is already running (e.g. on your machine or another container).

**Build:**

```bash
docker build -t product-service:latest .
```

**Run** (examples):

- App only (DB must be reachable from container):
  ```bash
  docker run -p 8081:8081 --name product-service product-service:latest
  ```
- DB on your host (e.g. MySQL on `localhost:3306`):
  ```bash
  docker run -p 8081:8081 --name product-service --add-host=host.docker.internal:host-gateway -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/zest_india_task product-service:latest
  ```
- With custom DB and JWT (env overrides):
  ```bash
  docker run -p 8081:8081 --name product-service \
    -e SPRING_PROFILES_ACTIVE=dev \
    -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/zest_india_task \
    -e SPRING_DATASOURCE_USERNAME=root \
    -e SPRING_DATASOURCE_PASSWORD=yourpassword \
    -e JWT_SECRET_KEY=your-secret-key \
    product-service:latest
  ```

**Useful commands:**  
`docker stop product-service` · `docker start product-service` · `docker logs -f product-service` · `docker rm -f product-service`

More details: [DOCKER.md](DOCKER.md).

---

### Option 3: Local (no Docker)

**Prerequisites:** Java 17, MySQL (e.g. 8.x), Gradle (or `./gradlew` / `gradlew.bat`).

1. **Configure**  
   Edit `src/main/resources/application-dev.yml`: set DB URL, username, password, `jwt.secret.key`, and `admin.register.secret`.

2. **Run**  
   `./gradlew bootRun --args='--spring.profiles.active=dev'`  
   Or run `ProductServiceApplication` with profile `dev`.

3. **Port**  
   App runs on **8081**.

4. **Check**  
   Open http://localhost:8081/swagger-ui.html → register → login → use “Authorize” with the token → try the APIs.

---

## API reference

**Base URL (local):** `http://localhost:8081`

### Auth

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/auth/login/opn` | Public | Login; returns `token` and `refreshToken`. |

**Body:** `{ "email": "...", "password": "..." }`  
**Response:** `email`, `token`, `refreshToken`.

---

### User (registration & profile)

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/user/register/opn` | Public | Register a user (role USER). |
| `GET`  | `/user/myProfile/secure` | USER | Get current user profile. |

**Register body:** `name`, `email`, `contact` (10-digit mobile), `password` (min 6 chars).  
**Profile response:** `name`, `email`, `contact`, `role`.

---

### Admin (registration)

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/admin/register/opn` | Public* | Register an admin (role ADMIN). *Header: `X-ADMIN-SECRET`. |

Same body as user registration; response includes `role: ADMIN`.

---

### Products (`/api/v1/product`)

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST`   | `/api/v1/product/secure` | ADMIN | Create product. |
| `PUT`    | `/api/v1/product/{id}/secure` | ADMIN | Update product. |
| `GET`    | `/api/v1/product/{id}/secure` | ADMIN | Get product by id. |
| `GET`    | `/api/v1/product/page/secure` | ADMIN | List all (paginated; optional `?category=...`). |
| `DELETE` | `/api/v1/product/{id}/secure` | ADMIN | Soft-delete (set unavailable). |
| `GET`    | `/api/v1/product/consumer/secure` | USER | List **available** products (paginated; optional `?category=...`). |

**Create body:** `productName`, `category` (CLOTH, ELECTRONICS, TOY, HOUSEHOLD, DAILY_ESSENTIALS), `productQuantity`, `availability`.  
**Pagination:** `?page=0&size=10`.

---

### Items (`/api/v1/item`)

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/api/v1/item/secure` | USER | Add item (product + quantity). |
| `GET`  | `/api/v1/item/secure` | USER | List current user’s items. |
| `GET`  | `/api/v1/item/product/{productId}/secure` | ADMIN | List items for a product. |

**Add body:** `productId`, `quantity` (≥ 1).  
**Response fields:** `itemId`, `productId`, `productName`, `quantity`.

---

## Swagger

When the app is running:

- **Swagger UI (try the API in the browser):** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

Swagger is public (no login to open the page). Use **Authorize** to paste your `Bearer <token>` and call secured endpoints.

---

## Summary

| Topic | Summary |
|--------|---------|
| **Flow** | Register (user or admin) → Login → Call APIs with JWT. |
| **Token** | Access token 15 min; refresh 7 days. Send as `Authorization: Bearer <token>` or cookie `jwt`. |
| **Roles** | Endpoints are Public, **USER** only, or **ADMIN** only (see tables above). |
| **Run** | **Recommended:** `docker-compose up -d`. Or [Docker only](#option-2-docker-app-only) or [local](#option-3-local-no-docker). |
| **Docs** | Swagger at `/swagger-ui.html`; OpenAPI at `/v3/api-docs`. |
