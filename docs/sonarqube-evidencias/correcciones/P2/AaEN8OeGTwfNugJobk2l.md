# AaEN8OeGTwfNugJobk2l

- **Regla / severidad:** `java:S8688` / INFO.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/ComparacionPrecioService.java:65`.
- **Descripción exacta:** Explicitly specify the time zone by passing a ZoneId or a Clock to the .now() method.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OeGTwfNugJobk2l.png) y [metadatos](../../antes/AaEN8OeGTwfNugJobk2l.json).

## ANTES

```java
MONEDA_INTERNA, null, null, null, false, ex.getEstado(), ex.getMessage(), OffsetDateTime.now());
```

## DESPUÉS

```java
MONEDA_INTERNA, null, null, null, false, ex.getEstado(), ex.getMessage(), BusinessTime.nowOffset());
```

## Sustento técnico

La llamada dependía de la zona predeterminada del proceso. BusinessTime centraliza la zona de negocio America/Lima y pasa un ZoneId explícito a la API temporal.

**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.

**Estado final propuesto:** CORREGIDA. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.
