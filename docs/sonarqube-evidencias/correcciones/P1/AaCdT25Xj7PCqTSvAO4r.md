# AaCdT25Xj7PCqTSvAO4r — auto-llamada en servicio comercial

- **Regla / severidad:** `java:S6809` / CRITICAL; `ServicioService.java:108`.
- **Alerta:** llamada directa a método transaccional en el mismo bean.
- **Evidencia:** [captura ANTES](../../antes/AaCdT25Xj7PCqTSvAO4r.png) y [metadatos](../../antes/AaCdT25Xj7PCqTSvAO4r.json).

Aunque la línea actual cambió por evolución del archivo, la alerta corresponde a `createResponse(...) -> create(...)`. El método externo ya abre `REQUIRED`, igual que el interno; repositorio y mapeo están cubiertos.

**Código ANTES/DESPUÉS (sin cambio):** `return servicioMapper.toResponse(create(request));` dentro de `@Transactional createResponse(...)`.

La política se verificó mediante prueba automática y las integraciones dinámicas pasaron. No se cambió código sin defecto observable.

**Estado:** riesgo aceptable propuesto, pendiente de aprobación. Residual: extraer colaborador si cambia propagación, aislamiento o timeout.
