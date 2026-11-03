# AaEN8OTnTwfNugJobk2F

- **Regla / severidad:** `java:S4144` / MAJOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/model/ConfiguracionSitio.java:171`.
- **Problema o recomendación:** Update this method so that its implementation is not identical to "onCreate" on line 165.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OTnTwfNugJobk2F.png) y [metadatos](../../antes/AaEN8OTnTwfNugJobk2F.json).

## ANTES

```java
protected void onUpdate() {
```

## DESPUÉS

```java
onCreate y onUpdate delegan en prepareForPersistence().
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
