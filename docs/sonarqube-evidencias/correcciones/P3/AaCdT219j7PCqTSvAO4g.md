# AaCdT219j7PCqTSvAO4g

- **Regla / severidad:** `java:S119` / MINOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/repository/IGenericRepository.java:7`.
- **Problema o recomendación:** Rename this generic name to match the regular expression '^[A-Z][0-9]?$'.
- **Evidencia:** [captura real ANTES](../../antes/AaCdT219j7PCqTSvAO4g.png) y [metadatos](../../antes/AaCdT219j7PCqTSvAO4g.json).

## ANTES

```java
public interface IGenericRepository<T, ID> extends JpaRepository<T, ID> {
```

## DESPUÉS

```java
public interface IGenericRepository<T, I> extends JpaRepository<T, I>
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
