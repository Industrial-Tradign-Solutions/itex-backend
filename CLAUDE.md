# ITEX Backend

Spring Boot 3.3.6 / Java 21 / PostgreSQL REST API for iTradingSolutions' internal operations
(procurement → quoting → purchase orders → invoicing).

Setup, environment variables and the dependency list live in [README.md](README.md) and
[AGENTS.md](AGENTS.md) — not repeated here. This file covers the conventions that are not visible
from the file tree and that are easy to get wrong.

> **README.md is partly stale.** Its "Módulos de la API" endpoint list is wrong (real base paths are
> `/partners/suppliers`, `/ip/qr`, `/master/locations/countries`, … — see below), its package tree
> predates the `sales/` area, and it references an `OPTIMIZATION_PLAN.md` that no longer exists.
> Trust the code over that section.

## Build

The machine's default `java` is JDK 11 and the Spring Boot Gradle plugin needs 17+, so the Gradle
daemon must be pointed at a modern JDK explicitly — plain `./gradlew build` fails here:

```bash
JAVA_HOME="/c/Program Files/Amazon Corretto/jdk21.0.12_8" ./gradlew compileJava -q
```

**There is no test source set** — `src/test` does not exist. Verification is `compileJava` plus
manual API calls. Do not describe a change as "tested" on the strength of a successful compile; say
what was actually run.

## Controller base paths

```
/admin/roles  /admin/users  /email
/master/brands  /master/departments  /master/industries
/master/locations/{countries,states,cities}
/partners/clients  /partners/suppliers
/ip/products  /ip/qr  /ip/q  /ip/po        (+ /{id}/product and /{id}/other_charges sub-resources)
/sales/invoice                              (+ /{invoiceId}/history)
```

Child collections (products, charges) get their own controller under the parent path rather than
being embedded in the parent's update payload.

## Module layout

`com.itradingsolutions.itex.api.<area>.<module>`, area ∈ `admin`, `common`, `masters`, `partners`,
`ip` (Industrial Procurement), `sales`. Cross-cutting infrastructure is in
`com.itradingsolutions.itex.config`.

Every transactional module repeats the same shape (some use singular `controller`/`repository`,
others plural — follow whatever the module you are in already uses):

```
<module>/
  controller(s)/     thin HTTP adapters extending CommonController
  service/           interfaces + stateless @Component collaborators
  service/impl/      implementations extending UtilServiceAbs
  repository|repositories/
  exceptions/
  models/entities/   JPA, extend BaseEntity (id + createdAt via @PrePersist)
  models/dto/        Lombok classes extending BaseDTO
  models/request(s)/ records
  models/response(s)/ records, or Lombok classes extending BaseResponse
  models/mapper(s)/  MapStruct
  models/enums/
  models/filters/    Specification builders extending BaseFilter
```

**Naming.** Classes inside a module carry the module noun as prefix (`InvoiceAccessGuard`,
`InvoiceMutationGuard`, `InvoiceShipToResolver`). Services are named for the **operation** they
perform — `Query`, `Lock`, `Clone`, `History`, `Save` — never for an architectural category. Do not
introduce CQRS/DDD vocabulary (`Command`, `Handler`, `Aggregate`); none of it exists in this
codebase.

## Conventions that are easy to get wrong

**Immutability boundary.** `records` at the HTTP edge (Request/Response); mutable Lombok DTOs
inside. Do not convert DTOs to records — they extend `BaseDTO`, are populated by MapStruct, and
carry hand-written normalizing setters (`trim()`, `toUpperCase()`, and `setXxxId(UUID)` helpers that
build a stub child DTO). Those setters are why request→DTO goes through MapStruct at all.

**Mapping direction.** MapStruct handles `request → DTO`, `entity → DTO`, `DTO → response`, and
`entity → entity` (clone, with an explicit `@Mapping(ignore = true)` list). `DTO → entity` is
**always manual** in the service, because foreign keys must be resolved into managed entities
through their owning service:

```java
clientService.findClientById(id, true)                  // boolean = reject INACTIVE
clientContactService.findById(contactId, clientId)      // validates the contact belongs to that client
cityService.findEntityById(id)
userService.findEntityById(id, true)
```

Never resolve a sibling module's entity through its repository directly.

**Permissions, two layers.**
- Hard: `@AccessToAction(action = ModuleAction.X)` / `@AccessToModule(option = ModuleOption.Y)` on
  controller methods, enforced by `ValidateAccessAspect`, which throws. `@AccessToAction` implicitly
  checks module access too; `SUPER_ADMIN_ID` bypasses both.
- Soft: `validateAction(user, ModuleAction.X)` from `UtilServiceAbs` returns a boolean, for per-field
  gating inside services (e.g. only honour a `paymentTerms` override when the caller holds
  `EDIT_PAYMENT_TERMS_*`).

Adding an action means editing **both** the `ModuleAction` enum and the `t_actions` INSERT in the
owning migration.

**Messages.** Never hardcode user-facing strings. Use `simpleMessage(key)` / `compositeMessage(key,
String[])` (on `CommonController` and `UtilServiceAbs`) against
`src/main/resources/messages/validation-messages.properties`. Message text is English.

**Dropdown enums.** Any enum the frontend lists implements `BaseEnum` (single `String getName()`
backed by a `@Getter` display-name field) and is registered with one line in
`UtilController.listSystemEnums()` → `GET /common/static_lists`. Internal/audit enums
(`*HistoryAction`) are deliberately **not** registered.

**Open/lock.** Editable documents carry `openBy` + `openAt`; a mutation requires the caller to hold
the lock. `itex.tabs.max-tabs-open` caps concurrently open documents per user — check it before
acquiring a lock, including on create. Prefer an atomic conditional
`UPDATE ... WHERE open_by IS NULL` over read-then-write.

**History.** Each transactional module has a `*HistoryService` with
`addHistory(action, oldDto, newDto)` writing a JSON diff to `t_*_history`. Capture `oldDto` **before
mutating** and call the history service **inside the same `@Transactional` service method**. QR and
PO call it from the controller, outside the transaction, so the audit row is lost when the write
fails — do not copy that.

**Time.** `UtilServiceAbs.zoneId` is `America/New_York`. Use `ZonedDateTime.now(zoneId)`, never the
bare `now()`.

**Money.** Amounts are `NUMERIC(15,5)`, handled internally with 5 decimals and `RoundingMode.HALF_UP`.
Rounding to 2 decimals happens **only** when generating the PDF.

**No `assert`.** Assertions are disabled at runtime — the `assert query != null` lines in the QR/Q/PO
filters are dead code. Do not add more.

**OSIV is on** (`spring.jpa.open-in-view: true`), which hides lazy-loading problems until the
response is being written. Do not rely on it: resolve what you need inside the transaction.

## Flyway

Migrations: `src/main/resources/db/migration/V1/` and `V2/` (recursive classpath scan; there is no
`spring.flyway.*` block). Naming `V<major>.<minor>.<patch>__<Description>.sql`.

Rollback scripts in `src/main/resources/db/rollback/V2/` are **manual, not Flyway-managed** — they
live outside the migration location and start by deleting their own `flyway_schema_history` row.
Only V2 has them; a new V2 migration should come with one.

Editing an already-applied migration invalidates its checksum and forces a repair or schema rebuild.
Prefer adding a new migration unless the user explicitly says otherwise.

## Two independent consecutive mechanisms

Do not mix them up:

- **Generic** — `api/common/consecutive`, `IConsecutiveService`. Used by QR, Quotations, POs.
  Produces a **String** keyed by `(module, department, clientCode, YY, MM)`. Two-phase:
  `generateConsecutive(...)` computes `max+1` without reserving, then `saveConsecutive(...)` persists
  after the entity is saved — there is a race window between the two.
- **Sales** — `api/common/salesconsecutive`, `ISalesConsecutiveService`. Used by Invoices and Credit
  Memos. Produces a **long**, global (not per-department), via `generate(SalesConsecutiveType)` under
  a pessimistic row lock, reusing the lowest number returned to the free list by `release(...)`.
  `DRAFT_*` types reuse released numbers; final types never do.

Numbers persist as `BIGINT`. Zero-padding (`String.format("%06d", n)`) is presentation — applied in
the mapper or the PDF, never in the DB.

## Language

Code, identifiers, comments, Javadoc, commit messages and user-facing message strings: **English**.
Design documents under `docs/` and conversation with the user: **Spanish**.

## Design authority

- `docs/itex-invoicing-guide.md` — business spec for invoicing (statuses, totals, `due_at` rules,
  numbering, per-sales-rep scoping). Read it before changing invoice behaviour. When an instruction
  contradicts it, say so explicitly instead of silently picking one.
- `docs/itex-invoices-api.md` — endpoint contracts for the invoice module.
- `docs/deuda-tecnica-modulos-transaccionales.md` — catalogue of known debt in QR/Q/PO/Products with
  file:line evidence. It is a record, not a backlog: do not "fix" those findings as a side effect of
  unrelated work.
