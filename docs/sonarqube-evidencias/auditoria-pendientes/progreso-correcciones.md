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

## Pendientes y bloqueos

- `AaGdRnta97lfKRteM0Yu` (`PedidoService`, S3776): el método controla stock, variantes, dirección y transacción con reintentos; requiere pruebas de caracterización concurrente antes de dividirlo.
- `AaES7WZe-6tQcR7a5P5P` (`CotizacionService`, S3776): calcula precios, variantes y subtotales; requiere matriz funcional de precios incompletos antes del refactor.
- `AaEN8OgrTwfNugJobk2m` (`SeccionLandingService`, S3776): valida compatibilidad, enlaces y límites de acciones; requiere pruebas específicas de todas las combinaciones.
- `AaGdRnt197lfKRteM0Yx` (`IPublicContentService`, S107): cambiar el contrato de catálogo afecta controlador, implementación y consumidores; se recomienda un objeto de criterios después de verificar compatibilidad API.
- Las 11 incidencias `java:S6809` (`AaEN8OkrTwfNugJobk2u`, `AaEN8OmWTwfNugJobk2v`, `AaCdT25Pj7PCqTSvAO4q`, `AaCdT247j7PCqTSvAO4o`, `AaCdT24dj7PCqTSvAO4k`, `AaCdT24Bj7PCqTSvAO4j`, `AaCdT23wj7PCqTSvAO4h`, `AaCdT25Fj7PCqTSvAO4p`, `AaCdT24xj7PCqTSvAO4n`, `AaCdT25Xj7PCqTSvAO4r`, `AaCdT24nj7PCqTSvAO4m`) requieren comprobar propagación y rollback antes de extraer colaboradores; no se consideran aceptadas.
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
