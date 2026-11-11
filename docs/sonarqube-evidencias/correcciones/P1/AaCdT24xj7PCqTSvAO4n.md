# AaCdT24xj7PCqTSvAO4n — auto-llamada en sección landing

- **Regla / severidad:** `java:S6809` / CRITICAL; `SeccionLandingService.java:110`.
- **Alerta:** llamada directa a método transaccional en el mismo bean.
- **Evidencia:** [captura ANTES](../../antes/AaCdT24xj7PCqTSvAO4n.png) y [metadatos](../../antes/AaCdT24xj7PCqTSvAO4n.json).

El endpoint entra en `createResponse(...)`, ya anotado `@Transactional(REQUIRED)`, antes de invocar `create(...)` con política equivalente. Configuración, sección y persistencia permanecen atómicas.

**Código ANTES/DESPUÉS (sin cambio):** `return seccionLandingMapper.toResponse(create(request));` dentro de `@Transactional createResponse(...)`.

La prueba de evaluación y 21 integraciones de contenido confirman la frontera y el comportamiento. No se justifica una refactorización con mayor acoplamiento.

**Estado:** riesgo aceptable propuesto, pendiente de aprobación; revisar si diverge la política interna.
