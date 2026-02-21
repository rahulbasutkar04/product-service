# Product Service

A Spring Boot REST API for a **store** that manages **products** and **items**. It supports two roles: **User** (consumer) and **Admin** (product management). Authentication is JWT-based.

---

## Table of Contents

- [Initial Analysis / Project Overview](#initial-analysis--project-overview)
- [How to Use the APIs (Flow)](#how-to-use-the-apis-flow)
- [Authentication](#authentication)
- [API Reference](#api-reference)
- [Swagger / OpenAPI](#swagger--openapi)
- [Docker](#docker)
- [Local Setup](#local-setup)

---

## Initial Analysis / Project Overview

This service covers the following **domains**:

| Domain | Description |
|--------|-------------|
| **User** | End customers who register, log in, view available products, and add items (e.g. to a cart/wishlist). Role: `USER`. |
| **Admin** | Back-office users who register (with a secret), manage product catalog (CRUD), and view items by product. Role: `ADMIN`. |
| **Product** | Catalog entities with name, category, quantity, and availability. Created/updated/deleted by admins; users see only available products. |
| **Item** | User-added entries linking a product and quantity. Users add items and list their own; admins can list items by product. |
| **Auth** | Login returns JWT access and refresh tokens. All non-public APIs require a valid JWT. |

**Tech stack:** Java 17, Spring Boot 3.x, Spring Security (JWT), Spring Data JPA, hibernate, MySQL (dev), Springdoc OpenAPI (Swagger).

---

## How to Use the APIs (Flow)

To use the APIs (except the few public ones), **every user or admin must be registered and then log in** to obtain a JWT.

1. **Register**
   - **As User:** `POST /user/register/opn` with name, email, contact, password.
   - **As Admin:** `POST /admin/register/opn` with the same body **plus** header `X-ADMIN-SECRET: <admin.register.secret>` (value from config).

2. **Login**
   - **Anyone (user or admin):** `POST /auth/login/opn` with `email` and `password`. Response includes `token` (access) and `refreshToken`.

3. **Call secured APIs**
   - Send the **access token** in the **Authorization** header: `Authorization: Bearer <token>` (or in a cookie named `jwt`).
   - Use **user** endpoints only when logged in as `USER`; **admin** endpoints only when logged in as `ADMIN`.

If the access token expires (see [Authentication](#authentication)), use the refresh token (e.g. in your client flow) or log in again to get a new pair.

---

## Authentication

- **Mechanism:** JWT (Bearer token). Token can be sent in:
  - Header: `Authorization: Bearer <your-access-token>`
  - Cookie: `jwt=<your-access-token>`
- **Access token validity:** **15 minutes** (from issue time).
- **Refresh token validity:** **7 days** (from issue time).
- **Config:** Secret key is in `jwt.secret.key` (e.g. in `application-dev.yml`). For production, use env vars or a secrets manager; do not commit secrets.

**Public endpoints (no JWT):**

- `POST /user/register/opn` – user registration  
- `POST /admin/register/opn` – admin registration (requires `X-ADMIN-SECRET`)  
- `POST /auth/login/opn` – login  

All other endpoints require a valid JWT and the correct role (USER or ADMIN) as indicated in the API reference below.

---

## API Reference

Base URL (local dev): `http://localhost:8081`

### Auth

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/auth/login/opn` | Public | Login with email and password. Returns access + refresh tokens. |

**Request body (JSON):**

```json
{
  "email": "user@example.com",
  "password": "min 6 chars"
}
```

**Response:** `email`, `token` (access), `refreshToken`.

---

### User (registration & profile)

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/user/register/opn` | Public | Register a new **user** (role USER). |
| `GET`  | `/user/myProfile/secure` | **USER** | Get current user profile. |

**Register request body (JSON):**

- `name` (required), `email` (required, valid email), `contact` (required, 10-digit Indian mobile), `password` (required, min 6 chars).

**Profile response:** `name`, `email`, `contact`, `role`.

---

### Admin (registration only)

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/admin/register/opn` | Public* | Register a new **admin** (role ADMIN). *Requires header `X-ADMIN-SECRET` matching `admin.register.secret`. |

**Request:** Same body as user registration. Header: `X-ADMIN-SECRET: <secret>`.

**Response:** Same as user registration (includes `role: ADMIN`).

---

### Products (`/api/v1/product`)

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST`   | `/api/v1/product/secure` | **ADMIN** | Create a product. |
| `PUT`    | `/api/v1/product/{id}/secure` | **ADMIN** | Update product by id. |
| `GET`    | `/api/v1/product/{id}/secure` | **ADMIN** | Get product by id (full details). |
| `GET`    | `/api/v1/product/page/secure` | **ADMIN** | List all products with pagination; optional `category` filter. |
| `DELETE` | `/api/v1/product/{id}/secure` | **ADMIN** | Soft-delete (make unavailable) product by id. |
| `GET`    | `/api/v1/product/consumer/secure` | **USER** | List **available** products with pagination; optional `category` filter. |

**Create product body (JSON):**

- `productName` (required), `category` (required: `CLOTH`, `ELECTRONICS`, `TOY`, `HOUSEHOLD`, `DAILY_ESSENTIALS`), `productQuantity` (required, ≥ 0), `availability` (boolean).

**Update product body (JSON):** All optional – `productName`, `productQuantity`, `category`, `availability`.

**Pagination:** Use Spring `Pageable` (e.g. `?page=0&size=10`). Filter: `?category=ELECTRONICS`.

---

### Items (`/api/v1/item`)

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/api/v1/item/secure` | **USER** | Add an item (product + quantity) for the logged-in user. |
| `GET`  | `/api/v1/item/secure` | **USER** | Get all items added by the logged-in user. |
| `GET`  | `/api/v1/item/product/{productId}/secure` | **ADMIN** | Get all items for a given product. |

**Add item body (JSON):**

- `productId` (required), `quantity` (required, ≥ 1).

**Item response fields:** `itemId`, `productId`, `productName`, `quantity`.

---

## Swagger / OpenAPI

Interactive API documentation is available when the application is running:

- **Swagger UI:** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)  
- **OpenAPI JSON:** [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)  

Swagger and API-docs endpoints are **public** (no JWT required to open the UI). You can use the “Authorize” button in Swagger UI to set `Bearer <your-access-token>` and then try secured endpoints.

---

## Docker

You can run the service in Docker so readers can try it without a full local Java/MySQL setup.

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) installed and running.

### Build

From the project root:

```bash
docker build -t product-service:latest .
```

### Run

- **App only (DB must be reachable from container):**

  ```bash
  docker run -p 8081:8081 --name product-service product-service:latest
  ```

- **DB on host (e.g. MySQL on `localhost:3306`):**

  ```bash
  docker run -p 8081:8081 --name product-service --add-host=host.docker.internal:host-gateway -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/zest_india_task product-service:latest
  ```

- **With env overrides (DB URL, user, password, JWT secret):**

  ```bash
  docker run -p 8081:8081 --name product-service \
    -e SPRING_PROFILES_ACTIVE=dev \
    -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/zest_india_task \
    -e SPRING_DATASOURCE_USERNAME=root \
    -e SPRING_DATASOURCE_PASSWORD=yourpassword \
    -e JWT_SECRET_KEY=your-secret-key \
    product-service:latest
  ```

- **Run in background:** add `-d` before `-p`.

After starting, use:

- **Application:** http://localhost:8081  
- **Swagger UI:** http://localhost:8081/swagger-ui.html  

**Useful commands:**

| Action | Command |
|--------|---------|
| Stop | `docker stop product-service` |
| Start again | `docker start product-service` |
| Remove container | `docker rm -f product-service` |
| View logs | `docker logs -f product-service` |

For more detail (e.g. production notes), see [DOCKER.md](DOCKER.md).

---

## Local Setup

For development without Docker:

1. **Prerequisites**
   - **Java 17**
   - **MySQL** (e.g. 8.x) with a database (e.g. `zest_india_task`)
   - **Gradle** (or use the wrapper: `./gradlew` / `gradlew.bat`)

2. **Configuration**
   - Copy or edit `src/main/resources/application-dev.yml` and set:
     - `spring.datasource.url`, `username`, `password` for your MySQL instance.
     - `jwt.secret.key` (secure random string for production).
     - `admin.register.secret` (used for `X-ADMIN-SECRET` when registering admins).

3. **Run**
   - With Gradle: `./gradlew bootRun --args='--spring.profiles.active=dev'`  
   - Or run the main class `ProductServiceApplication` with profile `dev`.

4. **Port**
   - Default dev port: **8081**.

5. **Verify**
   - Open http://localhost:8081/swagger-ui.html and try:
     - Register user → Login → Use “Authorize” with the returned token → Call user/product/item APIs as per the table above.

---

## Summary

- **Flow:** Register (user or admin) → Login → Use APIs with the JWT.
- **Access:** Each API is either Public, **USER** only, or **ADMIN** only, as in the tables.
- **Auth:** Access token 15 minutes; refresh token 7 days; send token as `Authorization: Bearer <token>` or cookie `jwt`.
- **Docs:** Swagger at `/swagger-ui.html`; OpenAPI at `/v3/api-docs`.
- **Run:** Via Docker (see [Docker](#docker)) or local Java 17 + MySQL (see [Local Setup](#local-setup)).
