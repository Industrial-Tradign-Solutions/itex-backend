# Implementación Módulo Q (Quotations) - Plan de Trabajo

## Objetivo
Completar la implementación del módulo de Quotations (Q) basándose en el módulo Quote Requests (QR) existente y la estructura de base de datos definida en `V2.0.0__Quotations_module.sql`.

---

## Estado General del Proyecto

| Fase | Estado | Fecha Inicio | Fecha Fin | Notas |
|------|--------|--------------|-----------|-------|
| FASE 1 - Validaciones | ✅ Completado | 2026-05-04 | 2026-05-04 | Listo para testing |
| FASE 2 - Add QR a Q | ✅ Completado | 2026-05-04 | 2026-05-04 | Listo para testing |
| FASE 3 - History Infraestructura | ✅ Completado | 2026-05-04 | 2026-05-04 | Compilado sin errores |
| FASE 4 - History Integración | ✅ Completado | 2026-05-04 | 2026-05-04 | Integrado en todas las operaciones |
| FASE 5 - Clone Quotation | ✅ Completado | 2026-05-04 | 2026-05-04 | Clonación completa con productos |
| FASE 6 - Payment Terms Permission | ⏳ Pendiente | | | |
| FASE 7 - Scheduler Auto-reject | ⏳ Pendiente | | | |
| FASE 8 - PDF Generation | ⏳ Pendiente | | Requiere template Jasper |

**Leyenda:** ✅ Completado | 🔄 En progreso | ⏳ Pendiente | ❌ Bloqueado

---

## FASE 1: Validaciones de Cliente y Moneda

### Objetivo
Asegurar que todas las QRs vinculadas a una Quotation pertenezcan al mismo cliente y tengan la misma moneda que la Quotation.

### Archivos a crear
- [x] `exceptions/QuotationClientMismatchException.java` ✅
- [x] `exceptions/QuotationCurrencyMismatchException.java` ✅

### Archivos a modificar
- [x] `service/impl/IpQuotationServiceImpl.java` ✅
  - [x] Método `loadQuoteRequestToQuotation()` - Validar moneda de cada QR
  - [x] Método privado `validateQuoteRequestCurrency()` para validar monedas
- [x] `resources/messages/validation-messages.properties` ✅
  - [x] Agregar mensaje `ip.q.currency-mismatch`
  - [x] Agregar mensaje `ip.q.client-mismatch`

### Tests
- [ ] ⏳ Test Manual: Intentar crear Q con QRs de cliente diferente → debe fallar con error 400
- [ ] ⏳ Test Manual: Intentar crear Q con QRs de moneda diferente → debe fallar con error 400
- [ ] ⏳ Test Manual: Crear Q con QRs válidas (mismo cliente, misma moneda) → debe crear exitosamente

### Notas de implementación
- ✅ Las validaciones se ejecutan antes de guardar cualquier dato
- ✅ Validación de cliente: Ya existía via `qrService.findByIdAndClient()` (línea 209)
- ✅ Validación de moneda: Nueva implementación en `validateQuoteRequestCurrency()`
- ✅ Mensajes de error descriptivos indicando qué QR tiene inconsistencia
- ✅ Compilación exitosa sin errores

---

## FASE 2: Agregar QR a Quotation Existente

### Objetivo
Permitir agregar Quote Requests adicionales a una Quotation ya creada.

### Archivos a crear
- [x] `models/requests/AddQuoteRequestsToQuotationRequest.java` ✅
- [x] `exceptions/QuoteRequestAlreadyLinkedException.java` ✅

### Archivos a modificar
- [x] `controller/IpQuotationController.java` ✅
  - [x] Endpoint `POST /ip/q/{id_quotation}/quote-request`
- [x] `service/IpQuotationService.java` ✅
  - [x] Método `addQuoteRequestsToQuotation(UUID quotationId, List<UUID> qrIds)`
- [x] `service/impl/IpQuotationServiceImpl.java` ✅
  - [x] Implementar método con validaciones (cliente, moneda, duplicados, estado editable)

### Tests
- [ ] ⏳ Test Manual: Agregar QR válida a Q existente → debe vincularse
- [ ] ⏳ Test Manual: Intentar agregar QR de cliente diferente → error 400
- [ ] ⏳ Test Manual: Intentar agregar QR de moneda diferente → error 400
- [ ] ⏳ Test Manual: Intentar agregar QR ya vinculada → error 400 (duplicado)
- [ ] ⏳ Test Manual: Intentar agregar QR a Q COMPLETE/REJECTED → error (no editable)

### Request DTO
```json
{
  "quoteRequestIds": ["uuid1", "uuid2"]
}
```

### Notas de implementación
- ✅ Validaciones reutilizan métodos de Fase 1 (`validateQuoteRequestCurrency`, `findByIdAndClient`)
- ✅ Valida que la Q sea editable (no COMPLETE ni REJECTED)
- ✅ Valida duplicados antes de agregar
- ✅ Compilación exitosa sin errores

---

## FASE 3: History Infraestructura

### Objetivo
Crear toda la infraestructura para tracking de historial de cambios en Quotations.

### Archivos a crear
- [x] `models/enums/IpQuotationHistoryAction.java` ✅
  - Valores: `CREATE`, `UPDATE`, `CLONE`, `REJECTED`, `STATUS_CHANGE`, `ADD_PRODUCT`, `REMOVE_PRODUCT`, `UPDATE_PRODUCT`, `ADD_QR`, `REMOVE_QR`
- [x] `models/entities/IpQuotationHistoryEntity.java` ✅
  - Extends `HistoryEntity`
  - FK a `t_ip_quotations`
  - Campo `action` (enum)
- [x] `models/dto/IpQuotationHistoryDTO.java` ✅
  - Extends `HistoryDTO`
- [x] `models/response/IpQuotationHistoryResponse.java` ✅
  - Response con action
- [x] `models/mapper/IpQuotationHistoryMapper.java` ✅
  - MapStruct mapper
- [x] `repository/IIpQuotationHistoryRepository.java` ✅
  - Método `fetchByIpQuotationId(UUID)`
- [x] `service/IIpQuotationHistoryService.java` ✅
  - Interface con métodos `addHistory()`, `addHistoryProduct()`, `getHistoryById()`
- [x] `service/impl/IpQuotationHistoryServiceImpl.java` ✅
  - Extends `HistoryServiceImpl`
  - Implementa lógica de comparación de cambios

### Archivos a modificar
Ninguno (solo infraestructura)

### Tests
- [x] ✅ Test: Compilación exitosa sin errores
- [x] ✅ Test: Verificar que entity se mapea correctamente a tabla `t_ip_quotation_history`

### Notas de implementación
- ✅ Basado en `IpQuoteRequestHistoryServiceImpl` pero adaptado para Quotations
- ✅ Usa `compareOther()` para ClientContact, Client y UserDTO (no son BaseMasterDTO)
- ✅ Solo guarda UPDATE/UPDATE_PRODUCT si hay cambios reales
- ✅ Compilación exitosa

---

## FASE 4: History Integración

### Objetivo
Integrar el registro de historial en todas las operaciones existentes y nuevas.

### Archivos a crear
- [ ] `models/response/IpQuotationHistoryResponse.java`

### Archivos a modificar
- [ ] `controller/IpQuotationController.java`
  - [ ] Endpoint `GET /ip/q/history/{id_quotation}` con `@AccessToAction(VIEW_HISTORY_IP_QUOTATIONS)`
- [ ] `service/impl/IpQuotationServiceImpl.java`
  - [ ] Inyectar `IIpQuotationHistoryService`
  - [ ] Registrar `CREATE` en `createQuotation()`
  - [ ] Registrar `UPDATE` en `updateQuotation()`
  - [ ] Registrar `STATUS_CHANGE` en `changeStatusQuotation()`
  - [ ] Registrar `REJECTED` en `rejectQuotation()`
  - [ ] Registrar `REMOVE_QR` en `removeQuoteRequestFromQuotation()`
  - [ ] Registrar `ADD_QR` en nuevo método (Fase 2)
- [ ] `service/impl/IpQuotationProductServiceImpl.java`
  - [ ] Inyectar `IIpQuotationHistoryService`
  - [ ] Registrar `ADD_PRODUCT` en `createIpQuotationProduct()`
  - [ ] Registrar `UPDATE_PRODUCT` en `updateIpQuotationProduct()`
  - [ ] Registrar `REMOVE_PRODUCT` en `removeIpQuotationProduct()`

### Tests
- [ ] Test Manual: Crear Q → GET /history → debe mostrar acción CREATE
- [ ] Test Manual: Actualizar Q → GET /history → debe mostrar UPDATE con cambios
- [ ] Test Manual: Cambiar estado → GET /history → debe mostrar STATUS_CHANGE
- [ ] Test Manual: Agregar/editar/eliminar producto → GET /history → debe registrar acciones

---

## FASE 5: Clone Quotation

### Objetivo
Permitir clonar una Quotation existente con todos sus productos y QRs vinculadas.

### Archivos a crear
Ninguno

### Archivos a modificar
- [ ] `controller/IpQuotationController.java`
  - [ ] Endpoint `PATCH /ip/q/clone/{id_quotation}` con `@AccessToAction(CLONE_IP_QUOTATIONS)`
- [ ] `service/IpQuotationService.java`
  - [ ] Método `cloneQuotation(UUID id)`
- [ ] `service/impl/IpQuotationServiceImpl.java`
  - [ ] Implementar clonación: nuevo consecutivo, copiar campos, copiar QRs y productos
  - [ ] Registrar historial `CLONE` en Q original y `CREATE` en Q clonada
- [ ] `models/mapper/IpQuotationMapper.java`
  - [ ] Método `clone(IpQuotationEntity)` para copiar entity sin IDs

### Tests
- [ ] Test Manual: Clonar Q con productos → nueva Q con número diferente
- [ ] Test Manual: Verificar que Q clonada tiene mismo cliente, moneda, QRs, productos
- [ ] Test Manual: Verificar que Q clonada tiene estado CREATED
- [ ] Test Manual: Verificar historial de Q original muestra acción CLONE

### Lógica de clonación
- Nuevo número consecutivo
- Status = CREATED
- Copiar: cliente, contacto, currency, remarks, incoterms, etc.
- Copiar todas las QRs vinculadas (junction records)
- Copiar todos los productos con sus profit margins
- NO copiar: fechas (sent, answered, complete, reject), PDF, openBy

---

## FASE 6: Edit Payment Terms con Permiso Específico

### Objetivo
Asegurar que solo usuarios con el permiso `EDIT_PAYMENT_TERMS_IP_QUOTATIONS` (4003006) puedan modificar payment terms.

### Archivos a crear
Ninguno

### Archivos a modificar
- [ ] `service/impl/IpQuotationServiceImpl.java`
  - [ ] En `updateQuotation()`: validar permiso antes de actualizar `paymentTerms`
- [ ] `service/IpQuotationService.java` (opcional)
  - [ ] Documentar en Javadoc que paymentTerms requiere permiso especial

### Tests
- [ ] Test Manual: Usuario CON permiso 4003006 → puede actualizar paymentTerms
- [ ] Test Manual: Usuario SIN permiso 4003006 → intento de actualizar paymentTerms debe fallar con 403

### Implementación
```java
// Validar si el campo paymentTerms viene en el request
if (request.paymentTerms() != null && !request.paymentTerms().equals(existing.getPaymentTerms())) {
    // Validar permiso EDIT_PAYMENT_TERMS_IP_QUOTATIONS
    if (!hasPermission(EDIT_PAYMENT_TERMS_IP_QUOTATIONS)) {
        throw new InsufficientPermissionsException("Edit payment terms requires special permission");
    }
}
```

---

## FASE 7: Scheduler Auto-reject (45 días)

### Objetivo
Auto-rechazar Quotations en estado CREATED que tengan más de 45 días sin actividad.

### Archivos a crear
Ninguno

### Archivos a modificar
- [ ] `schedulers/IpQuotationScheduler.java`
  - [ ] Método `cronRejectIpQuotation()` con `@Scheduled(cron = "0 54 23 * * *")` (23:54 diario)
  - [ ] Lógica: buscar Q en estado CREATED con `createdAt < now - 45 days`, cambiar a REJECTED

### Tests
- [ ] Test Manual: Crear Q con fecha manipulada (más de 45 días) → ejecutar cron manualmente → verificar cambio a REJECTED
- [ ] Test Manual: Verificar logs del scheduler
- [ ] Test Manual: Verificar historial registra auto-reject

### Implementación
```java
@Scheduled(cron = "0 54 23 * * *") // 23:54 PM diario
private void cronRejectIpQuotation() {
    log.info("Starting auto-reject for old Quotations...");
    LocalDateTime cutoffDate = LocalDateTime.now().minusDays(45);
    List<IpQuotationEntity> oldQuotations = repository.findByStatusAndCreatedAtBefore(
        IpQuotationStatus.CREATED, cutoffDate
    );
    for (IpQuotationEntity q : oldQuotations) {
        q.setStatus(IpQuotationStatus.REJECTED);
        q.setRejectAt(LocalDateTime.now());
        repository.save(q);
        // Registrar historial
    }
    log.info("Auto-rejected {} quotations", oldQuotations.size());
}
```

---

## FASE 8: Generación de PDF (Pendiente Template Jasper)

### Objetivo
Generar PDF de la Quotation usando JasperReports.

### Prerequisitos
- [ ] Template Jasper (.jrxml) diseñado y ubicado en carpeta de reportes
- [ ] Definir qué información va en el PDF

### Archivos a crear
- [ ] `models/dto/reports/IpQuotationReportDTO.java`
- [ ] `models/dto/reports/IpQuotationProductReportDTO.java`

### Archivos a modificar
- [ ] `controller/IpQuotationController.java`
  - [ ] Endpoint `GET /ip/q/print/{id_quotation}`
- [ ] `service/IpQuotationService.java`
  - [ ] Método `printQuotation(UUID id)`
- [ ] `service/impl/IpQuotationServiceImpl.java`
  - [ ] Implementar generación PDF (similar a QR)
  - [ ] Cachear PDF path en campo `pdfUrl` para estados finales (COMPLETE, REJECTED)

### Tests
- [ ] Test Manual: Generar PDF de Q → descargar y verificar contenido
- [ ] Test Manual: Verificar PDF cacheado para Q en estado COMPLETE
- [ ] Test Manual: Regenerar PDF si Q se modifica

### Notas
- Basarse en implementación de `IpQuoteRequestServiceImpl.printQuoteRequest()`
- Considerar idioma (EN/ES) basado en cliente
- Incluir: número, fecha, cliente, productos con precios, totales, términos de pago, incoterms

---

## Funcionalidades Post-Implementación (Futuras)

### Mejoras Opcionales
- [ ] Scheduler para auto-cambio de estado (SENT → ANSWERED después de X días)
- [ ] Filtros adicionales en `FilterListIpQuotation` (por producto, referencias)
- [ ] Validación de transiciones de estado (máquina de estados más estricta)
- [ ] Notificaciones por email al cambiar estado
- [ ] Export a Excel de listado de Quotations

---

## Resumen de Endpoints Implementados/Pendientes

| Método | Endpoint | Acción | Estado |
|--------|----------|--------|--------|
| POST | `/ip/q` | Crear Q | ✅ Existente |
| PUT | `/ip/q/{id}` | Actualizar Q | ✅ Existente |
| GET | `/ip/q` | Listar Q | ✅ Existente |
| PATCH | `/ip/q/open-lock/{id}` | Abrir/Bloquear Q | ✅ Existente |
| PATCH | `/ip/q/close/{id}` | Cerrar Q | ✅ Existente |
| PATCH | `/ip/q/close-list` | Cerrar todas Q del usuario | ✅ Existente |
| GET | `/ip/q/load-open` | Cargar Q abiertas | ✅ Existente |
| PATCH | `/ip/q/change-status/{id}` | Cambiar estado | ✅ Existente |
| PATCH | `/ip/q/reject/{id}` | Rechazar Q | ✅ Existente |
| DELETE | `/ip/q/{id}/quote-request/{qqr_id}` | Remover QR de Q | ✅ Existente |
| POST | `/ip/q/{id}/product` | Agregar producto | ✅ Existente |
| PUT | `/ip/q/{id}/product/{pid}` | Editar producto | ✅ Existente |
| GET | `/ip/q/{id}/product/{pid}` | Obtener producto | ✅ Existente |
| DELETE | `/ip/q/{id}/product/{pid}` | Eliminar producto | ✅ Existente |
| **POST** | **`/ip/q/{id}/quote-request`** | **Agregar QR a Q** | **⏳ Fase 2** |
| **GET** | **`/ip/q/history/{id}`** | **Ver historial** | **⏳ Fase 4** |
| **PATCH** | **`/ip/q/clone/{id}`** | **Clonar Q** | **⏳ Fase 5** |
| **GET** | **`/ip/q/print/{id}`** | **Generar PDF** | **⏳ Fase 8** |

---

## Notas Técnicas

### Estándares de Código
- Seguir Java Coding Standards (Skill: java-coding-standards)
- Seguir Spring Boot Best Practices (Skill: java-springboot)
- Usar constructor injection
- Preferir records para DTOs de request/response
- Usar Optional para búsquedas
- Validar con Bean Validation (@Valid, @NotNull, etc.)

### Manejo de Errores
- Excepciones de dominio extienden `NotFoundException` o `BadRequestException`
- Mensajes descriptivos en español e inglés
- GlobalExceptionHandler maneja todas las excepciones

### Testing
- Cada fase debe ser testeable de forma independiente
- Tests manuales documentados antes de avanzar a siguiente fase
- Usar Postman/Insomnia para tests de endpoints

---

## Changelog

| Fecha | Fase | Cambios |
|-------|------|---------|
| 2026-05-04 | Inicial | Documento de seguimiento creado |

---

**Última actualización:** 2026-05-04
**Autor:** OpenCode AI Assistant
**Revisor:** JDB
