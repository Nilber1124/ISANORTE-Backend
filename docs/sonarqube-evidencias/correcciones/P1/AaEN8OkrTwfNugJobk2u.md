# AaEN8OkrTwfNugJobk2u — auto-llamada transaccional en cotización pública

- **Regla / severidad:** `java:S6809` / CRITICAL; `CotizacionService.java:235`.
- **Alerta exacta:** “Call transactional methods via an injected dependency instead of directly via 'this'.”
- **Evidencia:** [captura ANTES](../../antes/AaEN8OkrTwfNugJobk2u.png) y [metadatos](../../antes/AaEN8OkrTwfNugJobk2u.json).

**Contexto:** `createPublic(...)`, anotado `@Transactional` con propagación `REQUIRED`, llama `create(...)`, también `REQUIRED`. La anotación interna no pasa por otro proxy, pero no es necesaria: el método de entrada ya abrió la misma transacción de escritura antes de resolver cliente, productos y guardar.

**Código ANTES/DESPUÉS (sin cambio):** `@Transactional public PublicCotizacionResponse createPublic(...)` contiene `Cotizacion cotizacion = create(internalRequest);`.

**Decisión:** no inyectar el bean en sí mismo ni duplicar lógica; ambas opciones aumentarían acoplamiento sin cambiar commit/rollback. `TransactionalSelfInvocationAssessmentTest` verifica la frontera pública; `PublicCotizacionApiIntegrationTest` ejecutó 8 escenarios reales de persistencia.

**Estado:** riesgo aceptable propuesto, pendiente de aprobación; riesgo residual: si el método interno cambia a propagación distinta, deberá extraerse a otro bean.
