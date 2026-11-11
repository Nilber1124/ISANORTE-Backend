# AaCdT29Lj7PCqTSvAO5d — contraseña de base de datos histórica

- **Regla / severidad:** `secrets:S6703` / BLOCKER / VULNERABILITY.
- **Ubicación original:** `src/main/resources/application.properties:5`.
- **Estado original y actual:** FIXED en el reporte; `CLOSED / FIXED` en la API.
- **Evidencia ANTES:** [metadatos históricos](../../antes/AaCdT29Lj7PCqTSvAO5d.json); no hay PNG válido por cierre previo.

## Problema y riesgo residual

Es la detección especializada de contraseña asociada a `AaCdT29Lj7PCqTSvAO5c`. La configuración anterior contenía un valor que se mantiene redactado:

## Código anterior y corrección

```properties
spring.datasource.password=<VALOR_SENSIBLE_REDACTADO>
```

La plantilla requiere `SPRING_DATASOURCE_PASSWORD`, sin incluir una contraseña predeterminada. `.gitignore` impide añadir accidentalmente el archivo local.

## Pruebas y resultado

Compilación correcta; 19 pruebas de seguridad/CORS, 0 fallos y 0 errores. Verificación segura de plantilla y seguimiento Git completada.

**Estado P0:** exposición en código corregida; **rotación en PostgreSQL y actualización de despliegues pendientes de intervención externa**.
