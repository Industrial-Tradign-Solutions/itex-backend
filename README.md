# ITEX Backend - Sistema de Gestión Corporativo

## Tabla de Contenidos

1. [Descripción del Proyecto](#descripción-del-proyecto)
2. [Tecnologías y Dependencias](#tecnologías-y-dependencias)
3. [Requisitos del Sistema](#requisitos-del-sistema)
4. [Estructura del Proyecto](#estructura-del-proyecto)
5. [Configuración del Entorno](#configuración-del-entorno)
6. [Instalación y Ejecución](#instalación-y-ejecución)
7. [Módulos de la API](#módulos-de-la-api)
8. [WebSocket](#websocket)
9. [Notas Importantes](#notas-importantes)
10. [Solución de Problemas](#solución-de-problemas)

---

## Descripción del Proyecto

**ITEX Backend** es el servidor backend de la aplicación ITEX (Industrial Trading Solutions), un sistema integral para la gestión de procesos corporativos de la empresa. Este backend proporciona todos los servicios REST, seguridad, gestión de archivos, notificaciones y comunicación en tiempo real necesarios para operar los procesos de negocio de la organización.

El sistema permite gestionar:
- Proveedores y contactos comerciales
- Órdenes de compra y cotización
- Inventario y productos
- Maestros (países, estados, ciudades, departamentos)
- Módulos de QR (cotizaciones)
- Administración de usuarios y permisos
- Comunicación en tiempo real via WebSocket

---

## Tecnologías y Dependencias

### Framework y Lenguaje
- **Java 21** - Versión del lenguaje
- **Spring Boot 3.3.6** - Framework principal
- **Gradle** - Gestor de dependencias y build

### Base de Datos
- **PostgreSQL** - Base de datos relacional
- **Flyway** - Gestión de migraciones de base de datos

### Seguridad
- **Spring Security** - Autenticación y autorización
- **JWT (jjwt 0.12.6)** - Tokens de autenticación
- **BCrypt** - Encriptación de contraseñas

### Generación de Documentos
- **JasperReports 7.0.3** - Generación de PDFs
- **Freemarker** - Plantillas de correo electrónico

### Otras Dependencias
- **Lombok** - Reducción de código repetitivo
- **MapStruct 1.6.2** - Mapeo de objetos
- **Apache POI 5.4.1** - Manejo de archivos Excel
- **Spring WebSocket** - Comunicación en tiempo real
- **Spring Mail** - Envío de correos electrónicos
- **Jackson** - Serialización JSON (zona horaria: America/New_York)

---

## Requisitos del Sistema

### Software Necesario
- JDK 21 o superior
- PostgreSQL 12 o superior
- Gradle 8.x (incluido en el proyecto)
- Git (para control de versiones)

### Recursos Recomendados
- Mínimo 2GB de RAM
- 500MB de espacio en disco
- Conexión a internet para descargar dependencias

---

## Estructura del Proyecto

```
itex-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/itradingsolutions/itex/
│   │   │       ├── ItexApplication.java          # Punto de entrada
│   │   │       ├── api/                          # Controladores y servicios
│   │   │       │   ├── admin/                    # Módulo de administración
│   │   │       │   ├── common/                   # Componentes comunes
│   │   │       │   ├── ip/                       # Módulo IP/QR
│   │   │       │   ├── masters/                  # Maestros (ubicaciones, departamentos)
│   │   │       │   └── partners/                 # Socios comerciales (proveedores)
│   │   │       └── config/                       # Configuraciones
│   │   │           ├── controller/              # ControllerAdvice
│   │   │           ├── files/                   # Configuración de archivos
│   │   │           ├── messages/                # Mensajes de validación
│   │   │           ├── security/                # Seguridad (JWT, filtros)
│   │   │           └── websocket/               # Configuración WebSocket
│   │   └── resources/
│   │       ├── db/migration/                    # Migraciones Flyway
│   │       │   ├── V1/                          # Migraciones versión 1.x
│   │       │   └── V2/                          # Migraciones versión 2.x
│   │       ├── emailTemplates/                 # Plantillas Freemarker
│   │       ├── fonts/                           # Fuentes para JasperReports
│   │       ├── images/                          # Imágenes del sistema
│   │       ├── application.yml                  # Configuración principal
│   │       └── messages/                        # Properties de mensajes
│   └── test/                                     # Pruebas (actualmente vacío)
├── build.gradle                                  # Configuración de build
├── settings.gradle                              # Configuración de proyecto
├── gradlew / gradlew.bat                        # Scripts de Gradle
├── ITEX Software.postman_collection.json        # Colección Postman
└── OPTIMIZATION_PLAN.md                        # Plan de optimización
```

### Arquitectura de Paquetes

La arquitectura sigue el patrón de paquetes por dominio/característica:

- **api/**: Contiene todos los controladores REST y servicios
  - `admin/` - Gestión de administración
  - `common/` - Componentes reutilizables (email, utilitarios)
  - `ip/qr/` - Módulo de solicitudes de cotización
  - `masters/` - Datos maestros (ubicaciones, departamentos)
  - `partners/` - Gestión de socios comerciales (proveedores)

- **config/**: Configuraciones del sistema
  - `controller/` - Manejo global de excepciones
  - `files/` - Configuración de manejo de archivos
  - `messages/` - Mensajes de validación
  - `security/` - Configuración de seguridad JWT
  - `websocket/` - Configuración de WebSocket

---

## Configuración del Entorno

### Variables de Entorno Requeridas

El sistema requiere las siguientes variables de entorno para funcionar correctamente:

#### Base de Datos
| Variable | Descripción | Valor Por Defecto |
|----------|-------------|-------------------|
| `DB_HOST` | Host de PostgreSQL | localhost |
| `DB_PORT` | Puerto de PostgreSQL | 5432 |
| `DB_NAME` | Nombre de la base de datos | itexdb |
| `DB_USER` | Usuario de la base de datos | itex |
| `DB_PASS` | Contraseña de la base de datos | 123456789 |

#### Seguridad
| Variable | Descripción | Valor Por Defecto |
|----------|-------------|-------------------|
| `TOKEN_SECRET_KEY` | Clave secreta para firmar JWT | ITS TOKEN SECRET KEY AUTHENTICATION PARA QUE SEA SEGURO EL SISTEMA |
| `TOKEN_EXPIRATION_HOURS` | Horas de expiración del token | 10 |
| `TOKEN_APPLICATION_NAME` | Nombre de la aplicación en el token | ITEX SOFTWARE |
| `ENCRYPTION_KEY` | Clave para encriptar datos sensibles | CLAVE PARA ENCRIPTAR |

#### Correo Electrónico
| Variable | Descripción | Valor Por Defecto |
|----------|-------------|-------------------|
| `MAIL_USER` | Usuario de SMTP (Gmail) | (vacío) |
| `MAIL_PASS` | Contraseña de SMTP | (vacío) |

#### Aplicación
| Variable | Descripción | Valor Por Defecto |
|----------|-------------|-------------------|
| `WEB_URL` | URL del frontend | http://localhost:4200/login |
| `SERVER_PORT` | Puerto del servidor | 8080 |
| `MAX_TABS_OPEN` | Máximo de pestañas abiertas | 30 |
| `DEVELOP_MODE` | Modo desarrollo (muestra SQL) | false |

#### Rutas de Archivos
| Variable | Descripción | Valor Por Defecto |
|----------|-------------|-------------------|
| `FOLDER_ROUTE_DATA` | Ruta de datos | ../data/ |
| `FOLDER_ROUTE_TEMP` | Ruta de archivos temporales | ../temp/ |
| `FOLDER_ROUTE_JASPER` | Ruta de reportes Jasper | ../reports/src/ |

### Configuración de application.yml

El archivo `src/main/resources/application.yml` contiene la configuración principal con valores por defecto. Las variables de entorno sobrescriben estos valores.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:itexdb}
    username: ${DB_USER:itex}
    password: ${DB_PASS:123456789}
  jpa:
    open-in-view: true
    show-sql: ${DEVELOP_MODE:false}
  jackson:
    time-zone: America/New_York

server:
  port: ${SERVER_PORT:8080}
  servlet:
    context-path: /itex/api
```

---

## Instalación y Ejecución

### Paso 1: Clonar el Repositorio

```bash
git clone <repositorio-itex-backend>
cd itex-backend
```

### Paso 2: Configurar Base de Datos

1. Instalar PostgreSQL 12 o superior
2. Crear una base de datos llamada `itexdb`
3. Crear un usuario con permisos sobre la base de datos
4. Ejecutar las migraciones automáticamente con Flyway al iniciar la aplicación

### Paso 3: Configurar Variables de Entorno

#### En Windows (PowerShell)
```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="itexdb"
$env:DB_USER="itex"
$env:DB_PASS="tu_contraseña"
$env:TOKEN_SECRET_KEY="tu_clave_secreta"
$env:MAIL_USER="tu_correo@gmail.com"
$env:MAIL_PASS="tu_contraseña_app"
$env:ENCRYPTION_KEY="tu_clave_encripcion"
$env:WEB_URL="http://localhost:4200/login"
$env:SERVER_PORT="8080"
```

#### En Linux/Mac (Bash)
```bash
export DB_HOST="localhost"
export DB_PORT="5432"
export DB_NAME="itexdb"
export DB_USER="itex"
export DB_PASS="tu_contraseña"
export TOKEN_SECRET_KEY="tu_clave_secreta"
export MAIL_USER="tu_correo@gmail.com"
export MAIL_PASS="tu_contraseña_app"
export ENCRYPTION_KEY="tu_clave_encripcion"
export WEB_URL="http://localhost:4200/login"
export SERVER_PORT="8080"
```

#### Usando archivo .env (recomendado)

Crear un archivo `.env` en la raíz del proyecto:
```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=itexdb
DB_USER=itex
DB_PASS=tu_contraseña
TOKEN_SECRET_KEY=tu_clave_secreta_jwt_muy_larga_y_segura
MAIL_USER=tu_correo@gmail.com
MAIL_PASS=tu_contraseña_app
ENCRYPTION_KEY=tu_clave_encripcion_32_caracteres
WEB_URL=http://localhost:4200/login
SERVER_PORT=8080
FOLDER_ROUTE_DATA=../data/
FOLDER_ROUTE_TEMP=../temp/
FOLDER_ROUTE_JASPER=../reports/src/
```

### Paso 4: Compilar el Proyecto

```bash
./gradlew build
```

Este comando:
- Descarga todas las dependencias
- Compila el código fuente
- Ejecuta las verificaciones configuradas

### Paso 5: Ejecutar la Aplicación

```bash
./gradlew bootRun
```

La aplicación se ejecutará en: `http://localhost:8080/itex/api`

### Comandos Gradle Útiles

| Comando | Descripción |
|---------|-------------|
| `./gradlew build` | Compila el proyecto |
| `./gradlew bootRun` | Ejecuta la aplicación |
| `./gradlew clean` | Limpia archivos generados |
| `./gradlew clean build` | Limpia y recompila |
| `./gradlew dependencies` | Lista las dependencias |

---

## Módulos de la API

### Endpoints Principales

La API sigue el patrón RESTful y está disponible en `http://localhost:8080/itex/api`

#### Proveedores (Partners/Suppliers)
- `GET /suppliers` - Listar proveedores
- `POST /suppliers` - Crear proveedor
- `PUT /suppliers/{id}` - Actualizar proveedor
- `DELETE /suppliers/{id}` - Eliminar proveedor
- `GET /suppliers/{id}` - Obtener proveedor por ID
- `GET /suppliers/{id}/contacts` - Obtener contactos
- `POST /suppliers/{id}/contacts` - Agregar contacto

#### Maestros (Masters)
- `GET /countries` - Listar países
- `GET /countries/{id}/states` - Estados por país
- `GET /states/{id}/cities` - Ciudades por estado
- `GET /departments` - Listar departamentos
- `POST /departments` - Crear departamento

#### Solicitudes de Cotización (QR)
- `GET /quote-requests` - Listar solicitudes
- `POST /quote-requests` - Crear solicitud
- `PUT /quote-requests/{id}` - Actualizar solicitud
- `GET /quote-requests/{id}/products` - Productos de la solicitud
- `POST /quote-requests/{id}/products` - Agregar producto

#### Comunes (Common)
- `POST /auth/login` - Inicio de sesión
- `POST /auth/register` - Registro de usuarios
- `GET /common/upload/{fileName}` - Descargar archivo
- `POST /common/upload` - Subir archivo

### Autenticación

El sistema utiliza JWT para autenticación. Los endpoints protegidos requieren el header:
```
Authorization: Bearer <token_jwt>
```

### Validación de Solicitudes

El sistema utiliza Bean Validation con anotaciones como:
- `@NotNull` - Campo obligatorio
- `@NotBlank` - Cadena no vacía
- `@Size(min, max)` - Longitud válida
- `@Email` - Formato de correo válido

---

## WebSocket

### Configuración

El WebSocket está disponible en: `/itex/api/ws`

### Tipos de Mensajes

El sistema define tipos de mensajes para comunicación en tiempo real:
- `NOTIFICATION` - Notificaciones del sistema
- `UPDATE` - Actualizaciones de datos
- `ALERT` - Alertas y advertencias

### Uso del WebSocket

Para conectarse desde el cliente:
```javascript
const socket = new WebSocket('ws://localhost:8080/itex/api/ws');
```

---

## Notas Importantes

### Zona Horaria

- El sistema utiliza la zona horaria `America/New_York` (Eastern Time)
- Todas las fechas y horas en la API se devuelven en esta zona horaria

### Open-In-View

- `spring.jpa.open-in-view: true` está habilitado
- **Advertencia**: Tener cuidado con el acceso a relaciones lazy después de que la respuesta ha sido enviada

### JasperReports

- El archivo de fuentes `src/main/resources/fonts/calibri-fonts.jar` debe existir
- Sin este archivo, la generación de PDFs fallará

### Flyway

- Las migraciones se encuentran en `src/main/resources/db/migration/`
- V1/ - Migraciones de la versión 1.x
- V2/ - Migraciones de la versión 2.x
- Los rollbacks están en `src/main/resources/db/rollback/`

### Plantillas de Correo

- Located in `src/main/resources/emailTemplates/`
- Format: FreeMarker templates (.ftl)
- Templates disponibles:
  - `register.ftl` - Correo de registro
  - `commercial.ftl` - Correo comercial
  - `client-notification.ftl` - Notificaciones al cliente

### Configuración de Correo

- Servidor SMTP: smtp.gmail.com
- Puerto: 587
- Requires TLS enabled

---

## Solución de Problemas

### Error de Conexión a la Base de Datos

Verificar que:
1. PostgreSQL esté ejecutándose
2. Las credenciales sean correctas
3. La base de datos `itexdb` exista
4. El usuario tenga permisos sobre la base de datos

### Error al Generar PDFs

Verificar que:
1. El archivo `calibri-fonts.jar` exista en `src/main/resources/fonts/`
2. La ruta de JasperReports esté configurada correctamente

### Errores de JWT

Verificar que:
1. `TOKEN_SECRET_KEY` esté configurado
2. El token no haya expirado
3. El formato del token sea correcto

### Errores de Envío de Correo

Verificar que:
1. Las credenciales de Gmail sean correctas
2. Se esté usando una contraseña de aplicación (no la contraseña normal)
3. El SMTP esté habilitado en la cuenta de Google

---

## Contribuir al Proyecto

1. Crear un branch desde `develop`
2. Realizar los cambios necesarios
3. Ejecutar `./gradlew build` para verificar
4. Crear un Pull Request

---

## Licencia

Copyright © 2024 Industrial Trading Solutions. Todos los derechos reservados.

---

## Contacto

Para soporte técnico o consultas:
- Correo: soporte@itradingsolutions.com
- Web: https://itradingsolutions.com