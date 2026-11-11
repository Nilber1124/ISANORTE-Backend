# AaES7VxB-6tQcR7a5P5K

- **Regla / severidad:** `java:S1192` / CRITICAL.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/config/SecurityConfig.java:48`.
- **Problema o recomendación:** Define a constant instead of duplicating this literal "ROLE_" 3 times.
- **Evidencia:** [captura real ANTES](../../antes/AaES7VxB-6tQcR7a5P5K.png) y [metadatos](../../antes/AaES7VxB-6tQcR7a5P5K.json).

## ANTES

```java
authorities.setAuthorityPrefix("ROLE_");
```

## DESPUÉS

```java
ROLE_PREFIX centraliza el prefijo de autoridades.
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
