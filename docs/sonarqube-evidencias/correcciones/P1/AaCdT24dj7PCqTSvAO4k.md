# AaCdT24dj7PCqTSvAO4k — auto-llamada en creación de cotización

- **Regla / severidad:** `java:S6809` / CRITICAL; `CotizacionService.java:154`.
- **Alerta:** llamada directa a método transaccional en el mismo bean.
- **Evidencia:** [captura ANTES](../../antes/AaCdT24dj7PCqTSvAO4k.png) y [metadatos](../../antes/AaCdT24dj7PCqTSvAO4k.json).

`createResponse(...)` y `create(...)` usan `@Transactional` con `REQUIRED`. La transacción externa cubre cálculo de detalles, asociaciones y `save`; perder la segunda interceptación no cambia rollback ni commit.

**Código ANTES/DESPUÉS (sin cambio):** `return cotizacionMapper.toResponse(create(request));` dentro de `@Transactional createResponse(...)`.

La prueba de evaluación valida la frontera y `PublicCotizacionApiIntegrationTest` pasó 8 escenarios. Una refactorización solo para eliminar la alerta añadiría duplicación o autoinyección.

**Estado:** riesgo aceptable propuesto, pendiente de aprobación. Residual: cambios futuros de propagación/aislamiento exigirán colaborador independiente.
