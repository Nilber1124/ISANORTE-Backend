# ISANORTE Backend API (Spring Boot)

Este es el backend oficial del proyecto ISANORTE, desarrollado en Java con el framework Spring Boot. Este documento contiene todas las instrucciones necesarias para configurar el entorno local, conectarse a los servicios de terceros y ejecutar la aplicación.

## 🛠️ Requisitos Previos

Asegúrate de tener instalados los siguientes componentes en tu sistema operativo:

1. **Java Development Kit (JDK) 17 o superior**
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
CREATE USER isanorte_user WITH PASSWORD 'Nil71232516';

-- 3. Otorgar privilegios al usuario sobre la base de datos
GRANT ALL PRIVILEGES ON DATABASE isanorte_db TO isanorte_user;
```

*Nota: Flyway se encargará de crear las tablas de manera automática al iniciar la aplicación. No es necesario ejecutar ningún script de tablas (DDL) de forma manual.*

### 2. Credenciales de Cloudinary
El almacenamiento de fotografías del catálogo se realiza externamente en Cloudinary. La API necesita tus llaves para autorizar la subida de archivos. 

Puedes configurar estas credenciales de dos maneras:

**Opción A: Variables de entorno (Recomendado)**
Exporta las variables en tu terminal antes de arrancar el servidor:
```bash
export CLOUDINARY_CLOUD_NAME="tu_nombre_de_cloud"
export CLOUDINARY_API_KEY="tu_api_key"
export CLOUDINARY_API_SECRET="tu_api_secret"
```

**Opción B: Editando `application.properties` (Para desarrollo local rápido)**
Abre el archivo `src/main/resources/application.properties` y reemplaza los valores de las últimas tres líneas:
```properties
# Reemplaza los valores de la derecha por tus credenciales reales
cloudinary.cloud-name=tu_nombre_de_cloud
cloudinary.api-key=tu_api_key
cloudinary.api-secret=tu_api_secret
```
> **⚠️ Advertencia:** Nunca subas el archivo `application.properties` con tus credenciales reales a repositorios públicos como GitHub.

---

## 🚀 Ejecución del Proyecto

Una vez que tengas la base de datos lista y las credenciales de Cloudinary configuradas, puedes levantar el proyecto.

Abre una terminal en la raíz de la carpeta `constructora-api` y ejecuta el siguiente comando:

**En Linux / macOS:**
```bash
./mvnw spring-boot:run
```

**En Windows:**
```cmd
mvnw.cmd spring-boot:run
```

Si todo es correcto, verás el logo de Spring en la consola y un mensaje indicando que la aplicación ha iniciado (usualmente en el puerto `8080`).

---

## 📁 Estructura Principal del Proyecto

- `src/main/java/.../config/`: Contiene configuraciones de Beans y seguridad (ej: `CloudinaryConfig.java`).
- `src/main/java/.../controller/`: Endpoints REST expuestos para ser consumidos por el Frontend (ej: `ArchivoController.java`).
- `src/main/java/.../service/`: Lógica de negocio y comunicación con APIs de terceros.
- `src/main/resources/db/migration/`: Scripts SQL gestionados por Flyway para la evolución de la base de datos.
- `src/main/resources/application.properties`: Archivo principal de configuración (Base de datos, JPA, Flyway, Variables).
