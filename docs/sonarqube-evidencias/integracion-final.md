# Integración final de `origin/develop`

Fecha: 2026-11-03 (America/Lima)
Rama: `fix/sonarqube-quality`

## Revisiones integradas

- Base local anterior a las correcciones: `8ade3af0b38cfb81842c84f16580125d5eaf3088`.
- HEAD protegido antes del merge: `6a6d67d6e475a278db56be1f512bada2ee6292a0`.
- Referencia de recuperación: `salvaguarda/sonarqube-pre-integracion-20261103`.
- Commit remoto integrado: `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`.
- Commit de merge: `dac299598226c80f250804332a0af8b33e737968`.
- Padres del merge: `6a6d67d6e475a278db56be1f512bada2ee6292a0` y `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`.

Los 70 commits previos permanecen alcanzables desde el merge. No se modificó `develop`, no se hizo
push, rebase, pull ni análisis SonarQube.

## Solapamientos y resolución

Se revisaron las siete rutas modificadas por ambas ramas:

| Ruta | Resolución |
|---|---|
| `.gitignore` | Se conservaron las exclusiones remotas de `.env` y la protección local de `application.properties`. |
| `SecurityConfig.java` | Se conservaron CSRF limitado a `/api/**`, roles constantes y autorización de pedidos para clientes. |
| `ArchivoController.java` | Se mantuvo la eliminación de `@CrossOrigin("*")` y se incorporó la carga PDF y el método común de subida. |
| `PublicContentController.java` | Se incorporaron paginación y pedidos; `UUID` vuelve a ser necesario y se conserva. |
| `Producto.java` | Único conflicto textual. Se combinaron `BusinessTime.now()`, stock, `@Version` y `forzarAgotadoSiSinStock()`. |
| `UnidadNegocio.java` | Se conservaron `BusinessTime`, la constante de validación y la nueva relación con `Catalogo`. |
| `application-test.properties` | Se conservaron Flyway `2.7` y `cors.allowed-origins`. |

`src/main/resources/application.properties` se respaldó fuera del repositorio, se retiró del índice y
se restauró con el mismo SHA-256 como archivo local ignorado. No se versionaron credenciales. Las tres
propiedades públicas que faltaban se añadieron a `application.example.properties`.

Las migraciones remotas se integraron sin editar su contenido: se recuperaron V13/V14 y se conservaron
los renombrados V8–V10 presentes en el commit analizado. Esto requiere comprobar el historial Flyway de
cualquier base ya desplegada antes de ejecutar una migración real, porque un renombrado de versiones ya
aplicadas puede requerir coordinación operativa.

## Archivos e incidencias recuperadas

Se recuperaron 11 de las 13 rutas ausentes que sí existen en el commit analizado:

1. `model/Catalogo.java`
2. `model/DireccionCliente.java`
3. `model/Pedido.java`
4. `model/SeguimientoPedido.java`
5. `repository/specification/ProductoCatalogSpecifications.java`
6. `service/implementation/PedidoService.java`
7. `db/migration/V10__create_resenas_producto.sql`
8. `db/migration/V13__create_catalogos.sql`
9. `db/migration/V14__create_pedidos_direcciones.sql`
10. `DireccionClienteServiceTest.java`
11. `PedidoServiceTest.java`

Las otras dos rutas tampoco existen en `73615cec…` y conservan su clasificación histórica:

- `model/ImagenProyecto.java`: 1 incidencia.
- `service/ProductoHijosIntegrationTests.java`: 4 incidencias.

Estado de las 49 incidencias focalizadas:

- **17 corregidas tras la integración:** 10 `java:S8688`, 2 `java:S1192`, 4 `java:S5778` y 1 `java:S8924`.
- **1 recomendación sin cambio:** `java:S3776` de `PedidoService`; la refactorización amplia mantiene el riesgo de regresión documentado.
- **26 falsos positivos PostgreSQL conservados:** `plsql:VarcharUsageCheck`; no se cambió `VARCHAR` por `VARCHAR2` ni se editaron migraciones.
- **5 históricas:** asociadas a los dos archivos que tampoco existen en la revisión remota.

Los 17 expedientes recuperados incluyen una actualización posterior al merge con el código corregido y
el resultado de validación. Los resúmenes P2 y P3 fueron ajustados a 90 y 26 correcciones,
respectivamente. Las 112 correcciones previas permanecen en el historial y en el código; con las 17
recuperadas quedan 129 correcciones documentadas.

Las 429 capturas PNG ANTES coinciden byte a byte con el manifiesto previo. Los 446 JSON de metadatos
mantienen exactamente el contenido previo al normalizar CRLF a LF; su diferencia binaria se debe
únicamente a la conversión de finales de línea realizada por Git en Windows. No hay diferencias de
contenido sin explicar. El manifiesto se regeneró sobre los bytes actuales y la versión anterior se
conserva en el respaldo externo.

## Validación realizada

Entorno confirmado: Java `21.0.6`.

Compilación:

```powershell
mvn -B -DskipTests compile
```

Resultado: **BUILD SUCCESS**, 324 clases principales compiladas con `release 21`.

Pruebas focalizadas:

```powershell
mvn -B clean -Dtest=BusinessTimeTest,DireccionClienteServiceTest,PedidoServiceTest,PublicPedidoApiIntegrationTest,ArchivoControllerTest,PublicContentControllerTest,PublicContentServiceTest,PublicProductApiIntegrationTest,SecurityConfigIntegrationTest,CorsConfigTest test
```

Resultado final: **BUILD SUCCESS**, 49 pruebas, 0 fallos, 0 errores y 0 omitidas. Flyway validó 23
migraciones y aplicó el esquema de prueba hasta `2.7` sobre H2.

La primera ejecución sin `clean` no llegó a validar integraciones porque `target/classes` conservaba el
antiguo nombre de una migración renombrada y Flyway detectó dos V8. Se eliminó únicamente `target` con
el ciclo estándar de Maven y la repetición focalizada fue exitosa.

`git diff --check` no encontró errores de espacios ni marcadores de conflicto. No se ejecutó la batería
completa de pruebas.

## Pendientes para SonarQube Cloud

1. No se ha ejecutado SonarQube ni se han generado capturas DESPUÉS.
2. Confirmar que la organización permite análisis de ramas para `fix/sonarqube-quality`.
3. Ejecutar el scanner con un token temporal no persistido y registrar SHA, fecha, versión y log.
4. Comparar las 409 incidencias por ID/regla y obtener evidencia real de presentes y ausentes.
5. Validar con el responsable de base de datos el historial real de Flyway antes de desplegar los renombrados V8–V10.
