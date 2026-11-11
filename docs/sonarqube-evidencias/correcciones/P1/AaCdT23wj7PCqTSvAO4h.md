# AaCdT23wj7PCqTSvAO4h — auto-llamada en creación de producto

- **Regla / severidad:** `java:S6809` / CRITICAL; `ProductoService.java:199`.
- **Alerta:** llamada directa a método transaccional en el mismo bean.
- **Evidencia:** [captura ANTES](../../antes/AaCdT23wj7PCqTSvAO4h.png) y [metadatos](../../antes/AaCdT23wj7PCqTSvAO4h.json).

`createResponse(...)` está proxificado con `@Transactional(REQUIRED)` y envuelve por completo `create(...)`, cuya política es idéntica. Categorías, unidad, variantes y persistencia ocurren en la transacción externa.

**Código ANTES/DESPUÉS (sin cambio):** `return productoMapper.toResponse(create(request));` dentro de `@Transactional createResponse(...)`.

Se verificó la frontera por reflexión; `PublicProductApiIntegrationTest` y las integraciones de contenido pasaron. No se aplicó autoinyección ni duplicación.

**Estado:** riesgo aceptable propuesto, pendiente de aprobación. Residual: extraer colaborador si aparecen requisitos transaccionales distintos.
