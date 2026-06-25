# School Booking Platform Backend

This repository contains the backend service for the **School Booking Platform**.

This README describes the available REST endpoints and the JSON request/response
schemas used by the API.

:globe_with_meridians: Standard port: **8080**

## :shield: Authentication

- The service uses **JWT bearer tokens**.
- Obtain a token by calling the login endpoint. Use the returned token as an
  `Authorization: Bearer <token>` header for protected endpoints.

## :door: Endpoints

### 1) :unlock: `POST /api/auth/login`

Description: Authenticate a user and obtain an access token.

Request JSON:
```json
{
  "email": "user@example.com",
  "password": "yourPassword"
}
```
Validation:
- email: required, must be a valid email format
- password: required, non-empty

Response JSON (200):
```json
{
  "token": "<jwt-token-string>"
}
```
Errors:
- **400 Bad Request** — on validation errors for email/password
- **401 Unauthorized** — when credentials are invalid

### 2) :lock: `GET /api/emails`

Description: Retrieve a paginated list of stored emails. This endpoint is
protected and requires a valid bearer token with `ADMIN` role.

Authentication: set header

Authorization: Bearer <token>

**Query parameters (optional):**
- `page` (int) — page index, 0-based (default depends on Spring but commonly 0)
- `size` (int) — page size (number of items per page)
- `sort` (String) — sort specification, e.g. `sort=value,asc` or `sort=id,desc`

Response JSON (`Page<EmailDto>`):

The endpoint returns Spring's Page object serialized to JSON. Important fields:
```json
{
  "content": [
    {
      "id": 1,
      "value": "alice@example.com"
    },
    {
      "id": 2,
      "value": "bob@example.com"
    }
  ],
  "pageable": { /* pageable metadata, see Spring Data Page */ },
  "totalElements": 42,
  "totalPages": 5,
  "last": false,
  "size": 10,
  "number": 0,
  "sort": { /* sort metadata */ },
  "first": true,
  "numberOfElements": 10,
  "empty": false
}
```
Schema for an email item (`EmailDto`):
```json
{
  "id": 123,        // Long
  "value": "string" // email address
}
```
Errors:
- **401 Unauthorized** — when token is missing or invalid
- **403 Forbidden** — when authenticated user does not have ADMIN role

### 3) :mortar_board: Teacher management

The project now exposes REST endpoints to manage Teacher entities. Teachers
have a UUID primary key and are associated one-to-one with an existing `Email`
entity (referenced by `emailId` in the DB). Teachers support soft-delete.

All teacher endpoints require a valid bearer token. Role requirements are
noted per endpoint.

#### `GET /api/teachers`

Description: Retrieve a paginated list of teachers.

Authorization: Bearer <token>

Roles: ADMIN only

Query parameters (optional):
- `page` (int) — page index, 0-based
- `size` (int) — page size
- `sort` (String) — sort specification

Response JSON (`Page<TeacherDto>`):
```json
{
  "content": [
    {
      "uuid": "550e8400-e29b-41d4-a716-446655440000",
      "emailId": 123,
      "firstName": "Alice",
      "lastName": "Smith"
    }
  ],
  "pageable": { /* pageable metadata */ },
  "totalElements": 10,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

Schema for `TeacherDto`:
```json
{
  "uuid": "uuid-string",    // UUID
  "emailId": 123,            // Long (ID of Email entity)
  "firstName": "string",
  "lastName": "string"
}
```

#### `GET /api/teachers/{uuid}`

Description: Get a single teacher by UUID.

Authorization: Bearer <token>

Roles: ADMIN, STUDENT

Response JSON (`TeacherDto`):
```json
{
  "uuid": "550e8400-e29b-41d4-a716-446655440000",
  "emailId": 123,
  "firstName": "Alice",
  "lastName": "Smith"
}
```
Errors:
- **404 Not Found** — when teacher with given UUID does not exist
- **401 / 403** — as with other protected endpoints

#### `POST /api/teachers`

Description: Create a new teacher.

Authorization: Bearer <token>

Roles: ADMIN only

Request JSON (`CreateTeacherRequestDto`):
```json
{
  "email": "teacher@example.com",  // required, not blank
  "firstName": "John",
  "lastName": "Doe"
}
```
Notes:
- If the provided email value does not exist in the `emails` table, the
  application will create an `Email` record and associate it with the new
  Teacher.

Response JSON (`TeacherDto`):
```json
{
  "uuid": "550e8400-e29b-41d4-a716-446655440000",
  "emailId": 456,
  "firstName": "John",
  "lastName": "Doe"
}
```
Errors:
- **400 Bad Request** — on validation errors (e.g. missing/blank email)
- **401 / 403** — as above

#### `PUT /api/teachers/{uuid}`

Description: Update an existing teacher.

Authorization: Bearer <token>

Roles: ADMIN only

Request JSON (`UpdateTeacherRequestDto`) — fields are optional and will be
applied if present:
```json
{
  "email": "new-email@example.com",
  "firstName": "NewFirst",
  "lastName": "NewLast"
}
```
Notes:
- If `email` is provided and does not exist in the `emails` table, a new
  `Email` record will be created and associated with the teacher.

Response JSON (`TeacherDto`): updated teacher representation

#### `DELETE /api/teachers/{uuid}`

Description: Delete (soft-delete) a teacher by UUID.

Authorization: Bearer <token>

Roles: ADMIN only

Response JSON (`TeacherDto`): representation of the deleted teacher

Errors common to teacher endpoints:
- **401 Unauthorized** — when token is missing or invalid
- **403 Forbidden** — when authenticated user does not have required role
- **404 Not Found** — when resource (teacher) does not exist

### 4) :books: Subject management

The project exposes REST endpoints to manage Subject entities ("przedmioty").
Subjects are identified by a numeric ID and typically contain a name and an
optional description.

All subject endpoints require a valid bearer token. Role requirements are
noted per endpoint.

#### `GET /api/subjects`

Description: Retrieve a paginated list of subjects.

Authorization: Bearer <token>

Roles: ADMIN only

Query parameters (optional):
- `page` (int) — page index, 0-based
- `size` (int) — page size
- `sort` (String) — sort specification

Response JSON (`Page<SubjectDto>`):
```json
{
  "content": [
    {
      "id": 1,
      "name": "Mathematics",
      "description": "Basic mathematics"
    }
  ],
  "pageable": { /* pageable metadata */ },
  "totalElements": 10,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

Schema for `SubjectDto`:
```json
{
  "id": 1,           // Long
  "name": "string",
  "description": "string"
}
```

#### `GET /api/subjects/{id}`

Description: Get a single subject by numeric ID.

Authorization: Bearer <token>

Roles: ADMIN, STUDENT

Response JSON (`SubjectDto`):
```json
{
  "id": 1,
  "name": "Mathematics",
  "description": "Basic mathematics"
}
```
Errors:
- **404 Not Found** — when subject with given ID does not exist
- **401 / 403** — as with other protected endpoints

#### `POST /api/subjects`

Description: Create a new subject.

Authorization: Bearer <token>

Roles: ADMIN only

Request JSON (`CreateSubjectRequestDto`):
```json
{
  "name": "Mathematics",   // required, not blank
  "description": "Basic mathematics"
}
```

Response JSON (`SubjectDto`):
```json
{
  "id": 42,
  "name": "Mathematics",
  "description": "Basic mathematics"
}
```
Errors:
- **400 Bad Request** — on validation errors (e.g. missing/blank name)
- **401 / 403** — as above

#### `PUT /api/subjects/{id}`

Description: Update an existing subject.

Authorization: Bearer <token>

Roles: ADMIN only

Request JSON (`UpdateSubjectRequestDto`) — fields are optional and will be
applied if present:
```json
{
  "name": "Advanced Math",
  "description": "Covers algebra and calculus"
}
```

Response JSON (`SubjectDto`): updated subject representation

#### `DELETE /api/subjects/{id}`

Description: Delete (soft-delete) a subject by ID.

Authorization: Bearer <token>

Roles: ADMIN only

Response JSON (`SubjectDto`): representation of the deleted subject

Errors common to subject endpoints:
- **401 Unauthorized** — when token is missing or invalid
- **403 Forbidden** — when authenticated user does not have required role
- **404 Not Found** — when resource (subject) does not exist

## :magic_wand: MagicLink & MagicToken Mechanism

The platform supports a **passwordless login flow** using MagicLinks and MagicTokens. This mechanism
allows students to log in by clicking a link in an email instead of using a password.

### How It Works

1. **Generate MagicLink & MagicToken:**
   - The backend generates a `MagicToken` containing:
     - `email`: the student's email address
     - `createdAt`: timestamp of token creation
     - `notificationUuid`: unique identifier for tracking the notification
   - A `MagicToken` is then serialized (encrypted) to create a secure token string
   - The `MagicLink` is constructed with:
     - `baseUrl`: frontend base URL (from configuration)
     - `frontendEndpoint`: the login endpoint on the frontend
     - `paramName`: the query parameter name (e.g., "token")
     - `paramValue`: the serialized token
   - The complete URL example: `https://frontend.example.com/login?token=<encrypted-token>`

2. **Send MagicLink via Email:**
   - The generated `MagicLink` URL is embedded in an email message
   - Student receives the email and clicks the link

3. **Student Clicks the Link:**
   - Frontend extracts the token from the URL query parameter (`token`)
   - Frontend sends the token to the backend for validation and authentication

4. **Backend Validates the Token:**
   - Backend deserializes the encrypted token back to `MagicToken`
   - Validation checks:
     - Token is not expired (expires after `magic-link.expiration-time` seconds from creation)
     - Email address matches the requesting user
     - Notification UUID matches
   - If all checks pass, the student is authenticated and issued a JWT access token

### Configuration

The following properties control the MagicLink behavior (in `application.properties`):

```properties
magic-link.expiration-time=3600      # Token expiration time in seconds (default: 1 hour)
magic-link.secret=your-secret-key    # Secret key for token encryption/serialization
magic-link.base-url=https://frontend.example.com
magic-link.frontend-endpoint=login    # Endpoint on frontend that handles the magic link
magic-link.paramName=token            # Query parameter name for the token
```

### Data Structures

**MagicToken (internal):**
```json
{
  "email": "student@example.com",
  "createdAt": "2026-06-16T10:30:00+02:00",
  "notificationUuid": "550e8400-e29b-41d4-a716-446655440000"
}
```

**MagicLink (for email):**
- Not directly sent as JSON; composed as a URL:
  ```
  https://frontend.example.com/login?token=eyJhbGc...encrypted-token...
  ```
- Components:
  - `baseUrl`: `https://frontend.example.com`
  - `frontendEndpoint`: `login`
  - `paramName`: `token`
  - `paramValue`: encrypted `MagicToken`

### Error Handling

- **InvalidMagicTokenException**: Thrown when:
  - Token is expired
  - Email address mismatch
  - Notification UUID mismatch
  - Token decryption fails
  - Token is malformed or null

## Email delivery logging, MailHog (local testing) & Login notifications

Recent changes introduce email delivery logging and support for local email capture using MailHog. The application now records each attempted email delivery and persists a delivery log with statu[...]

Key points:

- New model: `EmailDeliveryLog` with fields: `recipient` (Email), `subject`, `body` (TEXT), `status` (PENDING/SENT/FAILED), `createdAt`, `errorMessage`, and soft-delete (`isDeleted`). See: `src/m[...]
- New DTO: `SendEmailRequestDto` (validated recipient, subject, body) and `DeliveryStatus` enum.
- Repository: `EmailDeliveryLogRepository` added with helper `findByStatus`.
- Service: `EmailDeliveryService` (interface) and `EmailDeliveryServiceImpl` which:
  - Validates requests
  - Persists an initial delivery log (status PENDING)
  - Sends email via `JavaMailSender`
  - Updates the log to SENT or FAILED and records stacktrace on failure
  - Ensures an `Email` entity exists for the recipient when persisting the log

Sending login notification:

- On successful login the backend triggers a login notification email for the user. This behavior is implemented in `AuthenticationServiceImpl.login` where it calls `EmailDeliveryService.send` wi[...]

MailHog support (docker-compose):

- A MailHog service has been added to docker-compose for local development and integration tests. MailHog exposes SMTP on port `1025` and a web UI on port `8025`.
- The docker-compose service includes a healthcheck and the application is configured to wait for MailHog to be healthy before starting (useful in integration environments).

Example docker-compose snippet (conceptual):
```yaml
mailhog:
  image: mailhog/mailhog:latest
  ports:
    - "1025:1025"   # SMTP
    - "8025:8025"   # Web UI
  restart: unless-stopped
  healthcheck:
    test: ["CMD", "curl", "-f", "http://localhost:8025" ]
    interval: 10s
    timeout: 2s
    retries: 5
```

Example test Spring properties (`src/test/resources/application-test.properties`):
```properties
spring.mail.host=localhost
spring.mail.port=1025
spring.mail.username=
spring.mail.password=
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false
```

Database changelog:

- A Liquibase changelog was added to create the `email_delivery_logs` table and the foreign key to the `emails` table. Check the repository's changelog under `src/main/resources/db/changelog` for[...]

## Notes & Validation

- Email creation/validation elsewhere in the API uses the following DTO (`EmailValueDto`):
  ```json
    {
      "value": "user@example.com"
    }
  ```
  Validation rules:
  - value: required, not blank, must be a valid email format

- Login uses `UserLoginRequestDto` (see above) and returns `UserLoginResponseDto`
  with a single field `token`.

## Examples (curl)

### Login and get token
```bash
curl -X POST "http://localhost:8080/api/auth/login" -H "Content-Type: application/json" -d \
  "{\"email\":\"admin@example.com\",\"password\":\"secret\"}"
```

### Use token to fetch emails (replace <token> with actual token)
```bash
curl -H "Authorization: Bearer <token>" "http://localhost:8080/api/emails?page=0&size=10"
```

### Use token to fetch teachers (ADMIN only)
```bash
curl -H "Authorization: Bearer <token>" "http://localhost:8080/api/teachers?page=0&size=10"
```

### Use token to fetch subjects (ADMIN only)
```bash
curl -H "Authorization: Bearer <token>" "http://localhost:8080/api/subjects?page=0&size=10"
```

## Further information

- For full API documentation (if enabled) check the project's **OpenAPI/Swagger
  UI** (commonly available at `/swagger-ui.html` or `/swagger-ui/index.html` when
  **springdoc-openapi** is configured) or consult the controller and DTO source
  code in `src/main/java/pl/koder95/sbp/backend`.

