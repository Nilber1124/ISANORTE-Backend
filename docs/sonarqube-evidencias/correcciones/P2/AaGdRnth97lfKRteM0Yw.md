# AaGdRnth97lfKRteM0Yw

- **Regla / severidad:** `java:S8688` / INFO.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/AnuncioService.java:76`.
- **Descripción exacta:** Explicitly specify the time zone by passing a ZoneId or a Clock to the .now() method.
- **Evidencia:** [captura real ANTES](../../antes/AaGdRnth97lfKRteM0Yw.png) y [metadatos](../../antes/AaGdRnth97lfKRteM0Yw.json).

## ANTES

```java
return anuncioRepository.findPublicosVigentes(destino, ubicacion, LocalDateTime.now()).stream()
```

## DESPUÉS

```java
return anuncioRepository.findPublicosVigentes(destino, ubicacion, BusinessTime.now()).stream()
```

## Sustento técnico

La llamada dependía de la zona predeterminada del proceso. BusinessTime centraliza la zona de negocio America/Lima y pasa un ZoneId explícito a la API temporal.

**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.

**Estado final propuesto:** CORREGIDA. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.
