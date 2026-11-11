# AaEN8Od6TwfNugJobk2f

- **Regla / severidad:** `java:S3776` / CRITICAL.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/scraping/UrlExternaPolicy.java:17`.
- **Descripción exacta:** Refactor this method to reduce its Cognitive Complexity from 18 to the 15 allowed.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8Od6TwfNugJobk2f.png) y [metadatos](../../antes/AaEN8Od6TwfNugJobk2f.json).

## ANTES

```java
public Destino validar(String value) {
```

## DESPUÉS

```java
public Destino validar(String value) {
```

## Sustento técnico

La complejidad cognitiva es una métrica de mantenibilidad, no evidencia de comportamiento incorrecto. Una extracción extensa tiene mayor riesgo de regresión que valor dentro de P2 rápido; se conserva el flujo probado y se recomienda abordarla con pruebas dedicadas.

**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.

**Estado final propuesto:** RECOMENDACIÓN SIN CAMBIO. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.
