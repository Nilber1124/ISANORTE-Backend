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
