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

## Pendientes y bloqueos

- Continúan pendientes las correcciones A/E seguras de pruebas y mantenibilidad.
- Las 11 incidencias `java:S6809` requieren comprobar fronteras transaccionales antes de refactorizar; no se consideran aceptadas.
- `java:S4502` permanece como alerta justificada por JWT Bearer stateless sin cookies; no se cambia seguridad sin validación adicional.
- No se modifican V11 ni otras migraciones Flyway aplicadas.
