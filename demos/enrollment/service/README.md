# Enrollment Service (DDD Demo)

This is a multi-module Java project demonstrating DDD Layered Architecture with Hexagonal (Ports & Adapters).

- **Layers (modules):**
  - `domain/` – Pure domain model and ports (no framework deps)
  - `application/` – Use cases/services orchestrating domain ports (no framework deps)
  - `infrastructure/` – Adapters (mock and DB), Spring wiring, MyBatis, Liquibase
  - `api/` – REST controllers and API runtime (Spring Boot)

## Project Layout

- **Root Maven:** `ddd-demo/service/pom.xml` (parent POM)
- **Key packages:**
  - Domain: `domain/src/main/java/com/example/enrollment/domain/...`
    - Registration flow: `.../enrollmentprocess/` (e.g., `RegistrationEnrollment`, `Otp`, `RegistrationEnrollmentRepository`)
    - Email templates: `.../emailtemplates/` (e.g., `EmailTemplateManager`, `EmailTemplateRepository`)
    - Ports: `.../enrollmentprocess/ports/` (e.g., `CaptchaService`, `MailService`, `PlayerManagementProvider`, `WalletService`)
  - Infrastructure:
    - Mock adapters: `infrastructure/src/main/java/.../infrastructure/mock/`
    - Email renderers: `.../adapters/emailrenderers/` (`QRCodeRenderer`, `Save2PhotoRender`)
    - Registration repo (DB): `.../infrastructure/mybatis/` (`MyBatisRegistrationEnrollmentRepository`, mapper + XML)
    - Spring wiring: `.../infrastructure/config/InfrastructureConfig.java`
    - Liquibase: `infrastructure/src/main/resources/db/changelog/`
    - MyBatis mappers: `infrastructure/src/main/resources/mappers/`
  - API: `api/src/main/java/com/example/enrollment/api/web/`

## Build & Test

Run from `ddd-demo/service/`:

```bash
# All modules
mvn -q -T 1C test

# Domain only
mvn -q -pl domain test

# Infrastructure (ensure reactor builds deps)
mvn -q -am -pl infrastructure test
```

## Run the API

### Mock profile (default)

- Uses in-memory adapters and logging mail service.

```bash
mvn -q -pl api spring-boot:run
```

- Swagger UI: http://localhost:8080/swagger-ui/index.html

### DB profile

- Configure environment variables (or JVM system properties):

```powershell
# Windows PowerShell examples
$env:DB_URL="jdbc:mysql://localhost:3306/enrollment_demo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="password"
```

- Start API with DB profile:

```bash
mvn -q -pl api spring-boot:run -Dspring-boot.run.profiles=db
```

- DB profile configuration lives in `api/src/main/resources/application.yaml` under the `db` profile.

## Registration Flow (REST)

- **Start enrollment** – `POST /api/registration/start`
  - Body (`application/json`):
    ```json
    { "email": "user@example.com", "fullName": "Demo User", "captcha": "abc" }
    ```
  - Response: `201 Created` with body `{ "id": "<enrollmentId>" }`

- **Verify OTP** – `POST /api/registration/{id}/verify-otp`
  - Body:
    ```json
    { "otp": "123456" }
    ```
  - Response: `200 OK`

- Language: optional `?lang=EN|ZH` query param (defaults to `EN`).

Controller: `api/src/main/java/com/example/enrollment/api/web/RegistrationController.java`.

## Email Templates

- Port: `EmailTemplateRepository` in the domain.
- Manager: `EmailTemplateManager` applies parameter substitution (`$$param$$`) and runs optional renderers.
- Mock repository: `infrastructure/.../mock/EmailTemplateRepositoryInMemory.java` (active in `mock` and `db` for demo) with seeded templates:
  - `otp` → body `$$otp$$`
  - `welcome` → body `$$player_name$$|$$rank$$`
- Renderers wired in `InfrastructureConfig`:
  - `QRCodeRenderer` (replaces `$$player_card$$` with encoded card id)
  - `Save2PhotoRender` (replaces `$$card_query$$` with Base64 JSON of card data)

## Adapters & Profiles

- **Mock adapters** (active in `mock`):
  - `MockCaptchaService`, `LoggingMailService`, `MockPlayerManagementProvider`, `GoogleWalletMock`, `AppleWalletMock`
  - Registration repository: `RegistrationEnrollmentRepositoryInMemory`
- **DB adapters** (active in `db`):
  - Registration repository: `MyBatisRegistrationEnrollmentRepository`

Beans are wired in `infrastructure/src/main/java/.../config/InfrastructureConfig.java`.

## Database Schema (Liquibase)

- Master: `infrastructure/src/main/resources/db/changelog/db.changelog-master.yaml`
- Registration table: `db.changelog-1.1.yaml` → `registration_enrollments`
- Mapper XML: `infrastructure/src/main/resources/mappers/RegistrationEnrollmentMapper.xml`

## DDD & Hexagonal Notes

- **Domain/Application purity**: No Spring or infra deps in `domain/` or `application/`.
- **Ports**: Defined in `domain/.../ports/`.
- **Adapters**: Implemented in `infrastructure/` (mock and DB).
- **Factories**: Aggregates rehydrated via static factories (e.g., `RegistrationEnrollment.rehydrate(...)`, `Otp.rehydrate(...)`).

## Environment & Misc

- `.gitignore` is present at `ddd-demo/.gitignore` to ignore `target/`, IDE files, etc.
- Swagger is enabled by default (`springdoc`).

## Troubleshooting

- If infra tests fail to resolve module deps, run with reactor:
  ```bash
  mvn -q -am -pl infrastructure test
  ```
- If DB profile fails, verify env vars and that Liquibase applied tables.
