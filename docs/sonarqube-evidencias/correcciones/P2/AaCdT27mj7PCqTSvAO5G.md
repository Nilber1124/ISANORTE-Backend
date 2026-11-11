# AaCdT27mj7PCqTSvAO5G

- **Regla / severidad:** `java:S8688` / INFO.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/model/UnidadNegocio.java:161`.
- **Descripción exacta:** Explicitly specify the time zone by passing a ZoneId or a Clock to the .now() method.
- **Evidencia:** [captura real ANTES](../../antes/AaCdT27mj7PCqTSvAO5G.png) y [metadatos](../../antes/AaCdT27mj7PCqTSvAO5G.json).

## ANTES

```java
fechaCreacion = LocalDateTime.now();
```

## DESPUÉS

```java
fechaCreacion = BusinessTime.now();
```

## Sustento técnico

La llamada dependía de la zona predeterminada del proceso. BusinessTime centraliza la zona de negocio America/Lima y pasa un ZoneId explícito a la API temporal.

**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.

**Estado final propuesto:** CORREGIDA. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.
