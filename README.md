# ISANORTE Backend API (Spring Boot)

Este es el backend oficial del proyecto ISANORTE, desarrollado en Java con el framework Spring Boot. Este documento contiene todas las instrucciones necesarias para configurar el entorno local, conectarse a los servicios de terceros, configurar la seguridad y ejecutar la aplicación.

---

## 🛠️ Requisitos Previos

Asegúrate de tener instalados los siguientes componentes en tu sistema operativo:

1. **Java Development Kit (JDK) 21**
2. **PostgreSQL 14 o superior** (Servidor de base de datos)
3. **Maven** (Opcional, el proyecto incluye el wrapper `./mvnw`)
4. Una cuenta activa en **Cloudinary** (para el almacenamiento de imágenes)

---

## ⚙️ Configuración Inicial

### 1. Base de Datos (PostgreSQL)
El proyecto utiliza PostgreSQL y gestiona las versiones de la base de datos a través de **Flyway**. Necesitas crear la base de datos y el usuario con los que se conectará la API.

Abre la consola de PostgreSQL (`psql`) o utiliza un cliente como pgAdmin/DBeaver y ejecuta las siguientes sentencias SQL:

```sql
-- 1. Crear la base de datos
CREATE DATABASE isanorte_db;

-- 2. Crear el usuario con su respectiva contraseña
CREATE USER isanorte_user WITH PASSWORD 'TuContraseña';

-- 3. Otorgar privilegios al usuario sobre la base de datos
GRANT ALL PRIVILEGES ON DATABASE isanorte_db TO isanorte_user;
```

*Nota: Flyway se encargará de crear las tablas de manera automática al iniciar la aplicación. No es necesario ejecutar ningún script de tablas (DDL) de forma manual.*

---

### 2. Credenciales de Cloudinary
El almacenamiento de fotografías del catálogo se realiza externamente en Cloudinary. La API necesita tus llaves para autorizar la subida de archivos. 

Puedes configurar estas credenciales de dos maneras:

**Opción A: Variables de entorno (Recomendado)**
```bash
export CLOUDINARY_CLOUD_NAME="tu_nombre_de_cloud"
export CLOUDINARY_API_KEY="tu_api_key"
export CLOUDINARY_API_SECRET="tu_api_secret"
```

**Opción B: Editando `application.properties` (Para desarrollo local)**
En `src/main/resources/application.properties`:
```properties
cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME:tu_nombre_de_cloud}
cloudinary.api-key=${CLOUDINARY_API_KEY:tu_api_key}
cloudinary.api-secret=${CLOUDINARY_API_SECRET:tu_api_secret}
```

---

### 3. Autenticación y Seguridad (Spring Security & JWT)

La API cuenta con autenticación basada en **JSON Web Tokens (JWT)** y control de acceso basado en roles (`ROLE_ADMINISTRADOR`).

#### Variables de Configuración

| Variable de Entorno | Propiedad en `application.properties` | Requisito / Restricción | Descripción |
| :--- | :--- | :--- | :--- |
| `JWT_SECRET` | `app.jwt.secret` | **Mínimo 32 caracteres (bytes)** | Clave secreta para la firma HMAC-SHA256 de los tokens. |
| `ADMIN_EMAIL` | `app.auth.initial-email` | Formato de email válido | Correo del usuario administrador inicial. |
| `ADMIN_PASSWORD` | `app.auth.initial-password` | **Entre 12 y 72 caracteres** | Contraseña del administrador inicial (hasheada con BCrypt). |
| `JWT_EXPIRATION` | `app.jwt.expiration` | Formato ISO-8601 (ej: `PT8H`) | Tiempo de validez del token (por defecto 8 horas). |
| `ADMIN_ROLE` | `app.auth.admin-role` | Nombre de rol (por defecto `ADMINISTRADOR`) | Nombre del rol con permisos administrativos. |

> **⚠️ Importante:** 
> - Si `JWT_SECRET` tiene menos de 32 caracteres, la aplicación arrojará una excepción al iniciar y se detendrá.
> - Al iniciar la aplicación por primera vez con `ADMIN_EMAIL` y `ADMIN_PASSWORD` configurados, el componente `AdminInitializer` creará automáticamente el administrador inicial en la base de datos. Si el usuario ya existe, no se modificará.

#### Cómo configurarlo:

**Opción A: Variables de entorno (Recomendado)**
```bash
export JWT_SECRET="tu_clave_secreta_super_segura_de_mas_de_32_bytes"
export ADMIN_EMAIL="admin@isanorte.com"
export ADMIN_PASSWORD="AdminPasswordSeguro123!"
```

**Opción B: En `application.properties` (Para desarrollo local)**
```properties
app.auth.admin-role=ADMINISTRADOR
app.auth.initial-email=${ADMIN_EMAIL:admin@isanorte.com}
app.auth.initial-password=${ADMIN_PASSWORD:AdminPasswordSeguro123!}
app.jwt.secret=${JWT_SECRET:tu_clave_secreta_super_segura_de_mas_de_32_bytes}
app.jwt.expiration=${JWT_EXPIRATION:PT8H}
```

---

## 🔐 Uso de la Autenticación (API Endpoints)

### Endpoints Públicos
- `POST /api/auth/login`: Autenticación y obtención de token.
- `GET /api/publico/**`: Consulta de catálogo de productos, banners, categorías, etc.
- `GET /actuator/health`: Chequeo de salud del servicio.

### Endpoints Protegidos
- Todas las rutas bajo `/api/**` que no sean públicas requieren el rol `ADMINISTRADOR`.

### 1. Iniciar Sesión (`Login`)
Envía una petición `POST` a `/api/auth/login`:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@isanorte.com",
    "password": "AdminPasswordSeguro123!"
  }'
```

**Respuesta exitosa (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "expiraEn": "2026-09-29T00:54:00Z"
}
```

### 2. Consumir Endpoints Protegidos
Para realizar peticiones a endpoints administrativos (creación, edición, eliminación), adjunta el token en la cabecera `Authorization`:

```http
Authorization: Bearer <tu_token_aqui>
```

Ejemplo con `curl`:
```bash
curl -X POST http://localhost:8080/api/productos \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1Ni..." \
  -H "Content-Type: application/json" \
  -d '{ ... }'
```

---

## 🚀 Ejecución del Proyecto

Una vez configuradas la base de datos, Cloudinary y las variables de seguridad, puedes levantar el proyecto:

**En Linux / macOS:**
```bash
./mvnw spring-boot:run
```

**En Windows:**
```cmd
mvnw.cmd spring-boot:run
```

Si todo es correcto, verás el banner de Spring Boot en la consola y la confirmación de inicialización:
```text
Administrador inicial creado correctamente.
Started ConstructoraApiApplication in ... seconds
```

### Catálogo de demostración ISADECOR (opt-in)

El catálogo demo se encuentra en `src/main/resources/data/isadecor-demo-products.json`. El loader usa
la unidad `isadecor` y categorías activas que ya deben existir en la base de datos; no crea unidades,
categorías ni migraciones. Es idempotente por SKU/slug y no elimina productos existentes.

Para cargarlo una vez en desarrollo o durante una demo:

```bash
SEED_DEMO_PRODUCTS=true ./mvnw spring-boot:run
```

También se puede usar `--app.seed.demo-products=true`. La propiedad vale `false` por defecto, de modo
que el seed no se ejecuta automáticamente en producción. Si falta alguna categoría indicada por el JSON,
el arranque falla con el slug exacto que se debe crear previamente desde la administración.

---

## 🧪 Ejecución de Pruebas Unitarias

Para ejecutar la suite de pruebas del proyecto:

```bash
./mvnw test
```

O para ejecutar un test específico:
```bash
./mvnw test -Dtest=AuthServiceTest
```

---

## 📁 Estructura Principal del Proyecto

- `src/main/java/.../config/`: Configuraciones de Beans, CORS, inicializadores (`AdminInitializer.java`) y Seguridad (`SecurityConfig.java`).
- `src/main/java/.../security/`: Lógica de autenticación, servicio JWT (`JwtService.java`) y UserDetailsService.
- `src/main/java/.../controller/`: Controladores REST (ej: `AuthController.java`, `ArchivoController.java`).
- `src/main/java/.../service/`: Servicios y lógica de negocio.
- `src/main/resources/db/migration/`: Scripts SQL gestionados por Flyway para la evolución de la base de datos.
- `src/main/resources/application.properties`: Archivo principal de configuración.
