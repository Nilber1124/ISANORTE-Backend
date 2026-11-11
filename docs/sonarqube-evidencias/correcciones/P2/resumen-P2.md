# Resumen P2 — Corrección y documentación rápida

Rama: `fix/sonarqube-quality`  
Análisis original: `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`

## Totales

- Incidencias analizadas y documentadas: **104**.
- Corregidas: **90** (80 iniciales y 10 recuperadas/corregidas tras integrar `73615cec…`).
- Falsos positivos justificados: **0**.
- Recomendaciones de mantenibilidad sin cambio: **11**.
- Riesgos pendientes de aceptación: **0**.
- Históricas o ya resueltas: **3**.

## Cambios agrupados

- **S8688:** 76 llamadas activas migradas a `BusinessTime` con zona explícita `America/Lima`; 12 incidencias ya no estaban presentes.
- **S3358:** 2 ternarios anidados sustituidos por decisiones con nombre.
- **S1141:** 1 try anidado extraído a un método de resolución de redirección.
- **S5843:** 1 regex compleja dividida en tres patrones precompilados.
- **S3776/S107:** 11 recomendaciones conservadas sin cambios de alto riesgo, incluida la S3776 recuperada de `PedidoService`.

Tras integrar la revisión analizada se recuperaron `Catalogo`, `DireccionCliente`, `Pedido`,
`SeguimientoPedido` y `PedidoService`. Diez llamadas temporales S8688 pasaron de históricas a
corregidas mediante `BusinessTime`; la recomendación S3776 de `PedidoService` permanece sin cambio.

Archivos de producción modificados: 38 archivos con llamadas temporales, `BusinessTime.java`, `ComparacionPrecioService.java`, `SeccionLandingService.java`, `ScrapingPrecioService.java` y `PrecioHtmlExtractor.java`. No se modificaron migraciones, contratos SQL ni evidencias ANTES.

## Validación mínima realizada

Comando focalizado: `mvn -Dtest=BusinessTimeTest,ComparacionPrecioServiceTest,PrecioHtmlExtractorTest,ScrapingPrecioServiceTest test`.

Resultado real: **BUILD SUCCESS**, 298 clases principales compiladas, 36 fuentes de prueba compiladas y **37 pruebas** ejecutadas sin fallos ni errores.

## Limitaciones

No se ejecutaron pruebas de integración, batería completa, GitHub Actions ni SonarQube. Por ello no se afirma cierre en Sonar ni validación funcional integral. Las capturas DESPUÉS se obtendrán tras un análisis real posterior.
