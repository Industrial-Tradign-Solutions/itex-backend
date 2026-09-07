# Comparativa de Reglas de Negocio — QR ↔ Q (paridad y oportunidades)

> **Fecha:** 2026-09-05
> **Propósito:** documentar qué lógica de negocio tiene **QR y no Q**, y qué tiene **Q y no QR**, para decidir qué mejoras cruzar de un módulo al otro.
> **Fuente:** `docs/reglas-negocio-qr.md` y `docs/reglas-negocio-q.md` (verificadas contra el código al día de hoy).

## Cómo leer este documento

- §2 → reglas que existen **solo en QR** (candidatas a portarse a Q).
- §3 → reglas que existen **solo en Q** (candidatas a portarse a QR).
- Cada ítem indica: qué implica, si aplica al otro módulo y la **recomendación**.
- Las oportunidades **reales** (portables y con sentido de negocio) están en §0 y §5; el resto son diferencias de dominio que no se trasladan.

---

## 0. Resumen ejecutivo (oportunidades priorizadas)

| # | Regla | Origen | Destino | Impacto | Esfuerzo |
|---|-------|--------|---------|---------|----------|
| 1 | Bloquear cambio de **cliente** en QR enlazada a una Q | Q (`ip.q.client-change-blocked`) | QR | **Alto** (evita Q con QR de otro cliente) | Bajo |
| 2 | Bloquear cambio de **moneda** en QR enlazada a una Q | Q (`ip.q.currency-mismatch`) | QR | **Alto** (mantiene consistencia client+currency) | Bajo |
| 3 | **Open-lock también en `DELETE` de líneas** de QR | Q (POST/PUT/DELETE exigen lock) | QR | Medio (consistencia de edición) | Bajo |
| 4 | **Historial + cierre de lock** en auto-rechazo de Q | QR (`changeStatusInternal` + historial) | Q | Medio (auditoría y lock) | Medio |
| 5 | `change-status` de QR devuelve **objeto completo** | Q (`IpQuotationResponse`) | QR | Medio (paridad de API/UI) | Bajo |
| 6 | **Gate de permiso** para sobrescribir `paymentTerms` en Q | QR (`EDIT_PAYMENT_TERMS_...`) | Q | Medio (control) | Bajo |
| 7 | Exigir **≥1 producto para `SENT`** en QR | Q (`HAS_PRODUCTS` en `CREATED→SENT`) | QR | Medio (evita QR vacías enviadas) | Bajo |
| 8 | **Gate de permiso** para `COMPLETE` manual de Q | QR (`COMPLETE_IP_QUOTE_REQUESTS`) | Q | Opcional (depende del negocio) | Bajo |
| 9 | **`IntegrityValidator`** antes de guardar QR | Q (PUT de Q) | QR | Medio | Medio |
| 10 | Criterio único para **productos duplicados** (error vs dedup) | QR: error / Q: dedup | ambos | Bajo (definir criterio) | Bajo |
| 11 | Validar estado del producto maestro **al agregar** (no solo al ANSWERED) | Q (`draft-not-allowed`) | QR | Bajo | Bajo |
| 12 | Definir destino de las **QR enlazadas** cuando la Q se rechaza | — (ninguno lo hace) | Q | Medio (hoy quedan colgadas) | Medio |

---

## 1. Contexto: los dos mundos en el flujo IP

```
QR (pedir precio al proveedor) → Q (consolidar + margen al cliente) → PO (comprar)
```

- **QR** es el mundo **upstream/proveedor**: no conoce POs. Sus dependencias son sus **Q asociadas**.
- **Q** es el mundo **downstream/cliente**: no conoce proveedores. Sus dependencias son sus **QR enlazadas** y sus **PO**.
- Ambas comparten el mismo esqueleto de ciclo de vida (`CREATED → SENT → ANSWERED → COMPLETE | REJECTED`), edición por status, open-lock y scheduler de unlock. Ese núcleo ya es simétrico (ver §4).
- La paridad se rompe en las **validaciones de dependencia** (QR ↔ Q, Q ↔ PO) y en los **detalles de control** (permisos, historial, API).

---

## 2. Reglas que tiene QR y NO tiene Q

### 2.1 Permiso para sobrescribir `paymentTerms` (QR: 4002008)

- **QR:** el `PUT` solo sobrescribe `paymentTerms` con el permiso `EDIT_PAYMENT_TERMS_IP_QUOTE_REQUESTS` (4002008); si no, usa el valor del supplier.
- **Q:** la acción `EDIT_PAYMENT_TERMS_IP_QUOTATIONS` (4003006) **existe en el enum pero no se usa**: el `PUT` permite cambiar `paymentTerms` sin gate.
- **Aplica a Q:** sí. → **Recomendación (#6):** activar el gate 4003006 en el `PUT` de Q, igual que QR. Alternativa: quitarlo también de QR si el negocio decide que no aplica. **Decidir y alinear ambos.**

### 2.2 Permiso para `COMPLETE` manual (QR: 4002004)

- **QR:** el `COMPLETE` manual solo se permite desde `ANSWERED` **y** con el permiso `COMPLETE_IP_QUOTE_REQUESTS` (4002004), que no viene en roles por defecto. El camino normal es automático (la Q lo completa).
- **Q:** el `COMPLETE` manual requiere **≥1 PO asociada** pero **no** tiene gate de permiso.
- **Aplica a Q:** parcial. → **Recomendación (#8):** si el negocio quiere restringir quién completa una Q, crear un `COMPLETE_IP_QUOTATIONS`; hoy la PO ya es una salvaguarda. **Opción de negocio, no bug.**

### 2.3 Auto-rechazo "limpio": historial + cierre de open-lock (QR 30 días)

- **QR:** el job diario 00:00 usa `changeStatusInternal`:
  - respeta QR ya terminales / ya en el estado destino;
  - escribe el timestamp correcto y **limpia timestamps futuros** en retrocesos;
  - **cierra el open-lock** al llegar a terminal;
  - escribe **historial** `AUTO_REJECTED_TIME` **atribuido al sales rep**.
- **Q:** el job 23:54 **no** pasa por el flujo de cambio de estado: escribe `status = REJECTED` + `rejectAt` directo. **No cierra el open-lock, no escribe historial, no libera/revierte QR.** (Ya está marcado como pendiente en Q §11.1.)
- **Aplica a Q:** sí. → **Recomendación (#4):** refactor del job de Q para usar un `changeStatusInternal` equivalente (mismo patrón de QR), con historial y cierre de lock.

### 2.4 Historial específico de transiciones automáticas

- **QR:** registra `AUTO_REJECTED_TIME` y `STATUS_CHANGE_BY_Q` con usuario atribuido.
- **Q:** no registra nada en su auto-rechazo (ver 2.3).
- **Aplica a Q:** sí (incluido en la recomendación #4).

### 2.5 Producto duplicado: **error** (QR) vs **deduplicación silenciosa** (Q)

- **QR:** al agregar un producto ya existente en la QR → error `ip.qr.product.exist`.
- **Q:** si el mismo `productId` llega varias veces en un request → **deduplica "primera gana"** (omite el resto, sin error).
- **Aplica:** no es "portar" sino **unificar el criterio**. → **Recomendación (#10):** decidir si ambos deben dar error o ambos deduplicar, y reflejarlo en los manuales.

### 2.6 Validación de proveedor en el flujo de estados

- **QR:** `CREATED→SENT` y `SENT→ANSWERED` exigen **supplier asignado** → `ip.qr.supplier.required.for.status.change`.
- **Q:** no tiene concepto de proveedor (los proveedores llegan por las QR enlazadas).
- **Aplica a Q:** ❌ no aplica (dominio distinto). Nota: la Q **hereda** los proveedores de sus QR para el armado de PO.

### 2.7 Rechazo en cascada: QR exige que TODAS sus Q estén `REJECTED`

- **QR:** rechazar manualmente una QR asociada a Qs exige que **todas** sus Q estén `REJECTED` → `ip.qr.assigned-to-q-rejected`.
- **Q:** rechazar exige "**sin PO**" → `ip.q.cannot-reject-with-po`. No valida nada sobre sus QR enlazadas.
- **Aplica a Q:** el patrón "validar dependencias en cascada" sí, pero invertido. La contraparte real de la Q es: **al rechazar una Q, ¿qué pasa con sus QR enlazadas?** Hoy quedan intactas (colgadas). → **Recomendación (#12):** definir el destino de las QR cuando la Q se rechaza (p.ej. dejar libres las QR que no están terminales, o revertir su `COMPLETE` automático). Es la simetría que le falta a Q.

### 2.8 Campos de flete/embarque propios de la QR

- **QR:** `shippingPointZipCode`, `freightClass`, `fobShippingPoint`, `freightCharges` como campos del documento de proveedor.
- **Q:** no tiene estos campos; solo **agrega** el `freightCharges` de las QR enlazadas.
- **Aplica a Q:** ❌ no aplica (datos de dominio proveedor). Nota: Q ya muestra el total de flete de QR con productos (§6.2 del manual Q).

---

## 3. Reglas que tiene Q y NO tiene QR

### 3.1 Cambio de **cliente** bloqueado si hay QR enlazadas

- **Q:** cambiar el `client` con QR enlazadas → `ip.q.client-change-blocked`.
- **QR:** puede cambiar el `client` aunque esté enlazada a una Q (solo **regenera el `number`**). No hay check contra `quotationsQuoteRequests`.
- **Aplica a QR:** sí. → **Recomendación (#1):** bloquear el cambio de cliente en QR cuando esté asociada a una Q (dejaría la Q con una QR de otro cliente, rompiendo la regla `findByIdAndClient`). Impacto alto, esfuerzo bajo.

### 3.2 Cambio de **moneda** validado contra dependencias

- **Q:** cambiar la `currency` con una QR enlazada de otra moneda → `ip.q.currency-mismatch`.
- **QR:** puede cambiar la `currency` aunque esté enlazada a una Q (solo valida la moneda al enlazarse, no al editarla después).
- **Aplica a QR:** sí. → **Recomendación (#2):** bloquear (o revalidar) el cambio de moneda en QR cuando esté asociada a una Q. Complementa la #1 para mantener `client+currency` consistentes entre QR y Q.

### 3.3 Open-lock también en `DELETE` de líneas

- **Q:** `POST`/`PUT`/`DELETE` de productos y other charges exigen status editable **+ open-lock**.
- **QR:** los `DELETE` de producto y other charge validan **solo status editable**, no open-lock (los `POST`/`PUT` sí).
- **Aplica a QR:** sí. → **Recomendación (#3):** exigir open-lock en los `DELETE` de líneas de QR. Ya estaba marcado como pendiente (Q §11.4) y este documento confirma la asimetría.

### 3.4 `change-status` de Q devuelve el **objeto completo**

- **Q:** `PATCH /ip/q/{id}/change-status` responde `IpQuotationResponse` (objeto completo con productos, cargos, profit/freight).
- **QR:** `PATCH /ip/qr/{id}/change-status` responde `ListIpQuoteRequestResponse` (resumen), igual que antes del fix de Q.
- **Aplica a QR:** sí. → **Recomendación (#5):** devolver también el objeto completo de QR (`IpQuoteRequestResponse` o equivalente) en `change-status`, replicando el fix hecho en Q.

### 3.5 Requisito de contenido para `SENT`

- **Q:** `CREATED→SENT` exige **≥1 QR enlazada** y **≥1 producto** (`HAS_QUOTE_REQUESTS` + `HAS_PRODUCTS`) → `ip.q.not-valid-sent`.
- **QR:** `CREATED→SENT` no exige productos (solo supplier). Una QR sin productos puede enviarse (el bloqueo por productos aparece recién en `ANSWERED`/imprimir).
- **Aplica a QR:** sí (la parte de productos). → **Recomendación (#7):** exigir **≥1 producto** para `SENT` en QR, alineando con Q.

### 3.6 Validación de integridad transversal antes de guardar

- **Q:** el `PUT` corre `IntegrityValidator.validateQuotationIntegrity` (consistencia de líneas vs QR enlazadas) antes de persistir.
- **QR:** el `PUT` no tiene un validador de integridad equivalente.
- **Aplica a QR:** sí, si existen invariantes (p.ej. que cada línea referencia a un producto de la QR, totales consistentes). → **Recomendación (#9):** evaluar un validador de integridad para QR. **Requiere definir qué invariantes.**

### 3.7 Productos maestros en `DRAFT` prohibidos al agregar

- **Q:** al agregar un producto de QR a la Q, el producto maestro no puede estar en `DRAFT` → `ip.q.product.draft-not-allowed`.
- **QR:** valida el estado del producto **al pasar a `ANSWERED`** (todos `ACTIVE`) → `ip.qr.products-not-active`, no al agregar la línea.
- **Aplica a QR:** parcial. → **Recomendación (#11):** adelantar el control: validar el estado del producto maestro al **agregar** la línea en QR (feedback temprano), manteniendo la validación `ACTIVE` en `ANSWERED`.

### 3.8 Otros de Q no portables a QR

| Regla Q | Por qué no aplica a QR |
|---------|------------------------|
| Dependencias de **PO** (`complete-requires-po`, `cannot-reject-with-po`, `cannot-revert-with-po`) | QR es upstream y nunca toca POs. (El patrón "requisito de dependencia para avanzar" ya lo tiene QR con sus Q: §2.7.) |
| **Import de other charges** desde QR | QR es el origen; no importa nada. |
| `applicationAt` requerida antes de `SENT`/`ANSWERED` | QR usa `leadTime`/`leadTimeType`, no fecha de aplicación. |
| **Inmutabilidad de QR enlazadas** fuera de `CREATED` | Es la misma regla vista desde Q; la QR no controla su vínculo (lo controla la Q). |

---

## 4. Reglas que YA son simétricas (núcleo común, no tocar)

| Regla | QR | Q |
|-------|----|---|
| Estados terminales `COMPLETE`/`REJECTED` **inmutables** | ✅ | ✅ |
| Mismo estado → error | ✅ `ip.qr.equal-status` | ✅ `ip.q.equal-status` |
| Edición bloqueada en estado terminal | ✅ `ip.qr.not-editable-by-status` | ✅ `ip.q.not-editable-by-status` |
| Open-lock obligatorio para editar + límite de pestañas | ✅ | ✅ |
| Unlock **nocturno** (scheduler) | ✅ 23:50:30 | ✅ 23:53 |
| Cierre de open-lock al llegar a estado terminal | ✅ | ✅ |
| `change-status` **sin** exigir open-lock | ✅ (verificado en código) | ✅ (intencional) |
| Rollback limpia timestamps futuros | ✅ | ✅ |
| Enlace/desvinculación de QR solo desde `CREATED` | ✅ (controlado por Q) | ✅ `ip.q.qr.cannot-add/delete` |
| PDF reusado en estados terminales | ✅ | ✅ |
| Clonar exige original abierto (si está en status abierto) | ✅ | ✅ |
| Cambio de cliente regenera `number` | ✅ | ✅ |

---

## 5. Plan de acción recomendado (ordenado por valor/esfuerzo)

### Fase 1 — Quick wins (paridad de control, bajo riesgo)
1. **Bloquear cambio de cliente en QR enlazada** a una Q (#1).
2. **Bloquear cambio de moneda en QR enlazada** a una Q (#2).
3. **Open-lock en `DELETE` de líneas de QR** (#3).
4. **`change-status` de QR devuelve objeto completo** (#5).
5. **≥1 producto para `SENT` en QR** (#7).

### Fase 2 — Control y auditoría (medio)
6. **Refactor del auto-rechazo de Q** (45 días) con `changeStatusInternal` + historial + cierre de lock (#4) — resuelve Q §11.1.
7. **Gate de permiso para `paymentTerms` en Q** (4003006) o retirarlo de QR: alinear (#6).
8. **Criterio único de productos duplicados** error vs dedup (#10) — decisión de negocio + alinear manuales.

### Fase 3 — Estratégico / decisión de negocio
9. **Destino de las QR enlazadas al rechazar una Q** (#12) — cerrar la simetría de cascada.
10. **Permiso `COMPLETE_IP_QUOTATIONS`** para el `COMPLETE` manual de Q (#8) — solo si el negocio lo pide.
11. **`IntegrityValidator` para QR** (#9) — definir invariantes primero.
12. **Adelantar validación `DRAFT`/estado del producto al agregar en QR** (#11).

> Después de aplicar un ítem, actualizar `docs/reglas-negocio-qr.md` y/o `docs/reglas-negocio-q.md` y moverlo de §2/§3 a §4 (simétrico).

---

## 6. Notas y correcciones detectadas durante la comparativa

- **Corrección al manual QR §5:** la fila "`change-status` / imprimir → ✅ (solo si status abierto)" es imprecisa. Verificado en código: `change-status` de QR **no** exige open-lock (igual que Q). El open-lock aplica a **imprimir/clonar** y a la **edición** de campos y líneas. Actualizar el manual QR.
- La Q mantiene el **historial** `STATUS_CHANGE_BY_Q` de las QR al responder (§7 de ambos manuales): es la única escritura de historial automático del lado Q; el auto-rechazo de Q no lo replica (ver §2.3).
- Ambas tablas de transiciones (QR §4.1 y Q §4.1) usan las mismas 5 estados y el mismo mecanismo de timestamps; las diferencias están **solo** en los requisitos de cada transición (supplier vs QR/productos/PO) y en los gates de permiso.

---

## 7. Referencias

- Manual QR: `docs/reglas-negocio-qr.md`
- Manual Q: `docs/reglas-negocio-q.md`
- Código QR: `api/ip/qr/service/impl/IpQuoteRequestServiceImpl.java`, `IpQuoteRequestProductServiceImpl.java`, `IpQuoteRequestOtherChargeServiceImpl.java`
- Código Q: `api/ip/q/service/impl/IpQuotationServiceImpl.java`, `IpQuotationProductServiceImpl.java`, `IpQuotationOtherChargeServiceImpl.java`, `IpQuotationOtherChargesQuoteRequestServiceImpl.java`