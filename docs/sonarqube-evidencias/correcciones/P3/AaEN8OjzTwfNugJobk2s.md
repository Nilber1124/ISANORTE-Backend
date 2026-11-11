# AaEN8OjzTwfNugJobk2s

- **Regla / severidad:** `java:S1128` / MINOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/ComparacionCompetidoresService.java:12`.
- **Problema o recomendación:** Remove this unused import 'com.isanorte.constructora_api.exception.ScrapingPrecioException'.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OjzTwfNugJobk2s.png) y [metadatos](../../antes/AaEN8OjzTwfNugJobk2s.json).

## ANTES

```java
import com.isanorte.constructora_api.exception.ScrapingPrecioException;
```

## DESPUÉS

```java
Se eliminó el import no utilizado ScrapingPrecioException.
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
