# AaCdT29Lj7PCqTSvAO5c — secreto versionado histórico

- **Regla / severidad:** `java:S6437` / BLOCKER / VULNERABILITY.
- **Ubicación original:** `src/main/resources/application.properties:5`.
- **Estado original y actual:** FIXED en el reporte; `CLOSED / FIXED` en la API.
- **Evidencia ANTES:** [metadatos históricos](../../antes/AaCdT29Lj7PCqTSvAO5c.json); no hay PNG válido por cierre previo.

## Problema y riesgo residual

SonarQube detectó una credencial en una revisión histórica. El valor no se reproduce:

## Código anterior y corrección

```properties
spring.datasource.password=<VALOR_SENSIBLE_REDACTADO>
```

La configuración versionable usa ahora una referencia externa sin fallback sensible y el archivo local queda ignorado:

```properties
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
```

El estado CLOSED confirma ausencia en el análisis actual, no revocación. Los clones y el historial pueden conservar el valor antiguo.

## Pruebas y resultado

Compilación correcta; 19 pruebas de seguridad/CORS exitosas. Se verificaron el ignore y la plantilla sin exponer valores.

**Estado P0:** exposición en código corregida; **revocación/rotación pendiente de intervención externa**.
