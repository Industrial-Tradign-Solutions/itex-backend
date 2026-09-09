# DOCUMENTACIÓN DE CAMBIOS — SISTEMA WEBSOCKET (BACKEND ITEX)

**Fecha:** 2026-09-09
**Alcance:** Refactorización de la infraestructura WebSocket del backend `itex-backend`.
**Audiencia:** Equipo Frontend (consumidores del WebSocket y de las listas maestras).

---

## 1. Resumen ejecutivo

El WebSocket ya **no** se utiliza para sincronizar listas ni datos maestros. Ahora su única
responsabilidad es **notificar eventos de sesión de usuario** (nuevo login, cierre masivo,
inhabilitación de usuario/rol). El backend conserva el endpoint, el handshake y el formato
JSON de los mensajes de sesión **sin cambios** para minimizar el impacto.

**Consecuencia principal para Frontend:** tras cada mutación (crear, actualizar, habilitar o
deshabilitar) de Roles, Usuarios, Industrias, Departamentos, Países, Estados o Ciudades, el
frontend ya no recibirá el evento `LIST_*` con la lista actualizada. Debe **re-consultar el
endpoint REST `.../basic`** correspondiente (ver sección 3).

---

## 2. URL, handshake y contrato de mensaje (SIN CAMBIOS)

| Aspecto | Valor |
|---|---|
| Ruta WebSocket | `/websocket` registrada bajo el context-path del backend: `ws(s)://<host>:<puerto>/itex/api/websocket` |
| Handshake | Mismo: token JWT como query param `?token=<JWT>` |
| Autenticación | La valida el backend al conectar; si el token falta o es inválido se recibe un mensaje `ERROR_SOCKET` |

Formato JSON de cada mensaje (las claves se conservan idénticas):

```json
{
  "data": {},
  "webSocketMessageValue": "EVENTO",
  "webSocketMessageType": "LOGOUT",
  "sessionId": "0"
}
```

- `data`: contenido tipado del evento (objeto o texto, según evento).
- `webSocketMessageValue`: nombre del evento (mayúsculas).
- `webSocketMessageType`: tipo derivado del evento (`LOGOUT` o `ERROR`).
- `sessionId`: identificador de la sesión WebSocket destino.

Nota técnica: `webSocketMessageType` ya no viaja como campo mutable; se deriva del evento.
Las claves JSON son las mismas; únicamente el orden de las claves dentro del objeto puede variar
(no afecta a ningún parser JSON).

---

## 3. EVENTOS ELIMINADOS — el backend ya NO los emite

Se eliminaron todos los eventos `LIST_*` que empujaban listas maestras a todos los clientes:

| Evento eliminado | Lista que empujaba | Acción requerida en Frontend |
|---|---|---|
| `LIST_ROLES` | Roles | Tras mutar un rol, re-consultar `GET {BASE}/admin/roles/basic` |
| `LIST_USERS` | Usuarios | Tras mutar un usuario, re-consultar `GET {BASE}/admin/users/basic` |
| `LIST_INDUSTRIES` | Industrias | Tras mutar una industria, re-consultar `GET {BASE}/master/industries/basic` |
| `LIST_DEPARTMENTS` | Departamentos | Tras mutar un departamento, re-consultar `GET {BASE}/master/departments/basic` |
| `LIST_COUNTRIES` | Países | Tras mutar un país, re-consultar `GET {BASE}/master/locations/countries/basic` |
| `LIST_STATES` | Estados | Tras mutar un estado, re-consultar `GET {BASE}/master/locations/states/basic` |
| `LIST_CITIES` | Ciudades | Tras mutar una ciudad, re-consultar `GET {BASE}/master/locations/cities/basic` |

Donde `{BASE}` es `http(s)://<host>:<puerto>/itex/api`.

**Pasos sugeridos por mutación (create / update / enable / disable):**
1. El frontend ya recibe en la respuesta HTTP el `MessageResponse` con el DTO modificado
   (200/201). No cambió nada del lado REST.
2. Sustituir el antiguo "listener de socket LIST_*" por una llamada `GET .../basic` del módulo
   correspondiente y actualizar el estado local / store con la respuesta `{enables, disables}`.
3. Eliminar de la lógica de suscripción del socket todos los manejadores de `LIST_*`.

Formato de respuesta esperado en los `.../basic` (sigue igual):

```json
{
  "enables": [ { ... } ],
  "disables": [ { ... } ]
}
```

**Eventos que nunca llegaron a emitirse (código muerto eliminado, sin impacto real):**
`OPEN_PROSPECTS` / tipo `OPEN_RECORDS`. Si el frontend tenía listeners para este evento, puede
eliminarlos: el backend nunca los envió.

---

## 4. EVENTOS CONSERVADOS — únicos mensajes que recibirá el frontend

Todos se emiten por **broadcast a todas las sesiones conectadas** (comportamiento idéntico al
anterior); el frontend ya filtra por `token` / `userId` cuando corresponde.

### 4.1 Eventos de sesión (payload tipado `{token, userId}`)

| `webSocketMessageValue` | Cuándo se emite | `data` (JSON) |
|---|---|---|
| `NEW_LOGIN` | Login exitoso de un usuario | `{ "token": "<jwt de la NUEVA sesión>", "userId": "<uuid>" }` |
| `DISABLE_USER` | Un usuario fue inhabilitado (desactivado) | `{ "token": null, "userId": "<uuid>" }` |
| `DISABLE_ROLE` | Un rol fue inhabilitado y tiene usuarios asignados (se envía una vez **por cada** usuario del rol) | `{ "token": null, "userId": "<uuid>" }` |

Comportamiento esperado en el cliente (sin cambios):
- `NEW_LOGIN`: cerrar las sesiones/tabs **del mismo usuario** cuyo token sea distinto al recibido.
- `DISABLE_USER` / `DISABLE_ROLE`: cerrar la sesión del usuario cuyo `userId` coincide con el
  del usuario autenticado localmente.

### 4.2 Notificaciones de sistema (payload texto)

| `webSocketMessageValue` | Cuándo se emite | `data` (JSON) |
|---|---|---|
| `NOTIFICATION_LOGOUT` | Tras ejecutar "cerrar todas las sesiones": se emite 10 veces, cada 30 segundos, durante 5 minutos | `"The system will be offline in [X.X] minutes"` |
| `CLOSE_ALL_SESSIONS` | Al cumplirse el tiempo programado de "cerrar todas las sesiones" (5 minutos) | `"Your session has ended, the system will be offline for <N> minutes;"` |

### 4.3 Error de conexión

| `webSocketMessageValue` | Cuándo se emite | `data` (JSON) |
|---|---|---|
| `ERROR_SOCKET` | El token del handshake falta, es inválido o expiró | `"Error al ingresar al socket"` |

---

## 5. Cambios internos del backend (sin efecto en el contrato)

- Unificación a una única instancia del handler WebSocket (antes se creaban hasta 4 instancias).
- Registro de sesiones thread-safe; envío protegido ante escrituras concurrentes.
- Payload de sesión tipado con `record` inmutable (mismas claves JSON `token`/`userId`).
- Mensajes WebSocket convertidos a `record` inmutable (misma serialización JSON).
- Parseo robusto del parámetro `token` en el handshake.
- Logs INFO/WARN/ERROR en conexión, cierre, rechazo, serialización y envío.
- Tareas asíncronas ahora usan hilos virtuales (Java 21) y el temporizador de notificaciones
  de cierre es daemon (no impide la detención del proceso).

---

## 6. Checklist para el equipo Frontend

- [ ] Eliminar listeners de `LIST_ROLES`, `LIST_USERS`, `LIST_INDUSTRIES`, `LIST_DEPARTMENTS`,
      `LIST_COUNTRIES`, `LIST_STATES`, `LIST_CITIES`.
- [ ] Tras cada mutación de maestras/roles/usuarios, re-consultar el `GET .../basic`
      correspondiente (sección 3).
- [ ] Conservar listeners de `NEW_LOGIN`, `DISABLE_USER`, `DISABLE_ROLE`,
      `NOTIFICATION_LOGOUT`, `CLOSE_ALL_SESSIONS` y `ERROR_SOCKET` (sin cambios).
- [ ] La URL de conexión y el handshake `?token=` no cambian.
