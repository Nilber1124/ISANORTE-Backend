# AaEN8OttTwfNugJobk54

- **Regla / severidad:** `java:S8924` / MINOR.
- **Archivo y línea original:** `src/test/java/com/isanorte/constructora_api/config/DemoProductSeederTest.java:61`.
- **Problema o recomendación:** Use a static import for "times".
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OttTwfNugJobk54.png) y [metadatos](../../antes/AaEN8OttTwfNugJobk54.json).

## ANTES

```java
verify(productoRepository, org.mockito.Mockito.times(26)).save(captor.capture());
```

## DESPUÉS

```java
verify(productoRepository, times(26)).save(captor.capture());
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
