# AaCdT235j7PCqTSvAO4i

- **Regla / severidad:** `java:S119` / MINOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/GenericService.java:12`.
- **Problema o recomendación:** Rename this generic name to match the regular expression '^[A-Z][0-9]?$'.
- **Evidencia:** [captura real ANTES](../../antes/AaCdT235j7PCqTSvAO4i.png) y [metadatos](../../antes/AaCdT235j7PCqTSvAO4i.json).

## ANTES

```java
public abstract class GenericService<T, ID> implements IGenericService<T, ID> {
```

## DESPUÉS

```java
public abstract class GenericService<T, I> implements IGenericService<T, I>
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
