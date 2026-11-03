# AaGdRnta97lfKRteM0Yv

- **Regla / severidad:** `java:S8688` / INFO.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/PedidoService.java:302`.
- **Descripción exacta:** Explicitly specify the time zone by passing a ZoneId or a Clock to the .now() method.
- **Evidencia:** [captura real ANTES](../../antes/AaGdRnta97lfKRteM0Yv.png) y [metadatos](../../antes/AaGdRnta97lfKRteM0Yv.json).

## ANTES

```java
return "PED-" + LocalDate.now().toString().replace("-", "") + "-"
```

## DESPUÉS

```java
El archivo señalado ya no existe en la implementación actual.
```

## Sustento técnico

La revisión actual no contiene el punto reportado. No se reintrodujo código únicamente para reproducir o silenciar una incidencia histórica.

**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.

**Estado final propuesto:** HISTÓRICA YA RESUELTA. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.
