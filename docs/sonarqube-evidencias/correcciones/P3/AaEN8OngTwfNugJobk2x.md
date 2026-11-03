# AaEN8OngTwfNugJobk2x

- **Regla / severidad:** `java:S1659` / MINOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/competitor/SimilitudProductoService.java:29`.
- **Problema o recomendación:** Declare "count" on a separate line.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OngTwfNugJobk2x.png) y [metadatos](../../antes/AaEN8OngTwfNugJobk2x.json).

## ANTES

```java
BigDecimal sum = BigDecimal.ZERO; int count = 0;
```

## DESPUÉS

```java
BigDecimal sum = BigDecimal.ZERO;
int count = 0;
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
