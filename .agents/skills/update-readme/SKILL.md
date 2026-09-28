---
name: update-readme
description: >-
  Mantiene actualizado el README.md del proyecto constructora-api cada vez que se agreguen,
  modifiquen o eliminen configuraciones, variables de entorno, dependencias, endpoints clave o arquitectura.
---

# Mantenimiento y Actualización Continua del README.md

Esta skill define las pautas y el procedimiento sistemático para mantener el archivo `README.md` de **constructora-api** (Java 21, Spring Boot, PostgreSQL, Flyway, Cloudinary, Spring Security/JWT) siempre sincronizado con los cambios del código fuente.

---

## 1. Cuándo Activar Esta Skill (Gatillos / Triggers)

Debe ejecutarse una revisión y actualización del `README.md` **siempre** que ocurra cualquiera de los siguientes eventos:

1. **Variables de Entorno y Propiedades (`application.properties`)**:
   - Se introduce una nueva propiedad de configuración o variable de entorno (`${VARIABLE:default}`).
   - Se modifican valores por defecto, formatos requeridos o nombres de variables.
   - Se introducen validaciones restrictivas (longitud mínima de claves, formatos de email, tokens, puertos).
2. **Seguridad y Autenticación**:
   - Cambios en el mecanismo de autenticación (JWT, roles, permisos, prefijos `ROLE_`).
   - Modificación de endpoints públicos (`permitAll()`) o protegidos (`hasAuthority(...)`).
   - Nuevos flujos de login, refresh tokens o políticas de expiración.
3. **Servicios Externos o Integraciones**:
   - Nuevos proveedores o servicios de terceros (AWS S3, Cloudinary, pasarelas de pago, scrapers, etc.).
   - Requisitos de credenciales o cuentas externas para levantar el proyecto en local.
4. **Requisitos de Sistema y Dependencias**:
   - Cambio de versión de Java (ej. de JDK 17 a JDK 21).
   - Cambio de motor de base de datos o versiones mínimas recomendadas.
   - Nuevas herramientas CLI o dependencias nativas requeridas.
5. **Nuevos Módulos y Arquitectura**:
   - Creación de nuevos paquetes principales (`security/`, `event/`, `job/`, etc.).
   - Nuevos controladores o flujos funcionales relevantes para el consumo del frontend.
6. **Scripts de Base de Datos y Migraciones**:
   - Nuevas tablas de catálogo inicial o pasos adicionales requeridos en el setup de PostgreSQL / Flyway.

---

## 2. Secciones Clave a Sincronizar en el README.md

### A. Requisitos Previos (`## 🛠️ Requisitos Previos`)
* Validar que la versión del JDK coincida con `<java.version>` de `pom.xml`.
* Verificar las versiones mínimas de PostgreSQL y herramientas del entorno.

### B. Configuración de Base de Datos y Servicios (`## ⚙️ Configuración Inicial`)
* Instrucciones claras para crear la base de datos y usuario local en PostgreSQL.
* Credenciales de Cloudinary y otros servicios externos, documentando tanto la opción por variables de entorno como en `application.properties`.

### C. Seguridad, Variables y Autenticación (`## 🔐 Autenticación y Seguridad`)
* Mantener una **tabla de variables** actualizada con:
  - Nombre de la variable de entorno.
  - Propiedad correspondiente en `application.properties`.
  - Restricciones (longitud mínima, tipo de dato, formato regex).
  - Descripción clara de su propósito y si es obligatoria.
* Documentar advertencias de seguridad (`> **⚠️ Importante:**`).

### D. Guía de Consumo de Endpoints Clave
* Documentar cómo autenticarse (`POST /api/auth/login`) con ejemplos de payload JSON y respuesta esperada.
* Explicar cómo adjuntar el token JWT en las cabeceras (`Authorization: Bearer <token>`).
* Listar qué rutas son públicas (`/api/publico/**`) y cuáles son privadas (`/api/**`).

### E. Comandos de Ejecución y Pruebas
* Comandos actualizados para Linux/macOS (`./mvnw spring-boot:run`) y Windows (`mvnw.cmd spring-boot:run`).
* Comandos para ejecutar la suite de pruebas unitarias (`./mvnw test`).

### F. Estructura de Directorios (`## 📁 Estructura Principal del Proyecto`)
* Si se agregaron nuevos paquetes estructurales en `src/main/java/`, agregarlos con una breve descripción de su responsabilidad.

---

## 3. Pautas de Redacción y Estilo

1. **Claridad y Concisión**: Redactar en español técnico, claro y directo.
2. **Ejemplos Copiables y Funcionales**: Todos los bloques de código (`sql`, `bash`, `properties`, `json`, `http`) deben ser directamente ejecutables sin errores sintácticos.
3. **Cuidado con Secretos y Seguridad**:
   - NUNCA incluir claves reales, contraseñas de producción o credenciales privadas en el README.
   - Usar siempre valores ilustrativos (`tu_clave_aqui`, `admin@ejemplo.com`, etc.).
   - Incluir advertencias explícitas sobre no commitear credenciales sensibles a Git.
4. **Resaltado y Formato**:
   - Usar bloques de alerta (`> [!NOTE]`, `> [!IMPORTANT]`, `> [!WARNING]`).
   - Usar tablas para comparar variables y sus restricciones.

---

## 4. Procedimiento de Ejecución Paso a Paso

1. **Inspeccionar los cambios recientes**:
   - Ejecutar `git diff` o revisar los archivos modificados (`application.properties`, `pom.xml`, controllers, security configs).
2. **Determinar el impacto en el onboarding de un nuevo desarrollador**:
   - ¿Qué necesita configurar en su máquina para que el proyecto compile y arranque sin errores?
3. **Actualizar el archivo `README.md`**:
   - Editar o añadir las secciones pertinentes manteniendo la coherencia y el formato del documento.
4. **Verificar que no haya enlaces o rutas rotas**:
   - Confirmar que los nombres de propiedades y variables de entorno coincidan al 100% con los declarados en el código.
