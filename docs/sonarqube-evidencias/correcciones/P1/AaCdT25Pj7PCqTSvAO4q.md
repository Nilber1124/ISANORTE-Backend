# AaCdT25Pj7PCqTSvAO4q — auto-llamada en creación de categoría

- **Regla / severidad:** `java:S6809` / CRITICAL; `CategoriaProductoService.java:125`.
- **Alerta exacta:** auto-llamada a método transaccional sin proxy.
- **Evidencia:** [captura ANTES](../../antes/AaCdT25Pj7PCqTSvAO4q.png) y [metadatos](../../antes/AaCdT25Pj7PCqTSvAO4q.json).

`createResponse(...)` es el punto de entrada proxificado y posee `@Transactional(REQUIRED)`; llama `create(...)` con la misma propagación. Por ello repositorios, relaciones y mapper operan dentro de una única transacción, incluso sin interceptar nuevamente el método interno.

**Código ANTES/DESPUÉS (sin cambio):** `return categoriaProductoMapper.toResponse(create(request));` dentro de `@Transactional createResponse(...)`.

No se realizó un cambio cosmético. `TransactionalSelfInvocationAssessmentTest` verifica anotación y propagación; las 21 integraciones de contenido dinámico pasaron.

**Estado:** riesgo aceptable propuesto, pendiente de aprobación. Residual: reevaluar si el método interno adopta `REQUIRES_NEW`, timeout o aislamiento diferente.
