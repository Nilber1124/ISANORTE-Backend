# Progreso de correcciones SonarQube — segunda ronda

Los estados `FIXED` solo se registran cuando el usuario confirma un análisis real de SonarQube Cloud.

| Lote | IDs | Solución y archivos | Pruebas | Commit / push | Estado SonarQube |
| --- | --- | --- | --- | --- | --- |
| 1 | `AaGg3z0kPtLvwDc7UMX8`, `AaGg3z0kPtLvwDc7UMX9`, `AaGg3z0gPtLvwDc7UMX6`, `AaGg3z0gPtLvwDc7UMX7` | Límites multipart seguros y configurables; perfiles, README y prueba | `MultipartConfigurationTest`: 2/2 | `5751111`, publicado en `develop` | FIXED, confirmado por el usuario |
| 2 | `AaGdRntQ97lfKRteM0Yt` | Zona temporal explícita mediante `BusinessTime` en `ProyectoService` | `ProyectoServiceTest`: 3/3 | `71bfc0e`, publicado en `develop` | FIXED, confirmado por el usuario |
| 3 | `AaGg3zy9PtLvwDc7UMX4`, `AaGg3zywPtLvwDc7UMX3`, `AaGg3zz4PtLvwDc7UMX5` | Eliminar imports `OffsetDateTime` no usados | Compilación Java 21: `BUILD SUCCESS` | Este commit; push a `develop` | Pendiente de verificar |
| 4 | `AaGdRnvV97lfKRteM0Ze`, `AaGdRnvQ97lfKRteM0Zd`, `AaEN8OsTTwfNugJobk5v`, `AaEN8Or8TwfNugJobk5q`, `AaEN8Or8TwfNugJobk5r`, `AaEN8OsHTwfNugJobk5s`, `AaGg3z0rPtLvwDc7UMX-`, `AaEN8OsHTwfNugJobk5t`, `AaGg3z0rPtLvwDc7UMX_`, `AaEN8OsHTwfNugJobk5u`, `AaEN8OtNTwfNugJobk5z`, `AaEN8OtNTwfNugJobk50`, `AaEN8OtNTwfNugJobk51`, `AaEN8Os5TwfNugJobk5w`, `AaEN8Os5TwfNugJobk5x`, `AaEN8Os5TwfNugJobk5y` | Aislar una sola invocación susceptible de lanzar en lambdas de pruebas | 7 clases, 50/50 pruebas | Este commit; push a `develop` | Pendiente de verificar |
| 5 | `AaEN8OthTwfNugJobk52` | Retirar parámetros sin uso del helper `projectImage` | `DynamicContentApiIntegrationTest` | Este commit; push a `develop` | Pendiente de verificar |
| 6 | `AaEN8OttTwfNugJobk53` | Consolidar verificaciones repetidas con una aserción AssertJ por producto | `DemoProductSeederTest` | Este commit; push a `develop` | Pendiente de verificar |
| 7 | `AaGg3zyhPtLvwDc7UMX0`, `AaGg3zymPtLvwDc7UMX1` | Usar record patterns Java 21 conservando igualdad profunda de arrays | `PaginaExternaClientTest`, `UrlExternaPolicyTest` | Este commit; push a `develop` | Pendiente de verificar |
| 8 | `AaGg3zysPtLvwDc7UMX2`, `AaEN8OduTwfNugJobk2Y`, `AaEN8OduTwfNugJobk2T`, `AaEN8OduTwfNugJobk2Z`, `AaEN8OduTwfNugJobk2b` | Separar extracción por fuente, referencias JSON-LD, normalización y patrones de precios antiguos | `PrecioHtmlExtractorTest` | Este commit; push a `develop` | Pendiente de verificar |
| 9 | `AaEN8Od6TwfNugJobk2f`, `AaEN8Od6TwfNugJobk2g` | Separar validación URI/DNS y clasificación IPv4/IPv6 | `UrlExternaPolicyTest` | Este commit; push a `develop` | Pendiente de verificar |
| 10 | `AaGdRnsl97lfKRteM0Ys` | Eliminar el helper de diez parámetros y construir el record de datos directamente | `DemoHomeIsadecorSeederTest` | Este commit; push a `develop` | Pendiente de verificar |
| 11 | `AaGjwrYsQjmWlr9u4hwf`, `AaGjvYmKad7z90yEgGjf`, `AaGjvYmKad7z90yEgGjh`, `AaGjvYmKad7z90yEgGjg`, `AaEN8OtNTwfNugJobk50`, `AaEN8OtNTwfNugJobk51` | Corregir regresiones del análisis `f497069`: nulabilidad, literales, complejidad IPv4/separadores y lambdas | 3 clases, 61/61 pruebas | Este commit; push a `develop` | Pendiente de verificar |
| 12 | `AaEN8OgrTwfNugJobk2m` | Separar validación de enlaces landing en predicados por esquema | `DynamicContentApiIntegrationTest`: 22/22 | Este commit; push a `develop` | Pendiente de verificar |
| 13 | `AaGdRnt197lfKRteM0Yx` | Encapsular criterios del catálogo en `PublicProductCatalogQuery` sin cambiar el contrato HTTP | `PublicContentControllerTest`, `PublicContentServiceTest`: 6/6 | Este commit; push a `develop` | Pendiente de verificar |
| 14 | `AaGdRnta97lfKRteM0Yu`, `AaES7WZe-6tQcR7a5P5P`, `AaEN8OkrTwfNugJobk2u`, `AaCdT24dj7PCqTSvAO4k` | Extraer construcción de detalles de pedidos/cotizaciones y un núcleo transaccional sin autoinvocación | `PedidoServiceTest`, `PublicPedidoApiIntegrationTest`, `PublicCotizacionApiIntegrationTest`: 18/18 | Este commit; push a `develop` | Pendiente de verificar |
| 15 | `AaEN8OmWTwfNugJobk2v`, `AaCdT25Pj7PCqTSvAO4q`, `AaCdT247j7PCqTSvAO4o`, `AaCdT24Bj7PCqTSvAO4j`, `AaCdT23wj7PCqTSvAO4h`, `AaCdT25Fj7PCqTSvAO4p`, `AaCdT24xj7PCqTSvAO4n`, `AaCdT25Xj7PCqTSvAO4r`, `AaCdT24nj7PCqTSvAO4m` | Sustituir autoinvocaciones por núcleos privados dentro de las mismas fronteras transaccionales públicas | 6 clases de prueba, 51/51 | Este commit; push a `develop` | Pendiente de verificar |

## Pendientes y bloqueos

- `java:S4502` permanece como alerta justificada por JWT Bearer stateless sin cookies; no se cambia seguridad sin validación adicional.
- No se modifican V11 ni otras migraciones Flyway aplicadas.

## Cierre de la ronda automática

- Corregidas y publicadas en los lotes 1–10: **36 incidencias**.
- Pendientes por riesgo técnico: **4 incidencias A** y **11 incidencias F**.
- Falsos positivos y riesgos documentados C/D: sin cambios de código ni de estado remoto.
- Todas las correcciones publicadas quedan pendientes de confirmación en un análisis SonarQube, salvo los lotes 1 y 2 confirmados por el usuario.

## Análisis actualizado

- Revisión analizada: `f497069f8b438e886f66be640fadc9f6b10a3cb7`.
- Fecha SonarQube: `2026-11-03T23:36:43+0000`.
- Incidencias activas al iniciar la nueva ronda: **247**.

## Verificación definitiva del análisis `9a59e8a`

- Revisión analizada por SonarQube Cloud: `9a59e8a198e09b10d01753f95dbdf84aab7d71e1`.
- Fecha del análisis: `2026-11-04T05:57:33+0000`.
- Incidencias activas: **226**; las **21** tratadas desde el análisis de 247 incidencias ya no aparecen activas.
- Falsos positivos técnicamente sustentados que permanecen OPEN: **173**.
  - **161** `plsql:VarcharUsageCheck`: recomienda `VARCHAR2`, tipo de Oracle que no es nativo de PostgreSQL.
  - **7** `plsql:NullComparison`: interpreta comparaciones PostgreSQL contra cadena vacía (`<> ''`) como comparaciones con `NULL`; el SQL ya usa `IS NULL` correctamente donde corresponde.
  - **5** Java: `S4502`, `S112`, `S1130`, `S1135` y `S1172`, con sustento individual en la auditoría.
- Recomendaciones conservadas: **53** `plsql:S1192` en migraciones Flyway históricas. No son errores funcionales demostrados y alterar migraciones aplicadas invalidaría sus checksums.
- Incidencias reales pendientes de modificar en código: **0**.
- Quality Gate: **ERROR** porque los falsos positivos `NullComparison` mantienen la calificación de fiabilidad y `S4502` mantiene la calificación de seguridad mientras continúen OPEN.
- No se cambiaron estados remotos: el entorno no dispone de `SONAR_TOKEN` ni de una sesión autenticada autorizada. No se modificaron reglas, perfiles ni umbrales para ocultar alertas.
