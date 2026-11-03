# AaCdT24Bj7PCqTSvAO4j — auto-llamada en creación de empresa

- **Regla / severidad:** `java:S6809` / CRITICAL; `EmpresaService.java:85`.
- **Alerta:** llamada directa a método transaccional en el mismo bean.
- **Evidencia:** [captura ANTES](../../antes/AaCdT24Bj7PCqTSvAO4j.png) y [metadatos](../../antes/AaCdT24Bj7PCqTSvAO4j.json).

El método público `createResponse(...)` abre una transacción `REQUIRED` antes de llamar `create(...)`, que declara exactamente la misma semántica. La creación de redes sociales y guardado de empresa quedan cubiertos.

**Código ANTES/DESPUÉS (sin cambio):** `return empresaMapper.toResponse(create(request));` dentro de `@Transactional createResponse(...)`.

La prueba de evaluación comprueba la anotación pública; las integraciones de contenido pasaron sin regresiones. No se modifica el código porque no hay defecto ejecutable demostrado.

**Estado:** riesgo aceptable propuesto, pendiente de aprobación; reevaluar ante políticas internas diferentes.
