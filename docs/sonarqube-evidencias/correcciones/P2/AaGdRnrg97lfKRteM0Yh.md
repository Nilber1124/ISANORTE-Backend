# AaGdRnrg97lfKRteM0Yh

- **Regla / severidad:** `java:S8688` / INFO.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/model/SeguimientoPedido.java:68`.
- **Descripción exacta:** Explicitly specify the time zone by passing a ZoneId or a Clock to the .now() method.
- **Evidencia:** [captura real ANTES](../../antes/AaGdRnrg97lfKRteM0Yh.png) y [metadatos](../../antes/AaGdRnrg97lfKRteM0Yh.json).

## ANTES

```java
fecha = LocalDateTime.now();
```

## DESPUÉS

```java
El archivo señalado ya no existe en la implementación actual.
```

## Sustento técnico

La revisión actual no contiene el punto reportado. No se reintrodujo código únicamente para reproducir o silenciar una incidencia histórica.

**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.

**Estado final propuesto:** HISTÓRICA YA RESUELTA. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.


## Actualización tras integración de `origin/develop`

El merge del commit analizado `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93` recuperó el archivo original. La alerta volvió a ser verificable y se aplicó la corrección mínima:

```java
fecha = BusinessTime.now();
```

La modificación conserva la funcionalidad remota y elimina la causa técnica de la alerta. Validación focalizada: compilación con Java 21 y 49 pruebas exitosas, sin fallos ni errores.

**Estado actualizado:** CORREGIDA TRAS LA INTEGRACIÓN. La captura real DESPUÉS continúa pendiente de un nuevo análisis de SonarQube Cloud.
