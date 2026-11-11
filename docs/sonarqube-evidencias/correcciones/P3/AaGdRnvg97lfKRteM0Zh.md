# AaGdRnvg97lfKRteM0Zh

- **Regla / severidad:** `java:S5778` / MAJOR.
- **Archivo y línea original:** `src/test/java/com/isanorte/constructora_api/service/implementation/DireccionClienteServiceTest.java:53`.
- **Problema o recomendación:** Refactor the code of the lambda to have only one invocation possibly throwing a runtime exception.
- **Evidencia:** [captura real ANTES](../../antes/AaGdRnvg97lfKRteM0Zh.png) y [metadatos](../../antes/AaGdRnvg97lfKRteM0Zh.json).

## ANTES

```java
assertThrows(IllegalArgumentException.class, () -> service.update(UUID.randomUUID(), direccionId,
```

## DESPUÉS

```java
El punto reportado ya no está presente en la revisión actual.
```

## Sustento técnico

El archivo o la construcción señalada desapareció por evolución previa. No se recreó código para reproducir una alerta histórica.

**Estado final propuesto:** HISTÓRICA YA RESUELTA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.


## Actualización tras integración de `origin/develop`

El merge del commit analizado `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93` recuperó el archivo original. La alerta volvió a ser verificable y se aplicó la corrección mínima:

```java
assertThrows(IllegalArgumentException.class, () -> service.update(clienteId, direccionId, request));
```

La modificación conserva la funcionalidad remota y elimina la causa técnica de la alerta. Validación focalizada: compilación con Java 21 y 49 pruebas exitosas, sin fallos ni errores.

**Estado actualizado:** CORREGIDA TRAS LA INTEGRACIÓN. La captura real DESPUÉS continúa pendiente de un nuevo análisis de SonarQube Cloud.
