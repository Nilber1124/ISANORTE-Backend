# AaEN8OduTwfNugJobk2d

- **Regla / severidad:** `java:S5843` / MAJOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/scraping/PrecioHtmlExtractor.java:22`.
- **Descripción exacta:** Simplify this regular expression to reduce its complexity from 38 to the 20 allowed.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OduTwfNugJobk2d.png) y [metadatos](../../antes/AaEN8OduTwfNugJobk2d.json).

## ANTES

```java
"(?i)(cuota|installment|mensual|monthly|env[ií]o|shipping|descuento|discount|"
```

## DESPUÉS

```java
Tres patrones precompilados con responsabilidades separadas y evaluación mediante isExcludedText(value).
```

## Sustento técnico

La expresión compleja se dividió en patrones precompilados más pequeños por responsabilidad, conservando todas las alternativas.

**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.

**Estado final propuesto:** CORREGIDA. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.
