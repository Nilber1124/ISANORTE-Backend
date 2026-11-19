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

**Variables de entorno para la base de datos, CORS y puerto (obligatorias en Render):**
```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://<host>/<db>?sslmode=require"   # debe empezar con jdbc:postgresql://
export SPRING_DATASOURCE_USERNAME="tu_usuario"
export SPRING_DATASOURCE_PASSWORD="tu_contraseña"
export CORS_ALLOWED_ORIGINS="https://tu-frontend.vercel.app"   # lista separada por comas; solo orígenes exactos, sin comodines
export PORT=8080   # Render la inyecta automáticamente; en local por defecto es 8080
```
Si no se definen `SPRING_DATASOURCE_URL` y `SPRING_DATASOURCE_USERNAME`, la aplicación usa `jdbc:postgresql://localhost:5432/isanorte_db` y el usuario `isanorte_user`. **No hay contraseña por defecto**: `SPRING_DATASOURCE_PASSWORD` debe definirse siempre.

`src/main/resources/application.properties` está excluido de Git porque puede contener configuración local sensible. Usa `application.example.properties` como plantilla y conserva las credenciales exclusivamente en variables de entorno o en un gestor de secretos. Si una credencial llegó a versionarse, eliminarla del archivo no es suficiente: debe revocarse y rotarse en el proveedor correspondiente.

**Configuración para Render:** el perfil versionado `application-render.properties` enlaza estas variables sin almacenar secretos. Actívalo en el panel de Render con:

```bash
SPRING_PROFILES_ACTIVE=render
```

El perfil escucha en `0.0.0.0` y usa el puerto proporcionado por `PORT`. Flyway ejecuta todas las migraciones (esquema inicial, restricciones de PostgreSQL y datos mínimos), que crean las tablas que usan las entidades `Catalogo`, `Pedido`, `DetallePedido` y `DireccionCliente`. Los archivos `.env`, `application.properties` y otros perfiles locales quedan excluidos del contexto Docker; solo se permiten expresamente las plantillas seguras `application.example.properties` y `application-render.properties`.

---

### 2. Credenciales de Cloudinary
El almacenamiento de fotografías del catálogo se realiza externamente en Cloudinary. La API necesita tus llaves para autorizar la subida de archivos. 

Configura estas credenciales mediante variables de entorno:
```bash
export CLOUDINARY_CLOUD_NAME="tu_nombre_de_cloud"
export CLOUDINARY_API_KEY="tu_api_key"
export CLOUDINARY_API_SECRET="tu_api_secret"
```

La plantilla `src/main/resources/application.example.properties` referencia estas variables sin valores sensibles predeterminados. No copies claves reales al README ni a archivos versionados.

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
| `CORS_ALLOWED_ORIGINS` | `cors.allowed-origins` | Lista de orígenes `http://` o `https://` exactos, separados por comas; no admite `*` ni rutas | Frontends autorizados para llamadas desde navegador. |
| `MULTIPART_MAX_FILE_SIZE` | `spring.servlet.multipart.max-file-size` | Tamaño de Spring (`7MB` por defecto; no superar `8MB` sin evaluación) | Tamaño máximo por archivo subido a Cloudinary. |
| `MULTIPART_MAX_REQUEST_SIZE` | `spring.servlet.multipart.max-request-size` | Tamaño de Spring (`8MB` por defecto y mayor que el límite de archivo) | Tamaño máximo total de una petición multipart. |

> **⚠️ Importante:** 
> - Si `JWT_SECRET` tiene menos de 32 caracteres, la aplicación arrojará una excepción al iniciar y se detendrá.
> - Al iniciar la aplicación por primera vez con `ADMIN_EMAIL` y `ADMIN_PASSWORD` configurados, el componente `AdminInitializer` creará automáticamente el administrador inicial en la base de datos. Si el usuario ya existe, no se modificará.
> - `SPRING_DATASOURCE_PASSWORD`, `ADMIN_PASSWORD`, `JWT_SECRET` y las credenciales de Cloudinary no deben tener valores sensibles por defecto ni almacenarse en Git.
> - Los límites multipart predeterminados dejan margen para la envoltura de una petición con un archivo de hasta 7 MB. Amplíalos solo si existe un requisito funcional verificado y se han evaluado la memoria disponible, las cuotas y el control de frecuencia, ya que la carga actual se materializa en memoria antes de enviarse a Cloudinary.

#### Cómo configurarlo:

**Opción A: Variables de entorno (Recomendado)**
```bash
export JWT_SECRET="tu_clave_secreta_super_segura_de_mas_de_32_bytes"
export ADMIN_EMAIL="admin@isanorte.com"
export ADMIN_PASSWORD="AdminPasswordSeguro123!"
```

**Plantilla local segura:**

Haz una copia local de `src/main/resources/application.example.properties` como `application.properties`. El archivo resultante está ignorado por Git y mantiene las referencias a variables sin incluir secretos:
```properties
app.auth.admin-role=ADMINISTRADOR
app.auth.initial-email=${ADMIN_EMAIL}
app.auth.initial-password=${ADMIN_PASSWORD}
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=${JWT_EXPIRATION:PT8H}
```

La API es stateless y autentica mediante `Authorization: Bearer`; no usa cookies de autenticación. CSRF se excluye únicamente para `/api/**` y permanece activo para rutas web no API. CORS se centraliza en `CorsConfig` y rechaza comodines, rutas y esquemas distintos de HTTP/HTTPS.

---

## 🔐 Uso de la Autenticación (API Endpoints)

### Endpoints Públicos
- `POST /api/auth/login`: Autenticación y obtención de token.
- `GET /api/publico/**`: Consulta de catálogo de productos, banners, categorías, etc. Navegar la tienda no requiere cuenta.
- `POST /api/publico/cuenta/registro` y `POST /api/publico/cuenta/login`: Cuentas de cliente (ver abajo).
- `GET .../productos/{productoSlug}/recomendados`: Hasta cuatro recomendaciones automáticas del catálogo publicado.
- `GET .../productos/{productoSlug}/resenas` y `/resenas/resumen`: Comentarios y estadísticas persistidas.
- `POST /api/publico/resenas/{resenaId}/util`: Incrementa el contador de utilidad de una reseña.
- `POST .../productos/{productoSlug}/resenas`: Preparado para la futura autenticación de clientes; actualmente rechaza visitantes y credenciales administrativas.
- `GET /api/publico/sitios/{clave}/unidades/{unidadSlug}/home`: Home completa de una unidad (hero, cinta, promociones, campaña, secciones «tienda» y «fáciles de instalar» por demanda, servicios y reseñas) en una sola respuesta.
- `GET /actuator/health`: Chequeo de salud del servicio.

### Endpoints Protegidos

**Datos nuevos de empresa, sitio y contacto.** La empresa publica `anioFundacion`, `zonaCobertura`,
`tiempoRespuesta` y la ubicación de la oficina (`latitud`, `longitud`, validadas entre -90/90 y -180/180); la unidad destacada, sus `ofertas` (texto con «|» publicado como lista); y la solicitud
de contacto guarda `ubicacion`, `servicioInteres` y `consentimiento` (obligatorio, con `fechaConsentimiento`).

**Contenido del sitio.** Preguntas frecuentes, equipo, credenciales, testimonios, garantías, pasos del
proceso, políticas (privacidad, etc.) y galería de proyectos (`ANTES|PROCESO|FINAL|DETALLE`) tienen su propia tabla y un CRUD administrativo (`/api/faqs`, `/api/equipo`, `/api/credenciales`,
`/api/testimonios`, `/api/garantias`, `/api/pasos-proceso`, `/api/politicas`, `/api/proyecto-galeria`; JWT administrativo, con
`PATCH /{id}/activo`). Los proyectos tienen ficha técnica (`areaM2`, `plazoMeses`, `alcance`, `tipoObra`). El sitio lee todo, solo activos y ordenados, en `GET /api/publico/sitios/{clave}/confianza`.

El modulo de anuncios administra `GET/POST /api/anuncios`, `GET/PUT/DELETE /api/anuncios/{id}` y
`PATCH /api/anuncios/{id}/activo`. Estas rutas requieren JWT administrativo. Las imagenes reutilizan
la carga Cloudinary existente en `POST /api/archivos/subir-imagen`.

- Todas las rutas bajo `/api/**` que no sean públicas requieren el rol `ADMINISTRADOR`.
- Requieren sesión de cliente (rol `CLIENTE`, cabecera `Authorization: Bearer <token>`):
  - `GET /api/publico/cuenta/me` y `GET /api/publico/cuenta/cotizaciones`.
  - `GET` y `PUT /api/publico/cuenta/carrito`: carrito guardado en la cuenta (se reemplaza completo en cada `PUT`, máximo 50 líneas).
  - `POST /api/publico/sitios/{clave}/unidades/{unidadSlug}/cotizaciones`: la cotización queda vinculada a la cuenta.
  - `POST /api/publico/sitios/{clave}/unidades/{unidadSlug}/pedidos`: crea un pedido vinculado a la cuenta y su dirección.
  - `POST .../productos/{productoSlug}/comparar-precio` y `GET .../productos/{productoSlug}/comparacion-competidores`.

### Cuentas de cliente

- Son independientes de los administradores: tabla `clientes`, token con rol `CLIENTE` y sin acceso a `/api/**` administrativo.
- `POST /api/publico/cuenta/registro` exige `aceptaTratamientoDatos: true` y una contraseña de al menos 8 caracteres. Un correo repetido responde `409`.
- El token expira según `app.jwt.expiration`. Tras expirar, el frontend debe pedir un nuevo inicio de sesión.
- El esquema inicial (`V1`) incluye las reseñas persistidas de productos.
- `V1` crea `clientes` y relaciona `cotizaciones.cliente_id`.
- `V1` crea `carritos_cliente`, un carrito por cuenta.
- `V1` incorpora catálogos, inventario, pedidos y direcciones.

### Anuncios públicos

- `GET /api/publico/anuncios/ISANORTE` y `GET /api/publico/anuncios/ISADECOR` no requieren autenticación.
- Solo retornan anuncios activos, vigentes y destinados al sitio solicitado o a `AMBOS`, ordenados por
  orden de visualización, fecha de creación e identificador.
- `V1` crea la tabla de anuncios, las restricciones de enums y vigencia, y el índice de consulta pública.
- El parámetro opcional `ubicacion` permite consultar `GENERAL`, `CINTA_HOME`,
  `PROMOCION_PRINCIPAL`, `PROMOCION_SECUNDARIA`, `ANUNCIOS_HOME` o `CATEGORIA_SUPERIOR`.
- `V1` amplía anuncios para estas ubicaciones y agrega el orden

### 1. Iniciar Sesión (`Login`)
Envía una petición `POST` a `/api/auth/login`:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "<ADMIN_EMAIL>",
    "password": "<ADMIN_PASSWORD>"
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

### Datos iniciales y de ejemplo

Flyway crea el esquema (`V1`), los índices exclusivos de PostgreSQL (`V2`), una empresa mínima con su sitio
`isanorte` y la unidad ISADECOR (`V3`) y las tablas de la Home de ISADECOR y del inventario (`V4`). Las
migraciones comunes están en `db/migration/common/` y las propias de PostgreSQL en `db/migration/postgresql/`
(los tests con H2 solo ejecutan las comunes, vía `{vendor}`).

**Home de ISADECOR e inventario.** El contenido de la Home (hero, cinta, promociones, campaña, servicios y
secciones de productos) se administra en `/api/isadecor/{hero|cinta|promociones|campanas|servicios-empresa|servicio-puntos|secciones}`
y los productos fijados o excluidos de cada sección en `/api/isadecor/secciones/{id}/fijados|excluidos`.
Las secciones «Elige tu acabado» y «Fáciles de instalar» se ordenan automáticamente por demanda
(pedidos + cotizaciones en la ventana de días configurada), con modo `AUTOMATICO`, `MANUAL` o `MIXTO`. El stock
se gestiona por acabado (`PATCH /api/productos/{id}/stock` y `.../variantes/{varianteId}/stock`, historial en
`GET /api/productos/{id}/movimientos-stock`); los pedidos lo descuentan y la cancelación lo restituye. Las reseñas que salen en la Home se eligen con `GET /api/resenas` y `PATCH /api/resenas/{id}/home`. **No hay seeders ni contenido comercial en el código:** el contenido
(servicios, proyectos, equipo, preguntas frecuentes, etc.) se carga desde el panel administrativo o con un script
SQL aparte, y el administrador inicial se crea con `ADMIN_EMAIL`/`ADMIN_PASSWORD`.

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
- `src/main/resources/application.example.properties`: plantilla versionada sin secretos; copiar localmente como `application.properties`, que permanece ignorado por Git.
