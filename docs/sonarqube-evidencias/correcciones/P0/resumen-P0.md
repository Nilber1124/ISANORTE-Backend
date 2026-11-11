# Resumen de correcciones P0 — Seguridad

Fecha de ejecución: 2026-11-02  
Rama: `fix/sonarqube-quality`  
Análisis original: `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`

## Resultado ejecutivo

Se revisaron exclusivamente las ocho incidencias P0. Se corrigieron la desactivación global de CSRF y la política CORS permisiva; dos límites multipart se conservaron como falsos positivos justificados; cuatro detecciones de secretos ya estaban cerradas y ausentes del código versionado, pero siguen requiriendo prueba externa de revocación/rotación. No se modificaron estados en SonarQube ni se generaron capturas DESPUÉS.

| ID | Regla | Severidad | Resultado P0 |
|---|---|---:|---|
| [AaGdRnvH97lfKRteM0Zb](AaGdRnvH97lfKRteM0Zb.md) | `java:S5693` | MAJOR | Falso positivo justificado: archivo máximo de 10 MiB en carga administrativa |
| [AaGdRnvH97lfKRteM0Zc](AaGdRnvH97lfKRteM0Zc.md) | `java:S5693` | MAJOR | Falso positivo justificado: petición multipart total máxima de 15 MiB |
| [AaES7WjO-6tQcR7a5P5V](AaES7WjO-6tQcR7a5P5V.md) | `java:S6437` | BLOCKER | Secreto ausente del código; rotación externa no confirmada |
| [AaES7WjO-6tQcR7a5P5W](AaES7WjO-6tQcR7a5P5W.md) | `secrets:S6703` | BLOCKER | Contraseña ausente del código; rotación externa no confirmada |
| [AaEN8OFwTwfNugJobk18](AaEN8OFwTwfNugJobk18.md) | `java:S4502` | CRITICAL | Corregida: exclusión CSRF limitada a `/api/**` stateless |
| [AaEN8OpSTwfNugJobk21](AaEN8OpSTwfNugJobk21.md) | `java:S5122` | MAJOR | Corregida/reforzada: CORS único, exacto y sin credenciales |
| [AaCdT29Lj7PCqTSvAO5c](AaCdT29Lj7PCqTSvAO5c.md) | `java:S6437` | BLOCKER | Secreto histórico ausente; rotación externa no confirmada |
| [AaCdT29Lj7PCqTSvAO5d](AaCdT29Lj7PCqTSvAO5d.md) | `secrets:S6703` | BLOCKER | Contraseña histórica ausente; rotación externa no confirmada |

Las cuatro reglas de secretos representan dos pares de detecciones (`S6437` + `S6703`) sobre exposiciones históricas. No se reproduce ningún valor sensible.

## Cambios realizados

- `SecurityConfig.java`: CSRF deja de estar desactivado globalmente; solo se ignora para la API JWT stateless.
- `ArchivoController.java`: se elimina `@CrossOrigin(origins = "*")`.
- `CorsConfig.java`: una única política, orígenes exactos validados, cabeceras explícitas y credenciales CORS deshabilitadas.
- `.gitignore`: protege `src/main/resources/application.properties` local.
- `application.example.properties`: plantilla sin secretos ni fallbacks sensibles.
- `README.md`: configuración segura, CORS exacto y obligación de rotar secretos expuestos.
- `CorsConfigTest.java`, `SecurityConfigIntegrationTest.java` y `application-test.properties`: cobertura unitaria e integración de CORS y CSRF.

No se modificaron migraciones Flyway, la rama `develop`, ni incidencias P1–P4. Las evidencias de `docs/sonarqube-evidencias/antes/` permanecen sin sobrescritura.

## Pruebas ejecutadas

Comando final:

```powershell
& '<MAVEN_CACHE>/bin/mvn.cmd' -B '-Dmaven.repo.local=<M2_CACHE>' '-Dtest=CorsConfigTest,SecurityConfigIntegrationTest' test
```

Resultado real: **BUILD SUCCESS**, 19 pruebas ejecutadas, 0 fallos, 0 errores y 0 omitidas. La compilación principal también terminó correctamente durante el ciclo de pruebas.

Cobertura añadida:

- aceptación y normalización de orígenes exactos;
- rechazo de configuración vacía, comodines, esquema no HTTP(S) y rutas;
- preflight para origen autorizado;
- rechazo 403 de origen no autorizado;
- POST a API sin CSRF permitido para continuar a validación funcional;
- POST no API sin CSRF rechazado con 403.

Incidencias de ejecución observadas y resueltas:

- `mvnw.cmd` local no pudo iniciar Maven por un error interno del wrapper en Windows;
- dentro del sandbox, `javac` recibió `AccessDeniedException` al cerrar un JAR del caché Maven;
- se utilizó el Maven ya almacenado en caché y se ejecutó fuera del sandbox, sin reinstalar dependencias;
- la primera integración reveló que la configuración local heredaba un origen comodín; el perfil `test` se aisló con `cors.allowed-origins=http://localhost:4200` y la repetición fue exitosa.

## Riesgos y acciones externas pendientes

1. Rotar o revocar en PostgreSQL las credenciales históricamente expuestas y actualizar los secretos de todos los despliegues. El cierre de SonarQube no prueba esta acción.
2. Revisar y rotar cualquier otro proveedor afectado si la investigación del historial identifica credenciales adicionales; conservar evidencia operativa sin incluir el valor.
3. Mantener límites equivalentes en proxy/gateway y monitorización de cargas multipart. Reevaluar los falsos positivos si cambia la exposición o autorización de los endpoints.
4. Ejecutar un nuevo análisis de SonarQube Cloud y obtener capturas reales DESPUÉS. Hasta entonces no se afirma que SonarQube haya cerrado las correcciones nuevas.

## Estado final

- Incidencias P0 examinadas: **8**.
- Corregidas/reforzadas en código: **2**.
- Falsos positivos justificados: **2**.
- Detecciones históricas ya cerradas y ausentes del código: **4**.
- Incidencias con intervención externa pendiente: **4** (dos pares de reglas sobre secretos).
- Pruebas exitosas: **19/19**.
- Capturas ANTES alteradas: **0**.
- Capturas DESPUÉS inventadas o generadas sin análisis: **0**.
