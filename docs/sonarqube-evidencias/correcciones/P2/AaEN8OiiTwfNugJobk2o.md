# AaEN8OiiTwfNugJobk2o

- **Regla / severidad:** `java:S8688` / INFO.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/AuthService.java:59`.
- **Descripción exacta:** Explicitly specify the time zone by passing a ZoneId or a Clock to the .now() method.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OiiTwfNugJobk2o.png) y [metadatos](../../antes/AaEN8OiiTwfNugJobk2o.json).

## ANTES

```java
admin.setUltimoAcceso(LocalDateTime.now());
```

## DESPUÉS

```java
admin.setUltimoAcceso(BusinessTime.now());
```

## Sustento técnico

La llamada dependía de la zona predeterminada del proceso. BusinessTime centraliza la zona de negocio America/Lima y pasa un ZoneId explícito a la API temporal.

**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.

**Estado final propuesto:** CORREGIDA. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.
