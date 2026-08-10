# Hallazgos de deuda técnica — módulos transaccionales (Q, QR, PO, Products, INV)

Documento de auditoría. Cada hallazgo cita archivo:línea y código verbatim. No se aplican
correcciones aquí — es material de referencia para decidir qué y cuándo abordar.

Convención de rutas: relativas a `src/main/java/com/itradingsolutions/itex/api/`.

---

## Métricas base

| Archivo | Líneas | Dependencias `private final` inyectadas |
|---|---|---|
| `ip/q/service/impl/IpQuotationServiceImpl.java` | 836 | 16 |
| `ip/po/service/impl/IpPurchaseOrderServiceImpl.java` | ~750 | ~15 |
| `ip/qr/service/impl/IpQuoteRequestServiceImpl.java` | ~470 | ~12 |
| `ip/products/services/impl/IpProductServiceImpl.java` | 341 | 6 |
| `ip/q/service/impl/IpQuotationHistoryServiceImpl.java` | 258 | 2 |
| `ip/q/service/impl/IpQuotationProductServiceImpl.java` | 173 | 4 |
| `ip/q/service/impl/IpQuotationOtherChargesQuoteRequestServiceImpl.java` | 143 | 6 |
| `ip/products/services/impl/IpProductHistoryServiceImpl.java` | 129 | 2 |
| `ip/q/service/impl/IpQuotationOtherChargeServiceImpl.java` | 94 | 4 |
| `ip/q/controller/IpQuotationController.java` | 314 | 4 |
| `ip/products/controllers/IpProductController.java` | 319 | 4 |

---

## 1. N+1 / carga LAZY en listados y loops

**`ip/q/service/impl/IpQuotationServiceImpl.java:184-185`**
```java
Page<IpQuotationEntity> resp = quotationRepository.findAll(spec, pageable);
return new PageImpl<>(resp.getContent().stream().map(quotationMapper::entityToDTO).toList(), resp.getPageable(), resp.getTotalElements());
```
`entityToDTO` mapea `client`, `salesRep`, `quoteRequestsQuotations`, `products`, `qrOtherCharges` —
todas `FetchType.LAZY` (`IpQuotationEntity.java:61,65,72,129,139`). La specification no hace fetch join.

**`ip/q/models/mapper/IpQuotationMapper.java:71-79`** — `quotationProducts` es
`@OneToMany(fetch = LAZY)`; una inicialización de colección por cada QQR de cada quotation.

**`ip/q/models/mapper/IpQuotationMapper.java:122-131`** — `qqr.getQuoteRequest()` y
`qr.getSupplier()` son LAZY, resueltos por fila de producto.

**`ip/products/services/impl/IpProductServiceImpl.java:219-220`** — mismo patrón: mapper toca
`brand`, `coo`, `substituteProduct`, `surplus`, `openBy`, todas LAZY.

**`ip/q/service/impl/IpQuotationServiceImpl.java:684-688`** — escritura dentro de `forEach`:
```java
openQuotations.forEach(quotation -> {
    quotation.setOpenBy(null);
    quotation.setOpenAt(null);
    quotationRepository.save(quotation);
});
```
Mismo patrón en `:705-709` (reject masivo) y `:583-591`/`:461-478` (`qrService.findByIdAndClient`
dentro de loop).

**`ip/q/service/impl/IpQuotationProductServiceImpl.java:95-99`** — dos `findById` por iteración
dentro de `for (var dto : deduplicated)`.

**`ip/q/service/impl/IpQuotationOtherChargesQuoteRequestServiceImpl.java:74-75`** — repositorio
dentro de `for (var item : items)`.

**`ip/products/schedulers/IpProductScheduler.java:17-18`** — service call por elemento
(`findById` + `save` cada uno):
```java
var listProducts = productService.listAllOpenIpProducts();
listProducts.forEach(product -> productService.unlockIpProduct(product.getId()));
```

**QR — `ip/qr/service/impl/IpQuoteRequestServiceImpl.java:137-148`** — `clone` hace N `save()`
sueltos por hijo (productos, other charges) en vez de batch/cascade.

**PO — sin este problema en clone**: ya usa `cloneChildren` genérico con cascade
(`ip/po/service/impl/IpPurchaseOrderServiceImpl.java:741`).

---

## 2. Filtrado en memoria de tabla completa

**`ip/q/service/impl/IpQuotationServiceImpl.java:700-703`**
```java
var oldQuotations = quotationRepository.findAll().stream()
        .filter(q -> q.getStatus() == IpQuotationStatus.CREATED)
        .filter(q -> q.getCreatedAt().isBefore(cutoffDate))
        .toList();
```
Carga `t_ip_quotations` completa en memoria, desde el scheduler (`IpQuotationScheduler.java:37-41`).

**`ip/po/service/impl/IpPurchaseOrderServiceImpl.java:267`** — mismo patrón:
```java
var oldPurchaseOrders = repository.findAll().stream()
        .filter(po -> po.getStatus() == IpPurchaseOrderStatus.CREATED)
        .filter(po -> po.getCreatedAt().isBefore(cutoffDate))
        .toList();
```

---

## 3. Queries repetidas / redundantes

**`ip/products/services/impl/IpProductServiceImpl.java:207-211`** — `findById` dos veces en el
mismo flujo (`product` y `newProductId`).

**`ip/products/controllers/IpProductController.java:207-208`** — controller vuelve a cargar el
producto después de que el service ya cargó ambos:
```java
var product = productService.replaceProduct(newProductId.getProductId(), newProductId.getNewProductId());
var newProduct = productService.findIpProductById(newProductId.getNewProductId());
```

**`ip/q/controller/IpQuotationController.java:233-234`** — `getEntityById` + `cloneQuotation`
(que internamente vuelve a hacer `findById`).

**`ip/products/controllers/IpProductController.java:95-97, 224-226, 242-244`** — patrón repetido
`find...ById` seguido de `update...ById` que vuelve a resolver el mismo id.

**`ip/qr/service/impl/IpQuoteRequestServiceImpl.java:128, 133`** — `user` resuelto en `clone`, y
`saveBaseInfo` vuelve a llamar `getUserAuthenticated()` en la misma operación.

**`ip/products/services/impl/IpProductServiceImpl.java` (líneas 124-126, 149-150, 186-187,
196-197, 208-209)** — `findById` + mutar 1-2 campos + `save`, donde un solo `@Modifying UPDATE`
bastaría (mismo patrón que QR/PO en el open/lock).

**`getUserAuthenticated()` resuelto más de una vez por flujo**: `IpProductServiceImpl` lo llama
en el método (líneas 60,77,86,126,150,187,197,209) y `HistoryServiceImpl` lo vuelve a llamar en el
`addHistory` subsecuente (`common/util/services/impl/HistoryServiceImpl.java:38,74`).

---

## 4. Problemas de límite transaccional

**`ip/products/controllers/IpProductController.java:154-158`** — loop de escritura en un
controller (sin `@Transactional`); cada `unlockIpProduct` es su propia transacción, fallo parcial
deja desbloqueos parciales:
```java
listProductIds.forEach(item -> {
    if (item != null) {
        productService.unlockIpProduct(item);
    }
});
```

**`ip/q/controller/IpQuotationController.java:111-114`** — mismo patrón en `closeListIpQuotations`.

**`ip/qr/controllers/IpQuoteRequestController.java:169-183`** (`closeListIpQuoteRequests`) — carga
entidades completas, mapea a DTO (se descarta), y desbloquea una por una: 2N+1 queries en N
transacciones separadas, no atómico.

**`ip/products/controllers/IpProductController.java:292-307`** — loop de escritura +
`addHistory` por item en el controller, sin transacción envolvente.

**`ip/q/controller/IpQuotationProductController.java:55-59`** y
**`IpQuotationOtherChargeController.java:123-129`** — creación y escritura de historial en
transacciones separadas (create ya comiteó cuando se escribe el historial).

**`ip/q/service/impl/IpQuotationServiceImpl.java:668-673`** — método público de interfaz sin
`@Transactional`, invocado desde otro service:
```java
public void validateQuotationInCreatedStatus(IpQuotationEntity entity, UserEntity user) {
    if (entity.getStatus() != IpQuotationStatus.CREATED) {
```

**`ip/q/service/impl/IpQuotationServiceImpl.java:746-748`** — `printQuotation` anotado
`@Transactional` (lectura-escritura) pero mantiene la transacción abierta durante el render de
Jasper e I/O de archivo.

---

## 5. Race conditions

**Bloqueo open/lock — 3 copias con el mismo defecto**, lectura-luego-escritura sin `@Version` ni
lock pesimista:

`ip/q/service/impl/IpQuotationServiceImpl.java:222-235`:
```java
var quotation = findById(id);
if (quotation.getOpenBy() == null) {
    var user = userService.getUserAuthenticated();
    validateMaxOpenQuotations(user.getId());
    if (type.equals(OpenAndLockType.EDIT)) {
        quotation.setOpenBy(user);
        quotation.setOpenAt(ZonedDateTime.now(zoneId));
        quotation = quotationRepository.save(quotation);
    }
}
```
Gemelos: `ip/products/services/impl/IpProductServiceImpl.java:84-94`,
`ip/qr/service/impl/IpQuoteRequestServiceImpl.java:159-175`,
`ip/po/service/impl/IpPurchaseOrderServiceImpl.java:427-439`.
Ninguna entidad (`IpQuotationEntity`, `IpProductEntity`, `IpQuoteRequestEntity`,
`IpPurchaseOrderEntity`) declara `@Version`.

**Generación de consecutivo antes de guardar** — dos operaciones concurrentes pueden generar el
mismo número antes de que cualquiera de las dos persista:

`ip/q/service/impl/IpQuotationServiceImpl.java:206, 210-211`:
```java
entity.setNumber(consecutiveService.generateConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, client.getCode()));
...
var resp = quotationRepository.save(entity);
consecutiveService.saveConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, resp.getNumber());
```
Mismo patrón en `:498/550-551` (clone) y `:316-321` (update), y en QR/PO (`generateConsecutive`
seguido de `save` en operación separada).

Nota: `SalesConsecutiveServiceImpl` (usado por INV) **no tiene este problema** — usa pessimistic
lock (`findByTypeForUpdate`) sobre la fila del contador, serializando generate/release. Es el
mecanismo correcto; el de `IConsecutiveService` (usado por Q/QR/PO) no lo tiene.

**`ip/q/service/impl/IpQuotationProductServiceImpl.java:74-75, 86-92`** — verificación de
duplicados leída de la BD antes del insert, sin constraint única visible que la respalde en ese
punto del código.

**`ip/q/models/entities/IpQuotationsQuoteRequestEntity.java:65-71`** — número de línea calculado
como máximo en memoria, sin lock ni constraint de BD:
```java
public int getMaxNumberOfProducts() {
    if (quotationProducts == null || quotationProducts.isEmpty()) return 1;
    return quotationProducts.stream()
            .mapToInt(IpQuotationProductEntity::getNumber)
            .max()
            .orElse(0) + 1;
}
```

---

## 6. Lógica de negocio en controllers

**`ip/q/controller/IpQuotationController.java:177-178`**:
```java
if (status.equals(IpQuotationStatus.REJECTED))
    throw new IllegalArgumentException("Cannot change status to REJECTED");
```
(Nota: `IllegalArgumentException` cruda, no una excepción de negocio del proyecto — el
`ControllerAdvice` la trata genérica, sin mensaje i18n.)

**`ip/qr/controllers/IpQuoteRequestController.java:218-219`** — mismo patrón exacto, misma excepción cruda.

**`ip/products/controllers/IpProductController.java:292-307`** — orquestación completa de
importación (filtrado, anulación de campos, persistencia, historial) dentro del controller.

**`ip/q/controller/IpQuotationOtherChargeController.java:124-129, 160-163`** — construcción de
DTO y orquestación de historial en el controller.

**`ip/q/controller/IpQuotationProductController.java:119-130`** — ensamblado de DTO con
condicionales en el controller (`buildDTO`).

**`ip/q/controller/IpQuotationController.java:233-236`** — reconstrucción del estado "old" para
historial hecha en el controller.

---

## 7. God classes

`IpQuotationServiceImpl` (836 líneas, 16 dependencias) es el peor caso — mezcla CRUD, cambio de
estado, clonación, bloqueo, generación de PDF y validaciones de PO/QR cruzadas. `IpPurchaseOrderServiceImpl`
y `IpQuoteRequestServiceImpl` están en la misma categoría a menor escala. `IpProductServiceImpl` es
comparativamente más sano (341 líneas, 6 dependencias).

---

## 8. Paginación — doble envoltura de `PageImpl`

Patrón repetido **4 veces** (Products, Q, QR, PO): el service envuelve en `PageImpl` y el
controller vuelve a envolver:

`ip/products/services/impl/IpProductServiceImpl.java:220` + `ip/products/controllers/IpProductController.java:265`
`ip/q/service/impl/IpQuotationServiceImpl.java:185` + `ip/q/controller/IpQuotationController.java:136`
`ip/qr/service/impl/IpQuoteRequestServiceImpl.java:210` + `ip/qr/controllers/IpQuoteRequestController.java:196`
`ip/po/service/impl/IpPurchaseOrderServiceImpl.java:459` + `ip/po/controller/IpPurchaseOrderController.java:300`

---

## 9. Seguridad de ordenamiento y filtros

**`common/util/models/filter/BaseFilter.java:24-34`** — `Sort.by(getShortBy())` toma el string del
cliente sin whitelist: una propiedad inválida produce `PropertyReferenceException` (500), y
permite ordenar por paths anidados arbitrarios. Afecta a los 4 módulos (Q, QR, PO, Products) porque
todos extienden `BaseFilter`.

**`ip/products/models/filter/FilterListIpProducts.java:53-56`** — `LIKE` sin normalizar mayúsculas
en la columna pero sí en el parámetro (case-mismatch), y sin escapar `%`/`_` de input del cliente:
```java
criteriaBuilder.like(root.get("description"), "%" + getDescription().toUpperCase() + "%");
```

**`ip/q/models/filters/FilterListIpQuotation.java:66-69`** — mismo problema de falta de escape en
`remarks`.

**`ip/qr/models/filters/FilterListIpQuoteRequest.java:108, 126, 138`** — `assert query != null`
usado como si fuera una validación de runtime; los asserts están deshabilitados por defecto (sin
`-ea`), por lo que es código muerto.

---

## 10. Manejo de BigDecimal / dinero

**`ip/q/models/dto/IpQuotationProductDTO.java:30-41`** — ramas tempranas devuelven el valor sin
`setScale`, mientras la rama con margen de ganancia sí aplica `setScale(5, HALF_UP)` — el mismo
getter produce valores en dos escalas distintas según si `profitMargin` es cero:
```java
if (profitMargin == null || BigDecimal.ZERO.compareTo(profitMargin) == 0)
    return quoteRequestProduct.getUnitPrice();
return quoteRequestProduct.getUnitPrice()
        .multiply(BigDecimal.ONE.add(profitMargin))
        .setScale(5, RoundingMode.HALF_UP);
```

**`ip/q/models/dto/IpQuotationDTO.java:72-82`** — `getTotalOtherCharges()` no aplica `setScale`
(a diferencia de `getSubTotal`/`getFreightCharges`/`getTotal`), y no filtra nulos con
`.filter(Objects::nonNull)` como sí hacen líneas cercanas → riesgo de NPE en `BigDecimal::add` si
`getValue()` es null.

**`ip/products/models/dto/IpProductDTO.java:147-154`** — `getTotalSurplus()` suma cantidades sin
`setScale`.

**`ip/q/models/dto/reports/IpQuotationProductReportDTO.java:56-59`** — formatos de presentación
inconsistentes: `#,##0.00000` para precio unitario, `#,##0.00` para precio extendido.

No se encontró uso de `double`/`float` para dinero, ni `BigDecimal.equals()`, ni `divide()` sin
escala en ninguno de los 5 módulos.

---

## 11. Null-safety

**`ip/q/models/dto/IpQuotationDTO.java:77-80`** — encadenado sin guarda de null sobre una relación
conocida como nullable (el propio service la null-checkea en otro punto):
```java
var qrTotal = Optional.ofNullable(qrOtherCharges)
        .orElseGet(Collections::emptyList).stream()
        .map(oc -> oc.getQrOtherCharge().getValue())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
```

**`ip/q/controller/IpQuotationOtherChargeController.java:126-127, 161-162`** — mismo encadenado
sin guarda, en el controller.

**`ip/q/service/impl/IpQuotationServiceImpl.java:701-702`** — `getCreatedAt()` desreferenciado sin
chequeo en un full-table scan.

**`ip/q/service/impl/IpQuotationServiceImpl.java:296, 318, 824-828, 273`** — varios encadenados de
getters sobre relaciones LAZY/posiblemente null sin guarda.

**`ip/q/service/impl/IpQuotationProductServiceImpl.java:44-45`** — `getQuoteRequestProduct()`
desreferenciado sin chequeo, aunque la misma clase lo trata como nullable en otro punto (línea 131).

**`ip/q/models/entities/IpQuotationsQuoteRequestEntity.java:68`** — unboxing de `Integer`
potencialmente null.

**`ip/q/service/impl/IpQuotationHistoryServiceImpl.java:117-118, 174-175, 199`** — `.name()` sobre
enums potencialmente null.

**`ip/products/services/impl/IpProductServiceImpl.java:131`** — `request.getQuantity()`
desreferenciado sin chequeo.

No se encontró `Optional.get()` sin chequeo previo en ninguno de los 5 módulos.

---

## 12. Código muerto / incorrecto

**`ip/q/service/impl/IpQuotationServiceImpl.java:94, 98`** — dos dependencias inyectadas
(`clientContactRepository`, `otherChargeRepository`) nunca referenciadas en el resto del archivo.

**`ip/q/service/impl/IpQuotationServiceImpl.java:400-410, 412-429`** — dos métodos con TODO +
bloque comentado; las validaciones de dependencia con PO son no-ops:
```java
private void validatePurchaseOrderChangeStatus(IpQuotationEntity quotation) {
    //TODO, validamos que el no tenga ninguna PO asignada
    /* ... */
}
```

**`ip/q/service/impl/IpQuotationServiceImpl.java:668-673`** — parámetro `user` nunca usado en el
cuerpo del método.

**`ip/q/service/impl/IpQuotationServiceImpl.java:658-665`** — el mismo valor (`entity.getNumber()`)
se pasa a dos campos distintos del response.

**`ip/products/services/impl/IpProductServiceImpl.java:280-287`** — el PK de la entidad de surplus
se fija al id del producto (no uno propio); dos escrituras separadas (`addSurplusIpProduct`,
`outSurplusIpProduct`) dependen de este detalle implícito.

**`ip/q/service/impl/IpQuotationServiceImpl.java:601, 669, 830`** — tipo totalmente calificado
usado inline existiendo ya el import equivalente.

**`ip/q/models/dto/reports/IpQuotationReportDTO.java:104,107,110,113`** — logging info/debug por
fila dejado en el hot path de generación de reportes.

**`ip/products/models/entity/IpProductEntity.java:91-93, 78-80`** — `substituteProduct` y `coo`
declarados `optional = false` (no nulables) aunque la mayoría de productos no tienen sustituto y
el propio service setea `coo` a `null` explícitamente (`IpProductServiceImpl.java:320`).

**`ip/products/models/entity/IpProductEntity.java:101-103`** — `openBy` es la única relación de la
entidad sin `fetch = FetchType.LAZY` (EAGER por defecto), a diferencia de `IpQuotationEntity`.

**`ip/products/services/impl/IpProductServiceImpl.java:252,254,261,269`** — strings de error en
inglés hardcodeados en vez de `simpleMessage(...)` (mecanismo i18n usado en el resto de la clase).

**`ip/products/services/impl/IpProductHistoryServiceImpl.java:83,92,117,121`** — comentarios en
español y un emoji (`// 🔹 Campos simples`) en un codebase por lo demás en inglés.

**`CommonController.java:25`**:
```java
protected final WebSocketHandlerItex socketHandler = new WebSocketHandlerItex(jwtService);
```
`jwtService` se inyecta por campo (`@Autowired`), lo cual ocurre **después** de que corren los
inicializadores de campo — este `socketHandler` siempre se construye con `jwtService = null`.
Confirmado con `grep`: el único consumidor real de `CommonController.socketHandler` es
`CommonMasterController.java:130` (módulo de maestros). Ningún controller de Q/QR/PO/Products/INV
lo usa hoy. Bajo impacto actual, pero si se usara fallaría en tiempo de ejecución
(`NullPointerException` dentro de `WebSocketHandlerItex`, o comportamiento no autenticado si el
constructor tolera `null`).

---

## 13. Métodos de más de 40 líneas

| Ubicación | Método | Líneas |
|---|---|---|
| `ip/q/service/impl/IpQuotationServiceImpl.java:239` | `updateQuotation` | 90 |
| `ip/q/service/impl/IpQuotationProductServiceImpl.java:37` | `createIpQuotationProducts` | 88 |
| `ip/q/service/impl/IpQuotationServiceImpl.java:486` | `cloneQuotation` | 75 |
| `ip/q/service/impl/IpQuotationOtherChargesQuoteRequestServiceImpl.java:42` | `bulkImport` | 48 |
| `ip/products/services/impl/IpProductServiceImpl.java:238` | `validateImportProducts` | 41 |
| `ip/products/services/impl/IpProductHistoryServiceImpl.java:89` | `getValidateChanges` | 40 |

---

## 14. Duplicación entre módulos

**A. Bloque open/lock check-then-act — 4 copias** (Q, QR, PO, Products) — ver §5.

**B. Unlock (`findById` + anular 2 campos + `save`) — 4 copias** (Q, QR, PO, Products).

**C. `listAllOpen…` (by-username + all) — 4 copias** (Q, QR, PO, Products).

**D. `listAll…` spec + `PageImpl` — 4 copias** (Q, QR, PO, Products) — ver §8.

**E. Controller `PageImpl` re-wrap — 4 copias** (Q, QR, PO, Products) — ver §8.

**F. Quota de tabs abiertos (`countByOpenUserId >= maxTabsOpen`) — 4 copias** (Q, QR, PO, Products).

**G. `validateOpen…` (dueño del lock) — 4 copias** (Q, QR, PO, Products).

**H. `findById` + `orElseThrow` helper privado — 4+ copias**, una por módulo.

**I. Trío de queries `openBy` en el repositorio (`countByOpenUserId`, `fetchAllOpenByUsername`,
`fetchAllOpen`) — 4 copias verbatim** en `IIpProductRepository`, `IpQuotationRepository`,
`IIpQuoteRequestRepository`, `IIpPurchaseOrderRepository`.

**J. Desbloqueo masivo programado — 3 implementaciones divergentes**: loop con `save` por fila
(Q), llamada a service por elemento desde el scheduler (Products), batch real (PO). Solo PO lo
resolvió bien.

**K. Loop de cierre masivo en controller — 3 copias** (Q, QR, Products) — ver §4/§6.

**L. Scaffolding de Specification (`Specification.where(null)`, `hasStatus()`, etc.) — 4 copias**
casi idénticas en cada `FilterList*`.

**M. Rama "guardar historial solo si cambió" — al menos 2 copias** (`IpQuotationHistoryServiceImpl`,
`IpProductHistoryServiceImpl`), con la misma condición repetida.

**N. `getHistoryById` — copias idénticas** en Q y Products (`repository.fetchByXId` + map a DTO).

---

## Resumen — qué módulo tiene qué

| Defecto | Q | QR | PO | Products | INV (nuevo) |
|---|---|---|---|---|---|
| N+1 en listado paginado | Sí | Sí | Sí | Sí | Corregido (`@EntityGraph`) |
| Race condition en open/lock | Sí | Sí | Sí | Sí | Corregido (UPDATE condicional) |
| Loop de escritura en controller | Sí | Sí | No | Sí | Corregido (bulk en service) |
| Doble `PageImpl` | Sí | Sí | Sí | Sí | Corregido (`Page.map`) |
| Consecutivo sin lock atómico | Sí | Sí | Sí | N/A | N/A (`SalesConsecutiveServiceImpl` ya usa lock pesimista) |
| God service (>15 deps) | Sí (16) | Parcial | Parcial | No | Evitado (servicios separados por responsabilidad) |
| Sort sin whitelist | Sí (heredado) | Sí (heredado) | Sí (heredado) | Sí (heredado) | Corregido (whitelist propia) |
| Alcance por vendedor (`sales_rep_id`) | No aplica | No aplica | No aplica | No aplica | Nuevo, exigido por la guía de negocio |
