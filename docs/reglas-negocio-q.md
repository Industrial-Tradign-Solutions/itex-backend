# Manual de Reglas de Negocio — Quotation (Q)

> **Módulo:** `IP_QUOTATIONS`
> **Estado del documento:** ⚠️ **BORRADOR** — el módulo de Q aún tiene **ajustes pendientes** (ver §11). Este manual refleja el comportamiento vigente al 2026-09-05 y debe actualizarse cuando se apliquen esos ajustes.
> **Base:** `src/main/java/com/itradingsolutions/itex/api/ip/q/`

## 1. Propósito del módulo y su lugar en el flujo IP

Una **Quotation (Q)** es la cotización formal que se le entrega al **cliente**: consolida productos y cargos de una o más **QR** del mismo cliente y moneda, y les aplica márgenes y condiciones de venta.

Flujo general del negocio:

```
QR (pedir precio al proveedor) → Q (Quotation: consolidar precios + margen) → PO (Purchase Order: comprar)
```

- La Q se crea para un **client** y una **currency**; solo admite **QR del mismo cliente y misma moneda**.
- Cuando la Q pasa a **`ANSWERED`**, completa o rechaza automáticamente las QR involucradas (ver §7).
- Una Q `ANSWERED` (o `COMPLETE` con la opción de ver completadas) queda **disponible para generar POs**.

## 2. Campos de la Q

| Campo | Obligatorio | Quién lo asigna | Importancia / regla |
|-------|-------------|-----------------|---------------------|
| `number` | Sí | Sistema (consecutivo `IP/Q` por código de cliente) | Identifica la Q. Único. Se **regenera** si cambia el cliente. |
| `status` | Sí | Sistema / usuario | `CREATED, SENT, ANSWERED, COMPLETE, REJECTED` (ver §4). |
| `currency` | Sí | Usuario | **Condicional**: solo se puede cambiar si **ninguna QR enlazada** tiene otra moneda → `ip.q.currency-mismatch`. |
| `client` | Sí | Usuario | **Condicional**: solo se puede cambiar si la Q **no tiene QR enlazadas** → `ip.q.client-change-blocked`. Al cambiar: regenera `number`, toma `paymentTerms` del nuevo cliente y limpia el contacto. |
| `clientContact` | No | Usuario | Debe pertenecer al client. |
| `clientQNumber` | No | Usuario | Referencia externa del cliente. |
| `salesRep` | Sí | Sistema en creación (usuario autenticado) / editable | Vendedor a cargo. |
| `remarks` | No | Usuario | Notas para el cliente (documento). |
| `internalRemarks` | No | Usuario | Notas internas. |
| `leadTime` + `leadTimeType` | Sí | Usuario (default `0` / `DAYS` en creación) | Plazo de entrega ofrecido. |
| `validity` + `validityType` | Sí | Usuario (default `0` / `DAYS` en creación) | Vigencia de la cotización. |
| `incoterms` | No | Usuario | Incoterm del documento. |
| `paymentTerms` | Sí | **Auto: desde el client** en creación; editable en `PUT` | No hay gate de permiso en el `PUT` (a diferencia de QR). Existe la acción `EDIT_PAYMENT_TERMS_IP_QUOTATIONS` (4003006) pero **no se usa** en el flujo de actualización actual. |
| `applicationAt` | Sí (para `SENT`/`ANSWERED`) | Usuario | Fecha de aplicación ofrecida, **solo fecha (sin hora)**. Es **requerida** para poder pasar a `SENT` o `ANSWERED` → `ip.q.application-at-required`. |
| `pdfUrl` | No | Sistema (al imprimir) | PDF generado; en estados **finales** se **reutiliza**. |
| `openBy` / `openAt` | No | Sistema (open-lock) | Lock de edición (ver §5). Se limpia en estados finales y con el unlock nocturno. |
| `quoteRequestsQuotations` | — | Usuario | **QRs enlazadas** (reglas en §8). |
| `otherCharges` | — | Usuario | Cargos **manuales** de la Q (valor + descripción). |
| Other charges **importados** de QR | — | Usuario | Cargos importados desde las QR enlazadas (ligados a una QR). |
| `sentAt` / `answeredAt` / `completeAt` / `rejectAt` | — | Sistema | Fechas de las transiciones (ver §3). |

## 3. Fechas y timestamps

| Fecha | Cuándo se escribe | Cuándo se limpia |
|-------|-------------------|------------------|
| `createdAt` | Creación (zona ET). | Nunca. |
| `applicationAt` | Creación o `PUT`. | En `clone` (la copia nace sin fecha) y si se manda vacía. |
| `sentAt` | `CREATED → SENT`. | Rollback a `CREATED`. |
| `answeredAt` | `SENT → ANSWERED`. | Rollback a `SENT` o `CREATED`. |
| `completeAt` | Paso a `COMPLETE`. | Nunca (estado terminal). |
| `rejectAt` | Paso a `REJECTED`. | Nunca (estado terminal). |
| `openAt` | Open-lock `EDIT`. | Cierre manual, unlock nocturno o paso a estado terminal. |

> **Regla general:** avanzar escribe el timestamp del destino; **retroceder** limpia los timestamps posteriores. Ej.: `ANSWERED → CREATED` limpia `answeredAt` **y** `sentAt`. La fecha de aplicación es **solo fecha** y se valida antes de `SENT`/`ANSWERED`.

## 4. Ciclo de vida de estados

### 4.1 Matriz de transiciones (flujo manual)

| Transición | Tipo | Requisitos | Effecto en fechas | Error si falla |
|------------|------|------------|-------------------|----------------|
| `CREATED → SENT` | Manual (`PATCH /ip/q/{id}/change-status?status=SENT`) | **≥1 QR enlazada** y **≥1 producto** (`HAS_QUOTE_REQUESTS` + `HAS_PRODUCTS`); `applicationAt` presente | `sentAt = ahora` | `ip.q.not-valid-sent`; `ip.q.application-at-required` |
| `SENT → ANSWERED` | Manual | ≥1 producto; `sentAt` presente; `applicationAt` presente | `answeredAt = ahora`; **dispara transiciones automáticas de las QR** (§7, que las deja inmutables) | `ip.q.not-valid-answered`; `ip.q.application-at-required` |
| `ANSWERED → COMPLETE` | Manual | ≥1 producto; `answeredAt` presente; **≥1 PO asociada** | `completeAt = ahora` | `ip.q.not-valid-complete`; `ip.q.complete-requires-po` |
| `ANSWERED → SENT` (rollback) | Manual | **Sin PO asociada** | `answeredAt = null` | `ip.q.cannot-revert-with-po` |
| `ANSWERED → CREATED` (rollback) | Manual | Sin PO asociada | `answeredAt = null`, `sentAt = null` | `ip.q.cannot-revert-with-po` |
| `SENT → CREATED` (rollback) | Manual | — (no valida PO asociada en este caso) | `sentAt = null` | — |
| `CREATED/SENT/ANSWERED → REJECTED` | Manual (`DELETE /ip/q/{id}`, permiso `REJECT_IP_QUOTATIONS`) | **Sin PO asociada** | `rejectAt = ahora`; limpia `openBy`/`openAt` | `ip.q.cannot-reject-with-po` |

**Reglas transversales:**
- **Mismo estado:** no se permite → `ip.q.equal-status`.
- **Estados terminales (`COMPLETE` / `REJECTED`):** inmutables → `ip.q.cannot-change-complete-status` / `ip.q.cannot-change-rejected-status`.
- Al pasar a estado terminal se **cierra el open-lock**.
- `change-status` con `REJECTED` **no está permitido**: el controlador lo rechaza (`IllegalArgumentException`); el rechazo va por `DELETE`.
- **`change-status` NO exige open-lock.** Sí exige status no terminal (los cambios de estado no pasan por `validateQuotationEditable`).
- **Respuesta del endpoint:** desde la última corrección, `PATCH /ip/q/{id}/change-status` devuelve el **objeto completo** (`IpQuotationResponse`), igual que `reject`; ya no devuelve el resumen `ListIpQuotationResponse`.

### 4.2 Validación extra al enviar ($4.1 note)

> El `change-status` de Q **no** fuerza open-lock; pero para **editar** campos/productos/cargos sí se exige (ver §5). La distinción es intencional: el estado puede avanzarse sin lock, la edición no.

## 5. Editabilidad y open-lock

- **Estados editables:** `CREATED`, `SENT`, `ANSWERED`. En `COMPLETE`/`REJECTED` la edición está **bloqueada** → `ip.q.not-editable-by-status`.
- **Open-lock obligatorio para editar:** cualquier modificación (campos, productos, other charges manuales o importados) exige que la Q esté abierta **por el usuario autenticado** (`PATCH /ip/q/{id}/open-lock/{id}?type=EDIT`):
  - Nadie la tiene abierta → `ip.q.not-block`.
  - La tiene otro usuario → `ip.q.not-block-by` (con el nombre).
- **Límite de pestañas:** tope configurable (`itex.tabs.max-tabs-open`) → `ip.q.not-open-max`.
- **Cierre:** `PATCH .../close/{id}` o unlock nocturno (scheduler 23:53).

### Operaciones y validación por operación

| Operación | Status editable + open-lock | Regla extra |
|-----------|----------------------------|-------------|
| `PUT /ip/q/{id}` (campos) | ✅ ambos | Además: Currency/Client condicionales (§2); `IntegrityValidator` antes de guardar |
| Producto: `POST` / `PUT` / `DELETE` | ✅ ambos | No duplicar producto de QR en el request; **productos en `DRAFT` prohibidos**; deduplica por producto (primera línea gana) |
| Other charge manual: `POST` / `PUT` / `DELETE` | ✅ ambos | — |
| Import de other charges desde QR (`POST .../import-from-qr`) | ✅ ambos | Además bulk import; logra historial por línea |
| Other charge importado: `DELETE .../imported-from-qr/{id}` | ✅ ambos | — |
| Add/remove de **QR** a la Q (`POST/DELETE .../quote-requests...`) | ❌ NO usa editable genérico: **solo `CREATED`** | `ip.q.qr.cannot-add` / `ip.q.qr.cannot-delete`; QR duplicada → `ip.q.qr.duplicate`; mismo client + misma currency |
| `change-status` | ❌ NO exige open-lock | Requisitos de §4 |
| `print` / `clone` | ✅ open-lock (solo si estado abierto) | `ip.q.not-generate-doc` sin QRs; en terminal reusa el PDF |

## 6. Líneas de la Q (productos y other charges)

### 6.1 Productos

- Se agregan a partir de los productos de las **QR enlazadas** (cada línea de la Q referencia el producto de la QR de origen; `unitPrice`/`leadTime` provienen de la línea original de la QR).
- El margen (`profitMargin`) y la condición (`condition`) se agregan a nivel de la Q.
- **No** se permite el mismo producto de QR duplicado dentro de un request (`ip.q.product.duplicate-qrproduct-in-request`).
- **No** se permite agregar productos cuyo producto maestro esté en `DRAFT` (`ip.q.product.draft-not-allowed`).
- Si el mismo `productId` llega varias veces en un request, se **deduplica**: gana la primera, el resto se omite.

### 6.2 Other charges

- **Manuales:** cargos propios de la Q (valor + descripción); se manejan con el permiso genérico de actualización.
- **Importados desde QR:** se pueden listar `GET /ip/q/{id}/other_charges/available-from-qr`, importarse en lote (`import-from-qr`) y eliminarse (`imported-from-qr/{id}`). Cada cargo importado queda ligado a su QR.
- Totales: `freightCharges`/`total` del response suman solo los cargos y productos de QR que tienen productos en la Q.

## 7. Procesos automáticos (el sistema, no el usuario)

| # | Disparador | Acción |
|---|-----------|--------|
| 1 | **Q pasa a `ANSWERED`** (manual) | QR con ≥1 producto en la Q → **`COMPLETE`**; QR sin productos en la Q → **`REJECTED`**; QR ya terminal → intacta. Historial `STATUS_CHANGE_BY_Q`, usuario = quien respondió la Q. |
| 2 | **Job diario 23:53** | Desbloquear **todas** las Q abiertas (`openBy`/`openAt = null`). |
| 3 | **Job diario 23:54** | Q `CREATED` con `createdAt` > 45 días → se marca `REJECTED` (`rejectAt = ahora`). ⚠️ **No usa `changeStatus`**: no valida nada, no escribe historial, no limpia el open-lock ni revierte QR. **Ver pendientes §11.** |

Cron literal en `IpQuotationScheduler`: unlock `0 53 23 * * *`; auto-rechazo `0 54 23 * * *`.

## 8. Relación Q ↔ QR y Q ↔ PO

### 8.1 Q ↔ QR

- A la Q solo se pueden enlazar QR del **mismo client** (validadas por `findByIdAndClient`) y de la **misma currency** (`validateQuoteRequestCurrency` → `ip.q.currency-mismatch`).
- El enlace solo es posible si la Q está en **`CREATED`** (`ip.q.qr.cannot-add` / `ip.q.qr.cannot-delete`). Una vez la Q avanza, las QR enlazadas quedan **inmutables** (no se pueden desvincular).
- Al pasar a `ANSWERED` (§7) las QR enlazadas se completan o rechazan según lleven productos en la Q.
- **Pendiente:** si la Q hace rollback, las QR no vuelven a su estado anterior (ver §11).

### 8.2 Q ↔ PO

- Pasar a **`COMPLETE`** requiere **≥1 PO asociada** → `ip.q.complete-requires-po`.
- **Rechazar** la Q requiere **no tener PO** → `ip.q.cannot-reject-with-po`.
- **Rollback** desde `ANSWERED` (a `SENT`/`CREATED`) requiere **no tener PO** → `ip.q.cannot-revert-with-po`.
- Disponibilidad para crear PO: se listan las Q en `ANSWERED` (y `COMPLETE` si `viewCompleted=true`) por cliente y moneda.

## 9. Permisos del módulo

| Id | Acción | Uso |
|----|--------|-----|
| 4003001 | `CREATE_IP_QUOTATIONS` | Crear Q (`POST /ip/q`). |
| 4003002 | `UPDATE_IP_QUOTATIONS` | Actualizar Q, productos, other charges (manuales e importados). |
| 4003003 | `VIEW_HISTORY_IP_QUOTATIONS` | Ver historial. |
| 4003004 | `CLONE_IP_QUOTATIONS` | Clonar Q. |
| 4003005 | `REJECT_IP_QUOTATIONS` | Rechazar Q (`DELETE /ip/q/{id}`). |
| 4003006 | `EDIT_PAYMENT_TERMS_IP_QUOTATIONS` | Existe en el enum pero **no se usa** en el flujo de actualización actual (el `PUT` permite modificar `paymentTerms` sin gate). |
| — | Sin acción (solo acceso al módulo) | `change-status`, open/close-lock, listar, imprimir. |

## 10. Mensajes de error (key → cuándo se dispara)

| Key | Cuándo |
|-----|--------|
| `ip.q.not-valid-sent` | `CREATED→SENT` sin QR enlazadas o sin productos. |
| `ip.q.not-valid-answered` | `SENT→ANSWERED` sin productos o sin `sentAt`. |
| `ip.q.not-valid-complete` | `ANSWERED→COMPLETE` sin `answeredAt`/sin productos. |
| `ip.q.application-at-required` | Pasar a `SENT`/`ANSWERED` sin `applicationAt`. |
| `ip.q.complete-requires-po` | `COMPLETE` sin ninguna PO asociada. |
| `ip.q.cannot-reject-with-po` | Rechazo con PO asociada. |
| `ip.q.cannot-revert-with-po` | Rollback (`ANSWERED→SENT/CREATED`) con PO asociada. |
| `ip.q.cannot-change-complete-status` / `ip.q.cannot-change-rejected-status` | Intentar cambiar un estado terminal. |
| `ip.q.equal-status` | Transición a un estado igual al actual. |
| `ip.q.client-change-blocked` | Cambiar el cliente con QR enlazadas. |
| `ip.q.currency-mismatch` | Cambiar la moneda con una QR enlazada de otra moneda (o enlazar una QR de otra moneda). |
| `ip.q.qr.cannot-add` / `ip.q.qr.cannot-delete` | Agregar/quitar QR con la Q fuera de `CREATED`. |
| `ip.q.qr.duplicate` | Agregar una QR ya enlazada. |
| `ip.q.not-editable-by-status` | Editar campos/líneas con la Q en `COMPLETE`/`REJECTED`. |
| `ip.q.not-block` / `ip.q.not-block-by` | Operación con open-lock sin abrir / abierta por otro usuario. |
| `ip.q.not-open-max` | Alcanzar el tope de pestañas abiertas por usuario. |
| `ip.q.product.duplicate-qrproduct-in-request` | Mismo producto de QR repetido en un request de alta. |
| `ip.q.product.draft-not-allowed` | Agregar un producto en estado `DRAFT`. |
| `ip.q.not-generate-doc` | Imprimir sin QRs. |
| `ip.q.not-exist` | Q inexistente. |

## 11. Ajustes pendientes del módulo (por lo que este documento es BORRADOR)

1. **Auto-rechazo de Q viejas (45 días):** el job de las 23:54 no pasa por `changeStatus`: escribe `status` + `rejectAt` directo. **No cierra el open-lock**, **no escribe historial** y **no** revierte/libera las QR enlazadas. Decidir si alinearlo con el flujo normal.
2. **Rollback de QR cuando la Q retrocede:** al volver `ANSWERED → SENT/CREATED`, las QR que se completaron/rechazaron **no** se restauran. Definir si el rollback debe revertir también las QR.
3. **`SENT → CREATED` no valida PO:** a diferencia del rollback desde `ANSWERED`, volver de `SENT` a `CREATED` no bloquea si hay PO. Confirmar si es intencional.
4. **`DELETE` de productos/other charges de la QR no exige open-lock** (solo status editable) — al revés que en Q, donde sí se exige ambos. Uniformar criterio si aplica.

## 12. Referencias de código

- Servicio principal: `api/ip/q/service/impl/IpQuotationServiceImpl.java` (mapa `TRANSITIONS`, §835)
- Productos: `api/ip/q/service/impl/IpQuotationProductServiceImpl.java`
- Other charges: `api/ip/q/service/impl/IpQuotationOtherChargeServiceImpl.java`
- Import de cargos desde QR: `api/ip/q/service/impl/IpQuotationOtherChargesQuoteRequestServiceImpl.java`
- Controlador: `api/ip/q/controller/IpQuotationController.java`
- Scheduler: `api/ip/q/schedulers/IpQuotationScheduler.java`
- Entidad: `api/ip/q/models/entities/IpQuotationEntity.java`
- Estados: `api/ip/q/models/enums/IpQuotationStatus.java`