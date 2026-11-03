# AaES7WAU-6tQcR7a5P5O

- **Regla / severidad:** `java:S8688` / INFO.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/model/CarritoCliente.java:41`.
- **Descripción exacta:** Explicitly specify the time zone by passing a ZoneId or a Clock to the .now() method.
- **Evidencia:** [captura real ANTES](../../antes/AaES7WAU-6tQcR7a5P5O.png) y [metadatos](../../antes/AaES7WAU-6tQcR7a5P5O.json).

## ANTES

```java
this.fechaActualizacion = LocalDateTime.now();
```

## DESPUÉS

```java
this.fechaActualizacion = BusinessTime.now();
```

## Sustento técnico

La llamada dependía de la zona predeterminada del proceso. BusinessTime centraliza la zona de negocio America/Lima y pasa un ZoneId explícito a la API temporal.

**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.

**Estado final propuesto:** CORREGIDA. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.
