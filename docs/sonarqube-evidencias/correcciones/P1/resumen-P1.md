# Resumen P1 — Fiabilidad, transacciones y expresiones regulares

Fecha: 2026-11-02  
Rama: `fix/sonarqube-quality`  
Análisis original: `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`

## Resultado

Se analizaron individualmente las 22 incidencias P1. Once eran defectos reales y recibieron una corrección mínima; once alertas `java:S6809` describen auto-llamadas reales, pero la pérdida de la segunda intercepción no cambia hoy la semántica porque cada método de entrada ya está proxificado con una transacción equivalente. Estas últimas se registran como **riesgo aceptable propuesto, pendiente de aprobación**, no como falso positivo definitivo.

| ID | Regla | Clasificación | Resultado |
|---|---|---|---|
| [AaEN8OBpTwfNugJobk14](AaEN8OBpTwfNugJobk14.md) | S8989 | Histórica / defecto real | `rollbackFor=IOException` explícito |
| [AaEN8OE6TwfNugJobk15](AaEN8OE6TwfNugJobk15.md) | S8786 | Error real | Validación de correo lineal |
| [AaEN8OkrTwfNugJobk2u](AaEN8OkrTwfNugJobk2u.md) | S6809 | Riesgo pendiente | Transacción externa equivalente |
| [AaEN8OnsTwfNugJobk20](AaEN8OnsTwfNugJobk20.md) | S8786 | Error real | Limpieza determinista de título |
| [AaEN8OddTwfNugJobk2S](AaEN8OddTwfNugJobk2S.md) | S6218 | Error real | Semántica y copia defensiva de `Pagina` |
| [AaEN8OduTwfNugJobk2X](AaEN8OduTwfNugJobk2X.md) | S5866 | Error real | Case folding Unicode |
| [AaEN8OduTwfNugJobk2c](AaEN8OduTwfNugJobk2c.md) | S8786 | Error real | Búsqueda de estilo lineal |
| [AaEN8Od6TwfNugJobk2e](AaEN8Od6TwfNugJobk2e.md) | S6218 | Error real | Semántica y copia defensiva de `Destino` |
| [AaEN8OmWTwfNugJobk2v](AaEN8OmWTwfNugJobk2v.md) | S6809 | Riesgo pendiente | Frontera read-only equivalente |
| [AaEN8OZpTwfNugJobk2Q](AaEN8OZpTwfNugJobk2Q.md) | S5998 | Error real | Cuantificadores posesivos en alta |
| [AaEN8OadTwfNugJobk2R](AaEN8OadTwfNugJobk2R.md) | S5998 | Error real | Cuantificadores posesivos en edición |
| [AaEN8OTnTwfNugJobk2G](AaEN8OTnTwfNugJobk2G.md) | S5850 | Error real | Recorte explícito sin alternancia |
| [AaEN8OTnTwfNugJobk2H](AaEN8OTnTwfNugJobk2H.md) | S8786 | Error real | Recorte lineal sin backtracking |
| [AaCdT25Pj7PCqTSvAO4q](AaCdT25Pj7PCqTSvAO4q.md) | S6809 | Riesgo pendiente | Categoría: `REQUIRED` externo |
| [AaCdT247j7PCqTSvAO4o](AaCdT247j7PCqTSvAO4o.md) | S6809 | Riesgo pendiente | Configuración: `REQUIRED` externo |
| [AaCdT24dj7PCqTSvAO4k](AaCdT24dj7PCqTSvAO4k.md) | S6809 | Riesgo pendiente | Cotización: `REQUIRED` externo |
| [AaCdT24Bj7PCqTSvAO4j](AaCdT24Bj7PCqTSvAO4j.md) | S6809 | Riesgo pendiente | Empresa: `REQUIRED` externo |
| [AaCdT23wj7PCqTSvAO4h](AaCdT23wj7PCqTSvAO4h.md) | S6809 | Riesgo pendiente | Producto: `REQUIRED` externo |
| [AaCdT25Fj7PCqTSvAO4p](AaCdT25Fj7PCqTSvAO4p.md) | S6809 | Riesgo pendiente | Proyecto: `REQUIRED` externo |
| [AaCdT24xj7PCqTSvAO4n](AaCdT24xj7PCqTSvAO4n.md) | S6809 | Riesgo pendiente | Sección: `REQUIRED` externo |
| [AaCdT25Xj7PCqTSvAO4r](AaCdT25Xj7PCqTSvAO4r.md) | S6809 | Riesgo pendiente | Servicio: `REQUIRED` externo |
| [AaCdT24nj7PCqTSvAO4m](AaCdT24nj7PCqTSvAO4m.md) | S6809 | Riesgo pendiente | Unidad: `REQUIRED` externo |

## Totales por disposición

- Corregidas o reforzadas: **11**.
- Falsos positivos declarados: **0**.
- Riesgos aceptables propuestos, pendientes de aprobación: **11**.
- Históricas/ya cerradas en Sonar: **1**, incluida entre las 11 corregidas (`AaEN8OBpTwfNugJobk14`).
- Pendientes por bloqueo técnico o intervención externa: **0**.

## Cambios de producción P1

- `DemoProductSeeder.java`: rollback explícito para `IOException`.
- `AdminInitializer.java`: validación lineal del correo.
- `AbstractHtmlCompetidorProvider.java`: eliminación de regex superlineal.
- `PrecioHtmlExtractor.java`: flags Unicode y patrón de estilo precompilado sin comodines globales.
- `PaginaExternaClient.java` y `UrlExternaPolicy.java`: igualdad por contenido y copias defensivas de arrays.
- `ConfiguracionSitioRequest.java` y `ConfiguracionSitioUpdateRequest.java`: cuantificadores posesivos.
- `ConfiguracionSitio.java`: recorte lineal de guiones extremos.

No se modificaron los nueve servicios señalados por S6809, migraciones Flyway, esquemas PostgreSQL, endpoints ni configuración global. P0 permanece conservado.

## Pruebas

### Unitarias y de regresión focalizada

```powershell
mvn -Dtest=DemoProductSeederTest,DemoProductSeederTransactionTest,AdminInitializerTest,CompetidorProductoProviderTest,PrecioHtmlExtractorTest,PaginaExternaClientTest,UrlExternaPolicyTest,ConfiguracionSitioRegexTest,TransactionalSelfInvocationAssessmentTest test
```

Resultado final: **83 pruebas, 0 fallos, 0 errores, BUILD SUCCESS**. Incluye entradas adversarias bajo timeout, Unicode, arrays mutables/nulos, rollback y verificación de las once fronteras transaccionales.

La primera ejecución detectó dos errores en pruebas nuevas —un patrón posesivo de correo demasiado restrictivo y un stub no consumido—; ambos se corrigieron. No se ocultaron ni se declararon exitosos.

### Integración y regresión P0

```powershell
mvn -Dtest=DynamicContentApiIntegrationTest,PublicCotizacionApiIntegrationTest,PublicProductApiIntegrationTest,SecurityConfigIntegrationTest,CorsConfigTest test
```

Resultado: **51 pruebas, 0 fallos, 0 errores, BUILD SUCCESS**. Flyway validó/aplicó las migraciones de prueba sobre H2 sin modificar ninguna migración existente.

Total exitoso en las dos ejecuciones finales: **134 pruebas**. El proyecto compiló sus **297 clases principales** y **35 fuentes de prueba** correctamente.

## Riesgo residual transaccional

La justificación de S6809 solo es válida mientras método externo e interno mantengan `Propagation.REQUIRED`, la misma naturaleza read/write y ausencia de aislamiento, timeout o rollback rules divergentes. `TransactionalSelfInvocationAssessmentTest` fallará si se elimina o cambia la anotación externa, pero un cambio de política interna debe acompañarse de extracción a un bean colaborador y una prueba de rollback real. La aceptación definitiva corresponde al responsable/profesor.

## Evidencias

Las 21 capturas P1 disponibles en `docs/sonarqube-evidencias/antes/` se conservaron. La incidencia histórica restante mantiene sus metadatos. No se generaron ni inventaron capturas DESPUÉS; se obtendrán tras un nuevo análisis de SonarQube Cloud.
