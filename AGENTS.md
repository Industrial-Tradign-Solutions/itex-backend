# ITEX Backend

## Build
```bash
./gradlew build
./gradlew bootRun
```

## Tech Stack
- Spring Boot 4.0.7, Java 25, Gradle 9.6.1
- Single-module Gradle project (`rootProject.name = 'ITEX'`)
- PostgreSQL + Flyway migrations (`src/main/resources/db/migration/V1/`, `V2/`)
- Uses Lombok + MapStruct; `lombok-mapstruct-binding` is required so MapStruct
  reads the model after Lombok has generated the accessors
- JWT auth via `io.jsonwebtoken:jjwt:0.12.6`
- JasperReports for PDF generation, Freemarker for email templates
- WebSocket at `/itex/api/ws`
- Jackson 3 (`tools.jackson.*`) is the JSON library. Jackson 2 is still on the
  classpath as a transitive of jjwt/JasperReports/POI; both coexist. Do not add
  new `com.fasterxml.jackson.databind` imports — the annotations package
  (`com.fasterxml.jackson.annotation`) is the one exception and stays.

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
- `config/` — security (JWT, auth aspect, security config), WebSocket, messages, files

## Notes
- `jackson.time-zone: America/New_York` — API returns dates in ET timezone
- `spring.jpa.open-in-view: true` — OSIV enabled; be careful with lazy loading after response committed
- JasperReports fonts JAR at `src/main/resources/fonts/calibri-fonts.jar` must exist for PDF generation
- `spring-boot-starter-aspectj` must stay declared: Boot 4 no longer pulls
  `spring-aspects` transitively, and without it `@AccessToAction` /
  `@AccessToModule` would silently stop enforcing permissions
- No tests currently in `src/test/`; verify changes manually or add tests before submitting