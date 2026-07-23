# School Booking Platform Backend
[![Java CI](https://github.com/koder95/school-booking-platform-backend/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/koder95/school-booking-platform-backend/actions/workflows/ci.yml)
[![Build and Push Docker Image](https://github.com/koder95/school-booking-platform-backend/actions/workflows/cd.yml/badge.svg?branch=master)](https://github.com/koder95/school-booking-platform-backend/actions/workflows/cd.yml)

This repository contains the backend service for the **School Booking Platform**.

This README describes the available REST endpoints and the JSON request/response
schemas used by the API.

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

- Java 17 (or newer) and a compatible JDK installed (or use the bundled `./mvnw` wrapper).
- Maven (if not using `./mvnw`).
- PostgreSQL 12+ (for production/local DB). Connection is configured via Spring properties (see `application.properties`).
- Docker & Docker Compose (recommended for local development and MailHog service).
- Optional: MailHog for capturing emails locally (SMTP on 1025, UI on 8025).

## :shield: Authentication
- The service supports two authentication flows:
  - JWT bearer tokens (classic username/password login)
  - One-time token (OTT / magic link) authentication — generate a one-time token and use it to log in without a password
- Obtain a token by calling the login endpoint (or use the OTT flow). Use the returned token as an `Authorization: Bearer <token>` header for protected endpoints.

Recent security & auth changes introduced in this branch/PR:
- The `User` model primary key has changed from a numeric Long to UUID (auto-generated). Many user-related endpoints now use UUIDs for identification.
- `Student` is now a subclass of `User` (single-table inheritance). The `Student` no longer has a separate numeric ID — use the `uuid` field from `User`.
- One-time token (OTT) endpoints added: `/api/ott` and `/api/ott/generate` (see Authentication section below).
- Some previous "magic link" implementations were removed and replaced by the current OTT service.

## :door: Endpoints
(Existing endpoints retained — see below for full list)

### New: Students module (updated)
This release refactors Student into the shared `User` model and updates the API to use UUIDs for user identifiers.

Key points:

- `Student` now extends `User` using single-table inheritance; both are stored in the `users` table with a `DTYPE` discriminator.
- Primary key for `User` (and therefore for `Student`) is UUID.
- `zoneId` moved from Student to the User base class.
- `StudentRepository` and `UserRepository` use UUID as the id type.
- DTOs and mappers updated accordingly.
- Validation in `CreateStudentRequestDto` enforces email and zoneId constraints.

Student endpoints (examples):

- `GET /api/students` — paged list of students (ADMIN only)
  - Query params: `page` (0-based), `size`, `sort`
  - Returns: `Page<StudentDto>`

- `POST /api/students` — create student (ADMIN only)
  - Request JSON example:
    ```json
    {
      "email": "student@example.com",
      "firstName": "Alice",
      "lastName": "Johnson",
      "zoneId": "Europe/Warsaw"
    }
    ```
  - Response: `StudentDto` with created entity info (contains `uuid`)

- `GET /api/students/{uuid}` — get single student by UUID (ADMIN only)

Errors: standard HTTP codes (400 on validation, 401/403 for auth, 404 when not found).

---

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

### Email delivery & errors
The application records each attempted email delivery and persists an EmailDeliveryLog with status (PENDING/SENT/FAILED) and error details on failure.

Key points:
- An `EmailDeliveryException` is thrown when sending fails. The exception message includes the delivery log id so clients can correlate failures with stored logs.
- `EmailDeliveryServiceImpl` records stacktraces in the delivery log and sets the log status to FAILED on exceptions.
- A global exception handler maps `EmailDeliveryException` to HTTP 500 and returns the exception message to the client.

---

### New: Booking feature (students)
This PR introduces booking functionality so authenticated students can book lessons (enroll) based on available lesson slots.

Key points:

- JPA `Booking` entity and DB migration added to create the `bookings` table.
  - Unique constraint enforces one booking per student per lesson (student-lesson combination unique).
  - Foreign keys reference `users` (student UUID) and `lessons` tables.
- `BookingDto`, `BookingMapper`, `BookingRepository`, and `BookingService` implemented.
- `BookingService` performs authorization checks and throws a domain exception (mapped to proper HTTP responses) on illegal booking attempts.
- Bookings may be created by authenticated students; admins can also manage bookings.

Booking endpoints (examples):

- `GET /api/bookings` — list bookings (ADMIN only by default; students may have endpoints to list their own bookings)
  - Query params: `page`, `size`, `sort`

- `POST /api/bookings` — create a booking (authenticated STUDENT or ADMIN)
  - Request JSON example:
    ```json
    {
      "lessonId": 123,
      "studentUuid": "550e8400-e29b-41d4-a716-446655440000"
    }
    ```
  - Response: `BookingDto` with booking details
  - Errors:
    - 400 Bad Request — validation errors
    - 401 Unauthorized — when token is missing or invalid
    - 403 Forbidden — when user not allowed to book for another student
    - 409 Conflict / 400 — when booking already exists (unique constraint) or slot consumed

- `DELETE /api/bookings/{id}` — cancel a booking (student cancels their own booking or ADMIN)

Notes:
- Booking creation is atomic with respect to lesson slot consumption — the implementation ensures a slot is consumed when booking is created.
- The database migration includes the unique constraint on (student_uuid, lesson_id).

---

### 1) :unlock: `POST /api/auth/login`
Description: Authenticate a user with email/password and obtain a JWT access token.

Request JSON:
```json
{
  "email": "user@example.com",
  "password": "yourPassword"
}
```
Validation:
- `email`: required, must be a valid email format
- `password`: required, non-empty

Response JSON (200):
```json
{
  "token": "<jwt-token-string>"
}
```
Errors:
- **400 Bad Request** — on validation errors for email/password
- **401 Unauthorized** — when credentials are invalid

(For OTT flow use `/api/ott` and `/api/ott/generate` as described above.)

### 2) :lock: `GET /api/emails`
Description: Retrieve a paginated list of stored emails. This endpoint is protected and requires a valid bearer token with `ADMIN` role.

Authentication: set header

Authorization: `Bearer <token>`

**Query parameters (optional):**
- `page` (int) — page index, 0-based (default depends on Spring but commonly 0)
- `size` (int) — page size (number of items per page)
- `sort` (String) — sort specification, e.g. `sort=value,asc` or `sort=id,desc`

Response JSON (`Page<EmailDto>`):

The endpoint returns Spring's Page object serialized to JSON. Important fields:
```json
{
  "content": [ /* list of EmailDto */ ],
  "pageable": { /* pageable metadata */ },
  "totalElements": 42,
  "totalPages": 5,
  "last": false,
  "size": 10,
  "number": 0
}
```

Errors:
- **401 Unauthorized** — when token is missing or invalid
- **403 Forbidden** — when authenticated user does not have ADMIN role

### 3) :mortar_board: Teacher management
The project now exposes REST endpoints to manage Teacher entities. Teachers
have a UUID primary key and are associated one-to-one with an existing `Email`
entity (referenced by `emailId` in the DB). Teachers support soft-delete.

Teachers are now linked to Subjects (many-to-one). The database schema for the teachers table was updated to include `subject_id` and relevant mappings and DTOs were adjusted.

All teacher endpoints require a valid bearer token. Role requirements are
noted per endpoint.

(Teachers section remains as documented in the previous README — endpoints use UUID identifiers.)

### 4) :books: Subject management
(Section unchanged — subjects remain numeric IDs)

### 5) :bookmark_tabs: Lessons management
(Lessons management unchanged from previous release notes — lessons are still used by the booking feature.)

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

## Tests
Run unit and integration tests with Maven:

```bash
./mvnw test
```

Some integration tests rely on a running PostgreSQL instance or Testcontainers; check test profiles and `src/test/resources/application-test.properties` for settings.

## API documentation / Swagger (OpenAPI)
This project includes SpringDoc OpenAPI configuration. When enabled, the API documentation and interactive UI are available at one of the following URLs (depending on SpringDoc version and config):

- http://localhost:8080/swagger-ui.html
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

## Notes & operational changes in this release
- OpenAPI (SpringDoc) configuration was added/updated to expose API docs and Swagger UI.
- Spring Data web support was enabled in the main application to allow automatic binding of `Pageable` parameters for paged endpoints.
- Global exception handling was extended to catch and correctly handle `SQLException` (returns appropriate 500/4xx responses with logged details) and domain exceptions for bookings and email delivery.
- `server.forward-headers-strategy` is set in `application.properties` to support proxy headers when deployed behind a reverse proxy.

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

## Examples (curl)

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
