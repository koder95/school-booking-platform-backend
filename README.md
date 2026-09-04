# School Booking Platform Backend
[![Java CI](https://github.com/koder95/school-booking-platform-backend/actions/workflows/ci.yml/badge.svg)](https://github.com/koder95/school-booking-platform-backend/actions/workflows/ci.yml)
[![Build and Push Docker Image](https://github.com/koder95/school-booking-platform-backend/actions/workflows/cd.yml/badge.svg)](https://github.com/koder95/school-booking-platform-backend/actions/workflows/cd.yml)

This repository contains the backend service for the **School Booking Platform**.
It is a Spring Boot application that provides RESTful APIs for managing students, teachers, lessons and bookings.
It is published as a Docker image on [Docker Hub](https://hub.docker.com/r/koder95/school-booking-platform-backend).

:globe_with_meridians: Standard port: **8080**

> [!CAUTION]
> This project is a work in progress. The API may change and is not yet stable.
> We recommend rebuilding the docker image and checking the README for updates
> before using it in production.

> [!TIP]
> This version is not stable and so you may crush with the database. If you want to reset the database, you can use the following command:
> ```bash
> docker-compose down -v && docker-compose up -d
> ```

## Requirements
Minimum recommended environment to run the project locally:

- Java 21 (or newer) and a compatible JDK installed (or use the bundled `./mvnw` wrapper).
- Maven (if not using `./mvnw`).
- PostgreSQL 12+ (for production/local DB). Connection is configured via Spring properties (see `application.properties`).
- Docker & Docker Compose (recommended for local development and MailHog service).
- Optional: MailHog for capturing emails locally (SMTP on 1025, UI on 8025).

## :shield: Authentication
- The service supports two authentication flows:
  - JWT bearer tokens (classic username/password login)
  - One-time token (OTT / magic link) authentication — generate a one-time token and use it to log in without a password
- Obtain a token by calling the login endpoint (or use the OTT flow). Use the returned token as an `Authorization: Bearer <token>` header for protected endpoints.

### One-time token (OTT) authentication
New endpoints to support passwordless login via one-time tokens (magic links):

- `POST /api/ott/generate` — generate and send a one-time token to an email address (creates a one_time_tokens DB record)
  - Request JSON:
    ```json
    {
      "email": "student@example.com"
    }
    ```
  - Response: 200 on success (token delivery is logged); if email sending fails an `EmailDeliveryException` is thrown and results in a 500 with a delivery log id in the message.

- `POST /api/ott` — authenticate using a one-time token (exchange for JWT)
  - Request JSON:
    ```json
    {
      "token": "<one-time-token>"
    }
    ```
  - Response: 200 with `{ "token": "<jwt-token-string>" }` on success

Notes:
- A Jdbc-backed one time token service and corresponding Liquibase changelog were added to persist tokens.
- One-time tokens are single-use and expire according to configured properties.

## Running locally (development)
There are two common ways to start the application locally:

1) Using Docker Compose (recommended for a quick local environment with PostgreSQL + MailHog):

```bash
# start DB + mailhog and the app (if Dockerfile + compose configured)
docker-compose up -d --build

# stop and remove volumes if you want a clean DB
docker-compose down -v
```

2) Using Maven wrapper (runs against local/Postgres configured in `application.properties`):

```bash
./mvnw spring-boot:run
# or build and run
./mvnw clean package
java -jar target/*.jar
```

Note: The application sets `server.forward-headers-strategy` in `application.properties` to ensure correct handling of forwarded headers when deployed behind a reverse proxy.

## API documentation / Swagger (OpenAPI)
This project includes SpringDoc OpenAPI configuration. When enabled, the API documentation and interactive UI are available at one of the following URLs (depending on SpringDoc version and config):

- http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

How to use the Swagger UI locally:

1. Start the application (see Running locally).
2. Open the Swagger UI URL in a browser.
3. For secured endpoints, click "Authorize" and paste `Bearer <token>` (include the "Bearer " prefix) into the value box.

Example: fetch students (ADMIN role)

```bash
# login to obtain token
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"secret"}'

# get paged students
curl -H "Authorization: Bearer <token>" "http://localhost:8080/api/students?page=0&size=20"
```

## Database migrations (Liquibase)
Liquibase changelogs are located under `src/main/resources/db/changelog`.
If you run the application with a fresh database, Liquibase will apply the changelogs automatically on startup.

## Mail / Email testing
For local development we recommend using MailHog:
- SMTP: `localhost:1025`
- Web UI: `http://localhost:8025`
See `docker-compose` for a MailHog service example.

## Email delivery logging, MailHog (local testing) & Login notifications
The application records each attempted email delivery and persists a delivery log with status and error information.

Key points:

- Model: `EmailDeliveryLog` with fields: `recipient` (Email), `subject`, `body` (TEXT), `status` (PENDING/SENT/FAILED), `createdAt`, `errorMessage`, and soft-delete (`isDeleted`).
- DTO: `SendEmailRequestDto` (validated recipient, subject, body) and `DeliveryStatus` enum.
- Repository: `EmailDeliveryLogRepository` added with helper `findByStatus`.
- Service: `EmailDeliveryService` (interface) and `EmailDeliveryServiceImpl` which:
  - Validates requests
  - Persists an initial delivery log (status PENDING)
  - Sends email via `JavaMailSender` (HTML body support)
  - Updates the log to SENT or FAILED and records stacktrace on failure
  - Ensures an `Email` entity exists for the recipient when persisting the log

Sending login notification:

- On successful login the backend triggers a login notification email for the user.

MailHog support (docker-compose):

- A MailHog service has been added to docker-compose for local development and integration tests. MailHog exposes SMTP on port `1025` and a web UI on port `8025`.

## CORS & Security
CORS configuration was extended to expose the `Authorization` header and allow credentials where appropriate to support frontend usage with cookies/authorization headers in cross-origin scenarios.

## Examples
Fully sandbox environment with MailHog is available:
- [Main entry](https://sbp-backend-development.up.railway.app)
- [Swagger UI](https://sbp-backend-development.up.railway.app/swagger-ui/index.html)
- [OpenAPI JSON](https://sbp-backend-development.up.railway.app/v3/api-docs)
- [MailHog UI](https://sbp-mailhog-development.up.railway.app)

### Login and get token
```bash
curl -X POST "http://localhost:8080/api/auth/login" -H "Content-Type: application/json" -d \
  "{\"email\":\"admin@example.com\",\"password\":\"secret\"}"
```

### Generate OTT (magic link) and use it
```bash
# request generation (server will send email with token link)
curl -X POST "http://localhost:8080/api/auth/ott/generate" -H "Content-Type: application/json" -d '{"email":"student@example.com"}'

# exchange a one-time token for JWT
curl -X POST "http://localhost:8080/api/auth/ott" -H "Content-Type: application/json" -d '{"token":"<one-time-token>"}'
```

### Book a lesson (STUDENT)
```bash
curl -X POST "http://localhost:8080/api/lessons/550e8400-e29b-41d4-a716-446655440000/booking" \
  -H "Authorization: Bearer <token>"
```

### Use token to fetch students (ADMIN only)
```bash
curl -H "Authorization: Bearer <token>" "http://localhost:8080/api/students?page=0&size=10"
```

## Further information
- For full API documentation check the project's **OpenAPI/Swagger UI** (URLs above) or consult the controller and DTO source code in `src/main/java/pl/koder95/sbp/backend`.
