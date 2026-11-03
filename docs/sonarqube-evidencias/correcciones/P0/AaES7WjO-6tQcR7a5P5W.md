# AaES7WjO-6tQcR7a5P5W — contraseña de base de datos expuesta

- **Regla / severidad:** `secrets:S6703` / BLOCKER / VULNERABILITY.
- **Ubicación original:** `src/main/resources/application.properties:16`.
- **Estado original y actual:** FIXED en el reporte; `CLOSED / FIXED` en la API.
- **Evidencia ANTES:** [metadatos históricos](../../antes/AaES7WjO-6tQcR7a5P5W.json); no hay PNG válido por cierre previo.

## Problema y riesgo

Esta regla detectó la misma exposición histórica que `AaES7WjO-6tQcR7a5P5V`, especializada como contraseña de base de datos. Una contraseña retirada del archivo todavía puede seguir activa y permanecer en clones o historial.

## Código anterior y corrección

```properties
spring.datasource.password=<VALOR_SENSIBLE_REDACTADO>
```

```properties
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
```

La plantilla segura no define fallback; el archivo local real está ignorado por Git. No se reprodujo el secreto en registros ni documentación.

## Pruebas y resultado

Compilación correcta; 19 pruebas de seguridad/CORS, 0 fallos y 0 errores. Validación de versionado y plantilla completada sin imprimir credenciales.

**Estado P0:** exposición en código corregida; **cambio de contraseña en PostgreSQL y actualización del entorno pendientes de intervención externa**.
