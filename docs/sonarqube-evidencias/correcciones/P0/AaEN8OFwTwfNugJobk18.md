# AaEN8OFwTwfNugJobk18 — protección CSRF desactivada globalmente

- **Regla / severidad:** `java:S4502` / CRITICAL / VULNERABILITY.
- **Ubicación original:** `src/main/java/com/isanorte/constructora_api/config/SecurityConfig.java:61`.
- **Estado original:** OPEN.
- **Evidencia ANTES:** [captura real](../../antes/AaEN8OFwTwfNugJobk18.png) y [metadatos](../../antes/AaEN8OFwTwfNugJobk18.json).

## Problema y riesgo

La protección CSRF se desactivaba para toda la aplicación. Aunque la API actual es stateless y recibe JWT Bearer en `Authorization` —el navegador no lo adjunta automáticamente como una cookie—, la desactivación global dejaría sin protección cualquier ruta web basada en sesión que se añadiera después.

## Código anterior

```java
.csrf(csrf -> csrf.disable())
```

## Código corregido

```java
.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
```

La exclusión queda limitada al contrato API stateless. CSRF permanece activo en rutas no API y no se eliminó funcionalidad.

## Pruebas y resultado

`SecurityConfigIntegrationTest` verifica que un POST API sin token CSRF alcanza la validación normal y que un POST no API sin token recibe 403. Junto con `CorsConfigTest`: 19 pruebas, 0 fallos, 0 errores; BUILD SUCCESS.

**Estado P0:** corregida en código; captura DESPUÉS pendiente del próximo análisis real de SonarQube Cloud.
