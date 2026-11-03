# AaGdRntQ97lfKRteM0Yt

- **Regla / severidad:** `java:S8688` / INFO.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/ProyectoService.java:161`.
- **Descripción exacta:** Explicitly specify the time zone by passing a ZoneId or a Clock to the .now() method.
- **Evidencia:** [captura real ANTES](../../antes/AaGdRntQ97lfKRteM0Yt.png) y [metadatos](../../antes/AaGdRntQ97lfKRteM0Yt.json).

## ANTES

```java
proyectoRepository.updateActivoById(id, request.activo(), LocalDateTime.now());
```

## DESPUÉS

```java
La llamada señalada ya no existe en la implementación actual.
```

## Sustento técnico

La revisión actual no contiene el punto reportado. No se reintrodujo código únicamente para reproducir o silenciar una incidencia histórica.

**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.

**Estado final propuesto:** HISTÓRICA YA RESUELTA. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.
