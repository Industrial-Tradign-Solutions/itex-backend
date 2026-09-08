# Manual de Reglas de Negocio — Quote Request (QR)

> **Módulo:** `IP_QUOTE_REQUESTS`
> **Estado del documento:** Estable (refleja el comportamiento actual del backend al 2026-09-05)
> **Base:** `src/main/java/com/itradingsolutions/itex/api/ip/qr/`

## 1. Propósito del módulo y su lugar en el flujo IP

Una **Quote Request (QR)** es la solicitud de cotización que un sales rep envía a un **proveedor** para conseguir precio y lead time de productos del cliente.

Flujo general del negocio:

```
QR (pedir precio al proveedor) → Q (Quotation: consolidar precios + margen) → PO (Purchase Order: comprar)
```

- La QR define el **client** y la **currency** con los que siempre debe operar.
- Sus **productos** llevan `unitPrice`, `leadTime` y `leadTimeType` que el proveedor cotiza.
- Una vez la QR está **`ANSWERED`** (o `COMPLETE` con la opción de "ver completadas"), puede **enlazarse a una Q** del mismo cliente y moneda.
- La QR **no** genera PO directamente: siempre pasa por una Q.

## 2. Campos de la QR

| Campo | Obligatorio | Quién lo asigna | Importancia / regla |
|-------|-------------|-----------------|---------------------|
| `number` | Sí | Sistema (consecutivo `IP/QR` por código de cliente) | Identifica la QR. Único. Se **regenera** si cambia el cliente. |
| `status` | Sí | Sistema / usuario | `CREATED, SENT, ANSWERED, COMPLETE, REJECTED` (ver §4). |
| `currency` | Sí | Usuario | Define la moneda de negociación. Al enlazar una QR a una Q, **debe coincidir** con la moneda de la Q. |
| `client` | Sí | Usuario | Cliente dueño de la solicitud. Cambiarlo regenera el `number`. El contacto (opcional) siempre se valida dentro del cliente. |
| `clientContact` | No | Usuario | Contacto del cliente; debe pertenecer al client. |
| `clientQrNumber` | No | Usuario | Referencia externa del cliente (nº de su solicitud). |
| `salesRep` | Sí | Sistema en creación (usuario autenticado) / editable | Vendedor a cargo de la QR. |
| `supplier` | No en `CREATED`; **Sí para `SENT`/`ANSWERED`** | Usuario | Proveedor que cotiza. Sin proveedor no se puede pasar a `SENT` ni `ANSWERED`. |
| `supplierContact` | No | Usuario | Contacto del proveedor; debe pertenecer al supplier. |
| `supplierQrNumber` | No | Usuario | Referencia externa del proveedor. |
| `paymentTerms` | No | **Auto: desde el supplier** al guardar | Se recalcula cuando cambia el proveedor. **Sobrescritura manual** solo con el permiso `EDIT_PAYMENT_TERMS_IP_QUOTE_REQUESTS` (4002008) y si el payload trae el valor. |
| `remarks` | No | Usuario | Notas visibles para el proveedor (salen en el documento). |
| `internalRemarks` | No | Usuario | Notas internas (no salen en el documento). |
| `shippingPointZipCode` | No | Usuario | Datos de embarque. |
| `freightClass` | No | Usuario | Clase de flete. |
| `fobShippingPoint` | No | Usuario | Punto FOB. |
| `freightCharges` | No | Usuario | Cargo de flete (valor). |
| `pdfUrl` | No | Sistema (al imprimir) | Ruta del PDF generado. En estados **finales** se **reutiliza** el PDF ya generado. |
| `openBy` / `openAt` | No | Sistema (creación y open-lock) | Lock de edición (ver §5). Se limpia al pasar a `COMPLETE`/`REJECTED` y con el unlock nocturno. |
| `products` | — | Usuario | Líneas de producto (ver §6). Al menos 1 producto para imprimir. |
| `otherCharges` | — | Usuario | Cargos adicionales descripción + valor (ver §6). |
| `sentAt` / `answeredAt` / `completeAt` / `rejectAt` | — | Sistema | Fechas de las transiciones (ver §3). |

## 3. Fechas y timestamps

| Fecha | Cuándo se escribe | Cuándo se limpia |
|-------|-------------------|------------------|
| `createdAt` | Creación (zona ET). | Nunca. |
| `sentAt` | `CREATED → SENT`. | Rollback a `CREATED`. |
| `answeredAt` | `SENT → ANSWERED`. | Rollback a `SENT` o `CREATED`. |
| `completeAt` | Paso a `COMPLETE`. | Nunca (estado terminal). |
| `rejectAt` | Paso a `REJECTED`. | Nunca (estado terminal). |
| `openAt` | Creación (se abre sola) y open-lock `EDIT`. | Cierre manual, unlock nocturno o paso a estado terminal. |

> **Regla general:** avanzar escribe el timestamp del destino; **retroceder** (rollback) limpia los timestamps posteriores. Ej.: `ANSWERED → CREATED` limpia `answeredAt` **y** `sentAt`.

## 4. Ciclo de vida de estados

### 4.1 Matriz de transiciones (flujo manual)

| Transición | Tipo | Requisitos | Effecto en fechas | Error si falla |
|------------|------|------------|-------------------|----------------|
| `CREATED → SENT` | Manual (`PATCH /ip/qr/{id}/change-status?status=SENT`) | Proveedor asignado | `sentAt = ahora` | `ip.qr.supplier.required.for.status.change` |
| `SENT → ANSWERED` | Manual | Proveedor asignado; **todos** los productos con `unitPrice` + `leadTime` + `leadTimeType` (`isValidAnswered()`); `sentAt` presente; productos en estado **ACTIVE** | `answeredAt = ahora` | `ip.qr.not-valid-answered`; `ip.qr.products-not-active` |
| `ANSWERED → COMPLETE` | Manual con permiso `COMPLETE_IP_QUOTE_REQUESTS` (4002004) — ver §4.2 | El estado actual debe ser `ANSWERED` | `completeAt = ahora` | `ip.qr.no-manual-complete`; `ip.qr.manual-complete-requires-answered` |
| `ANSWERED → SENT` (rollback) | Manual | Sin Q asociada | `answeredAt = null` | `ip.qr.assigned-to-q` |
| `ANSWERED → CREATED` (rollback) | Manual | Sin Q asociada | `answeredAt = null`, `sentAt = null` | `ip.qr.assigned-to-q` |
| `SENT → CREATED` (rollback) | Manual | — | `sentAt = null` | — |
| `CREATED/SENT/ANSWERED → REJECTED` | Manual (`DELETE /ip/qr/{id}`, permiso `REJECT_IP_QUOTE_REQUESTS`) o automático (scheduler) | Si tiene **Q asociadas**: que **todas** estén `REJECTED` | `rejectAt = ahora`; limpia `openBy`/`openAt` | `ip.qr.assigned-to-q-rejected` |

**Reglas transversales:**
- **Mismo estado:** no se permite ninguna transición a un estado igual al actual → `ip.qr.equal-status`.
- **Estados terminales (`COMPLETE` / `REJECTED`):** son **inmutables**, no se puede salir de ellos → `ip.qr.cannot-change-complete-status` / `ip.qr.cannot-change-rejected-status`.
- Al pasar a estado terminal se **cierra el open-lock** (`openBy`/`openAt = null`).
- El endpoint `change-status` funciona con **acceso al módulo** (no requiere una acción específica). `COMPLETE` manual sí exige la acción 4002004.

### 4.2 COMPLETE manual vs automático

- **Automático (camino principal):** cuando una **Q que contiene sus productos pasa a `ANSWERED`**, la QR se completa automáticamente (ver §7).
- **Manual (camino de respaldo):** permitido **solo** desde `ANSWERED` y **solo** con el permiso `COMPLETE_IP_QUOTE_REQUESTS` (4002004), que no viene asignado por defecto a ningún rol.

## 5. Editabilidad y open-lock

- **Estados editables:** `CREATED`, `SENT`, `ANSWERED`. En `COMPLETE`/`REJECTED` la edición está **bloqueada** → `ip.qr.not-editable-by-status`.
- **Open-lock:** editar exige que la QR esté abierta **por el usuario autenticado**:
  - `PATCH /ip/qr/{id}/open-lock/{id}?type=EDIT` la abre (escribe `openBy`/`openAt`).
  - Si nadie la tiene abierta → `ip.qr.not-block`.
  - Si la tiene **otro usuario** → `ip.qr.not-block-by` (incluye el nombre de quien la tiene).
- **Límite de pestañas:** hay un tope configurable (`itex.tabs.max-tabs-open`) de QRs abiertas por usuario → `ip.qr.not-open-max`.
- **Apertura en creación:** la QR se crea **ya abierta** por el usuario autenticado.
- **Cierre:** `PATCH .../close/{id}`, cierre en lote (`/close-list`) o **unlock nocturno** (scheduler 23:50:30).

### Operaciones y validación de lock por operación

| Operación | Open-lock | Status editable |
|-----------|-----------|-----------------|
| `PUT /ip/qr/{id}` (actualizar campos) | ✅ | ✅ |
| Producto: `POST` / `PUT` | ✅ | ✅ |
| Producto: `DELETE` | ❌ (no lo valida) | ✅ |
| Other charge: `POST` / `PUT` | ✅ | ✅ |
| Other charge: `DELETE` | ❌ (no lo valida) | ✅ |
| `change-status` / imprimir | ✅ (solo si status abierto) | n/a |
| `clone` | ✅ (solo si el original está en status abierto) | n/a |

> **Nota:** el `DELETE` de productos y other charges **solo** valida status editable (no open-lock); los `POST`/`PUT` sí validan ambos. Es un detalle a revisar si se quiere uniformar.

## 6. Líneas de la QR (productos y other charges)

### 6.1 Productos

- `number`: asignado por el sistema (siguiente número, `max + 1`).
- `quantity` y `unitType`: obligatorios.
- `leadTime`, `leadTimeType`, `unitPrice`: opcionales en edición, **pero requeridos por todas las líneas** para poder pasar a `ANSWERED` (`isValidAnswered()`).
- No se admite **duplicar el mismo producto** en la QR → `ip.qr.product.exist`.

### 6.2 Other charges

- Campos: `description` (obligatoria) + `value`.
- No se admite **descripción duplicada** → `ip.qr.other-charges.exist`.

## 7. Procesos automáticos (el sistema, no el usuario)

| # | Disparador | Acción |
|---|-----------|--------|
| 1 | **Una Q que contiene sus productos pasa a `ANSWERED`** | La QR → **`COMPLETE`** (`completeAt = ahora`; historial `STATUS_CHANGE_BY_Q`, usuario = quien respondió la Q). |
| 2 | Ídem, pero la Q **no** usa productos de esta QR | La QR → **`REJECTED`** (`rejectAt = ahora`; historial `STATUS_CHANGE_BY_Q`). |
| 3 | Ídem, pero la QR ya está en estado terminal | Queda intacta (no se toca). |
| 4 | **Job diario 00:00 ET** | QR `CREATED` con `createdAt` ≤ hoy−30 → `REJECTED` (historial `AUTO_REJECTED_TIME`, usuario = sales rep). |
| 5 | Job diario 00:00 ET | QR `SENT` con `sentAt` ≤ hoy−30 → `REJECTED` (historial `AUTO_REJECTED_TIME`). |
| 6 | Job diario 00:00 ET | QR `ANSWERED` con `answeredAt` ≤ hoy−30 **y sin Q asociada** → `REJECTED`. Si está asociada a una Q **no** se auto-rechaza. |
| 7 | **Job diario 23:50:30** | Desbloquear **todas** las QRs abiertas (`openBy`/`openAt = null`). |

Cron literal de los jobs en `IpQuoteRequestScheduler`: unlock `30 50 23 * * *`; auto-rechazo `0 0 0 * * *`.

> El corte de los 30 días es **fecha pura**: una QR vence cuando su fecha de referencia es `hoy − 30` o anterior.

## 8. Relación QR ↔ Q

- Una QR `ANSWERED` (o `COMPLETE` si se pasa `viewCompletedQR=true`) es candidata a enlazarse a una **Q del mismo cliente y misma moneda** (endpoint `GET /ip/qr/available-for-quotation/{id_client}`).
- Al enlazarse: se exige **mismo client** (`findByIdAndClient`) y **misma currency**.
- Mientras esté enlazada a una Q (cualquier estado de la Q que no sea `REJECTED`):
  - **No** puede hacer rollback (`ANSWERED → SENT/CREATED`) → `ip.qr.assigned-to-q`.
  - **No** puede rechazarse manualmente si la Q no está `REJECTED` → `ip.qr.assigned-to-q-rejected` (para rechazar se necesita que todas sus Q estén `REJECTED`).
- **Nota:** la Q puede desvincular la QR mientras la Q esté en `CREATED`.
- **Pendiente:** cuando una Q hace rollback (`ANSWERED → SENT/CREATED`), los estados de las QRs que se completaron/rechazaron **no** se revierten automáticamente.

## 9. Permisos del módulo

| Id | Acción | Uso |
|----|--------|-----|
| 4002001 | `CREATE_IP_QUOTE_REQUESTS` | Crear QR (`POST /ip/qr`). |
| 4002002 | `UPDATE_IP_QUOTE_REQUESTS` | Actualizar QR y sus líneas. |
| 4002003 | `VIEW_HISTORY_IP_QUOTE_REQUESTS` | Ver historial. |
| 4002004 | `COMPLETE_IP_QUOTE_REQUESTS` | **COMPLETE manual** (solo desde `ANSWERED`). No viene en roles por defecto. |
| 4002005 | `CLONE_IP_QUOTE_REQUESTS` | Clonar QR. |
| 4002006 | `REJECT_IP_QUOTE_REQUESTS` | Rechazar QR (`DELETE /ip/qr/{id}`). |
| 4002008 | `EDIT_PAYMENT_TERMS_IP_QUOTE_REQUESTS` | Sobrescribir `paymentTerms` manualmente. |
| — | Sin acción (solo acceso al módulo) | `change-status`, open/close-lock, listar, load-open, imprimir. |

> El id `4002007` queda libre/reservado.

## 10. Mensajes de error (key → cuándo se dispara)

| Key | Cuándo |
|-----|--------|
| `ip.qr.no-manual-complete` | `change-status → COMPLETE` sin el permiso 4002004. |
| `ip.qr.manual-complete-requires-answered` | COMPLETE manual desde un estado distinto de `ANSWERED`. |
| `ip.qr.supplier.required.for.status.change` | `CREATED→SENT` o `SENT→ANSWERED` sin proveedor. |
| `ip.qr.not-valid-answered` | `SENT→ANSWERED` con algún producto sin `unitPrice`/`leadTime`/`leadTimeType` o sin `sentAt`. |
| `ip.qr.products-not-active` | `SENT→ANSWERED` con algún producto no `ACTIVE`. |
| `ip.qr.assigned-to-q` | Rollback (`ANSWERED→SENT/CREATED`) con Q asociada. |
| `ip.qr.assigned-to-q-rejected` | Rechazo manual con una Q asociada que no está `REJECTED`. |
| `ip.qr.equal-status` | Transición a un estado igual al actual. |
| `ip.qr.cannot-change-complete-status` / `ip.qr.cannot-change-rejected-status` | Intentar cambiar un estado terminal. |
| `ip.qr.not-editable-by-status` | Editar campos/líneas con la QR en `COMPLETE`/`REJECTED`. |
| `ip.qr.not-block` / `ip.qr.not-block-by` | Operación con open-lock sin abrir / abierta por otro usuario. |
| `ip.qr.not-open-max` | Alcanzar el tope de pestañas abiertas por usuario. |
| `ip.qr.product.exist` / `ip.qr.other-charges.exist` | Producto/descripción duplicado en la QR. |
| `ip.qr.not-generate-doc` | Imprimir sin productos. |
| `ip.qr.not-exist` | QR inexistente (o línea que no pertenece a la QR). |
| `ip.qr.not-valid-complete` (reemplazada) | Sustituida por la lógica de `no-manual-complete`. |

## 11. Referencias de código

- Servicio principal: `api/ip/qr/service/impl/IpQuoteRequestServiceImpl.java`
- Productos: `api/ip/qr/service/impl/IpQuoteRequestProductServiceImpl.java`
- Other charges: `api/ip/qr/service/impl/IpQuoteRequestOtherChargeServiceImpl.java`
- Controlador: `api/ip/qr/controllers/IpQuoteRequestController.java`
- Scheduler: `api/ip/qr/schedulers/IpQuoteRequestScheduler.java`
- Entidad: `api/ip/qr/models/entities/IpQuoteRequestEntity.java` (`isValidAnswered()`)
- Estados: `api/ip/qr/models/enums/IpQuoteRequestStatus.java`