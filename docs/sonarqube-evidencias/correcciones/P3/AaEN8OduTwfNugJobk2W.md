# AaEN8OduTwfNugJobk2W

- **Regla / severidad:** `java:S1192` / CRITICAL.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/scraping/PrecioHtmlExtractor.java:63`.
- **Problema o recomendación:** Define a constant instead of duplicating this literal "content" 5 times.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OduTwfNugJobk2W.png) y [metadatos](../../antes/AaEN8OduTwfNugJobk2W.json).

## ANTES

```java
add(candidates, element.attr("content"), currency, title, true);
```

## DESPUÉS

```java
CONTENT_ATTRIBUTE sustituye las cinco repeticiones.
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
