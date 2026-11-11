# AaEN8OeGTwfNugJobk2j

- **Regla / severidad:** `java:S3358` / MAJOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/ComparacionPrecioService.java:55`.
- **Descripción exacta:** Extract this nested ternary operation into an independent statement.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OeGTwfNugJobk2j.png) y [metadatos](../../antes/AaEN8OeGTwfNugJobk2j.json).

## ANTES

```java
: diferencia.signum() < 0 ? "El precio externo es " + monto + " menor."
```

## DESPUÉS

```java
mensaje = comparisonMessage(diferencia);
```

## Sustento técnico

La decisión se extrajo a una operación con nombre y ramas explícitas, conservando los mismos resultados.

**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.

**Estado final propuesto:** CORREGIDA. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.
