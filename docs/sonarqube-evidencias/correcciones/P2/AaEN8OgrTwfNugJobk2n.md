# AaEN8OgrTwfNugJobk2n

- **Regla / severidad:** `java:S3358` / MAJOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/SeccionLandingService.java:270`.
- **Descripción exacta:** Extract this nested ternary operation into an independent statement.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OgrTwfNugJobk2n.png) y [metadatos](../../antes/AaEN8OgrTwfNugJobk2n.json).

## ANTES

```java
: section.getTipo() == TipoSeccionLanding.CTA ? 1 : Integer.MAX_VALUE;
```

## DESPUÉS

```java
int maximum = maximumActiveActions(section.getTipo());
```

## Sustento técnico

La decisión se extrajo a una operación con nombre y ramas explícitas, conservando los mismos resultados.

**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.

**Estado final propuesto:** CORREGIDA. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.
