# AaEN5JlyD6mrdRS6eNmS

- **Regla / severidad:** `java:S9391` / MAJOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/ProyectoService.java:129`.
- **Problema o recomendación:** Use a stream instead of this loop.
- **Evidencia:** [metadatos históricos](../../antes/AaEN5JlyD6mrdRS6eNmS.json); no existe PNG válido disponible.

## ANTES

```java
throw new IllegalStateException("Ya existe otro proyecto con slug: " + request.slug());
```

## DESPUÉS

```java
El punto reportado ya no está presente en la revisión actual.
```

## Sustento técnico

El archivo o la construcción señalada desapareció por evolución previa. No se recreó código para reproducir una alerta histórica.

**Estado final propuesto:** HISTÓRICA YA RESUELTA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
