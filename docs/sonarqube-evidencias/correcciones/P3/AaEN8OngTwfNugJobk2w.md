# AaEN8OngTwfNugJobk2w

- **Regla / severidad:** `java:S5673` / MAJOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/competitor/SimilitudProductoService.java:9`.
- **Problema o recomendación:** Use @Service instead of @Component, or rename this type if the @Component annotation is intentional
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OngTwfNugJobk2w.png) y [metadatos](../../antes/AaEN8OngTwfNugJobk2w.json).

## ANTES

```java
@Component
```

## DESPUÉS

```java
@Service
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
