# AaCdT247j7PCqTSvAO4o — auto-llamada en configuración de sitio

- **Regla / severidad:** `java:S6809` / CRITICAL; `ConfiguracionSitioService.java:88`.
- **Alerta:** llamada directa a método transaccional en el mismo bean.
- **Evidencia:** [captura ANTES](../../antes/AaCdT247j7PCqTSvAO4o.png) y [metadatos](../../antes/AaCdT247j7PCqTSvAO4o.json).

`createResponse(...)` ya entra por proxy con transacción de escritura `REQUIRED`; `create(...)` declara la misma política. No existe una operación fuera de transacción ni una propagación interna que se pierda.

**Código ANTES/DESPUÉS (sin cambio):** `return configuracionSitioMapper.toResponse(create(request));` dentro de `@Transactional createResponse(...)`.

Se verificó la anotación automáticamente y se ejecutaron 21 integraciones de contenido, incluidas altas y actualizaciones de configuración. No se añadió autoinyección ni se duplicó el flujo.

**Estado:** riesgo aceptable propuesto, pendiente de aprobación. Residual: separar el flujo si divergen las políticas transaccionales.
