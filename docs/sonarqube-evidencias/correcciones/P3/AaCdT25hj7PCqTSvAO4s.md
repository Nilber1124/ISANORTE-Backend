# AaCdT25hj7PCqTSvAO4s

- **Regla / severidad:** `java:S119` / MINOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/IGenericService.java:5`.
- **Problema o recomendación:** Rename this generic name to match the regular expression '^[A-Z][0-9]?$'.
- **Evidencia:** [captura real ANTES](../../antes/AaCdT25hj7PCqTSvAO4s.png) y [metadatos](../../antes/AaCdT25hj7PCqTSvAO4s.json).

## ANTES

```java
public interface IGenericService<T, ID> {
```

## DESPUÉS

```java
public interface IGenericService<T, I>
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
