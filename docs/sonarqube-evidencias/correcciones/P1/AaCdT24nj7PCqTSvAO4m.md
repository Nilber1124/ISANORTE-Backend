# AaCdT24nj7PCqTSvAO4m — auto-llamada en unidad de negocio

- **Regla / severidad:** `java:S6809` / CRITICAL; `UnidadNegocioService.java:135`.
- **Alerta:** llamada directa a método transaccional en el mismo bean.
- **Evidencia:** [captura ANTES](../../antes/AaCdT24nj7PCqTSvAO4m.png) y [metadatos](../../antes/AaCdT24nj7PCqTSvAO4m.json).

La alerta histórica corresponde a `createResponse(...) -> create(...)`. El punto de entrada posee `@Transactional(REQUIRED)` y cubre validación de empresa/destacado, relación y guardado; el método interno declara la misma política.

**Código ANTES/DESPUÉS (sin cambio):** `return unidadNegocioMapper.toResponse(create(request));` dentro de `@Transactional createResponse(...)`.

`TransactionalSelfInvocationAssessmentTest` lo verifica y las integraciones de contenido pasaron. Autoinyectar el servicio no aportaría una garantía adicional en el diseño actual.

**Estado:** riesgo aceptable propuesto, pendiente de aprobación. Residual: separar en otro bean ante una política transaccional diferente.
