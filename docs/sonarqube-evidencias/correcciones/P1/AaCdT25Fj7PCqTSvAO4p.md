# AaCdT25Fj7PCqTSvAO4p — auto-llamada en creación de proyecto

- **Regla / severidad:** `java:S6809` / CRITICAL; `ProyectoService.java:121`.
- **Alerta:** llamada directa a método transaccional en el mismo bean.
- **Evidencia:** [captura ANTES](../../antes/AaCdT25Fj7PCqTSvAO4p.png) y [metadatos](../../antes/AaCdT25Fj7PCqTSvAO4p.json).

`createResponse(...)` y `create(...)` comparten `REQUIRED`; la transacción del método público cubre asociación de servicios y guardado. La ausencia de una segunda intercepción no altera el resultado.

**Código ANTES/DESPUÉS (sin cambio):** `return proyectoMapper.toResponse(create(request));` dentro de `@Transactional createResponse(...)`.

La prueba transaccional valida la política exacta y las integraciones de contenido/productos finalizaron correctamente.

**Estado:** riesgo aceptable propuesto, pendiente de aprobación. Residual: si `create` requiere otra propagación, moverlo a otro bean.
