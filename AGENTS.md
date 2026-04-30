# ITEX Backend

## Build
```bash
./gradlew build
./gradlew bootRun
```

## Tech Stack
- Spring Boot 3.3.6, Java 21
- Single-module Gradle project (`rootProject.name = 'ITEX'`)
- PostgreSQL + Flyway migrations (`src/main/resources/db/migration/V1/`, `V2/`)
- Uses Lombok + MapStruct (annotation processor chain matters: lombok -> mapstruct-processor)
- JWT auth via `io.jsonwebtoken:jjwt:0.12.6`
- JasperReports for PDF generation, Freemarker for email templates
- WebSocket at `/itex/api/ws`

## Run
Requires environment variables:
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASS` — PostgreSQL connection
- `TOKEN_SECRET_KEY` — JWT signing key
- `MAIL_USER`, `MAIL_PASS` — SMTP credentials
- `ENCRYPTION_KEY` — app-level encryption
- `WEB_URL` — frontend URL (default: `http://localhost:4200/login`)
- `FOLDER_ROUTE_DATA`, `FOLDER_ROUTE_TEMP`, `FOLDER_ROUTE_JASPER` — file paths
- `SERVER_PORT` (default: 8080)

Server starts at `http://localhost:8080/itex/api`

## Architecture
- `com.itradingsolutions.itex.ItexApplication` — main entrypoint
- `api/` — REST controllers and services (grouped by domain: partners, masters, common, etc.)
- `config/` — security (JWT, auth aspect, security config), WebSocket, fonts, messages, files

## Notes
- `jackson.time-zone: America/New_York` — API returns dates in ET timezone
- `spring.jpa.open-in-view: true` — OSIV enabled; be careful with lazy loading after response committed
- JasperReports fonts JAR at `src/main/resources/fonts/calibri-fonts.jar` must exist for PDF generation
- No tests currently in `src/test/`; verify changes manually or add tests before submitting