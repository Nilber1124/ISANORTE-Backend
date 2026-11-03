# AaCdT27mj7PCqTSvAO5F

- **Regla / severidad:** `java:S1192` / CRITICAL.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/model/UnidadNegocio.java:114`.
- **Problema o recomendación:** Define a constant instead of duplicating this literal "El producto no puede ser null" 3 times.
- **Evidencia:** [captura real ANTES](../../antes/AaCdT27mj7PCqTSvAO5F.png) y [metadatos](../../antes/AaCdT27mj7PCqTSvAO5F.json).

## ANTES

```java
Objects.requireNonNull(producto, "El producto no puede ser null");
```

## DESPUÉS

```java
NULL_PRODUCT_MESSAGE centraliza el mensaje.
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
