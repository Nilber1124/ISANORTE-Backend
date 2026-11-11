# AaEN8OmWTwfNugJobk2v — auto-llamada read-only en proyecto

- **Regla / severidad:** `java:S6809` / CRITICAL; `ProyectoService.java:94`.
- **Alerta exacta:** “Call transactional methods via an injected dependency instead of directly via 'this'.”
- **Evidencia:** [captura ANTES](../../antes/AaEN8OmWTwfNugJobk2v.png) y [metadatos](../../antes/AaEN8OmWTwfNugJobk2v.json).

**Contexto:** `findByIdResponse(...)` ya está anotado `@Transactional(readOnly = true)` y llama `findById(...)`, igualmente read-only. El proxy abre la transacción antes de la auto-llamada; la carga de asociaciones y el mapeo ocurren dentro de ella.

**Código ANTES/DESPUÉS (sin cambio):** `@Transactional(readOnly = true)` y `return proyectoMapper.toResponse(findById(id));`.

**Decisión y evidencia:** no se modifica código porque la semántica efectiva coincide. La prueba de evaluación confirma `REQUIRED/readOnly=true`; 51 integraciones de contenido/producto/seguridad pasaron.

**Estado:** riesgo aceptable propuesto, pendiente de aprobación. Residual: una futura propagación distinta en `findById` requerirá separar colaborador.
