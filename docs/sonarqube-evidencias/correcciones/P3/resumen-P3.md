# Resumen P3 — Corrección y documentación eficiente

Rama: `fix/sonarqube-quality`  
Análisis original: `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`

## Totales

- Incidencias identificadas y documentadas: **54**.
- Corregidas: **26** (19 iniciales y 7 recuperadas/corregidas tras integrar `73615cec…`).
- Justificadas como recomendaciones sin cambio: **18**.
- Falsos positivos sustentados: **2**.
- Históricas o ya resueltas: **8**.
- Riesgos pendientes de aprobación: **0**.

## Cambios agrupados

Se introdujeron constantes para literales repetidos, se limpiaron imports, se renombraron parámetros genéricos, se separaron declaraciones múltiples, `SimilitudProductoService` pasó a `@Service` y los callbacks idénticos de `ConfiguracionSitio` delegan en un método común. No se modificaron migraciones, endpoints, contratos de base de datos ni evidencias ANTES.

Después de integrar la revisión analizada se recuperaron `ProductoCatalogSpecifications`,
`DireccionClienteServiceTest` y `PedidoServiceTest`. Se corrigieron dos literales duplicados y cinco
alertas de mantenibilidad en pruebas. Las cuatro alertas del archivo todavía inexistente
`ProductoHijosIntegrationTests.java` permanecen históricas.

## Validación mínima

Comando: `mvn -Dtest=PrecioHtmlExtractorTest,SimilitudProductoServiceTest,ConfiguracionSitioRegexTest test`.

Resultado: **BUILD SUCCESS**; 298 clases principales y 36 fuentes de prueba compiladas; **27 pruebas**, 0 fallos y 0 errores.

## Limitaciones

No se ejecutaron integraciones, batería completa, GitHub Actions ni SonarQube. Las recomendaciones sobre lambdas de tests, parámetros contractuales y control de flujo quedan para una fase con sus pruebas específicas. No se afirma cierre en Sonar ni se inventan capturas DESPUÉS.
