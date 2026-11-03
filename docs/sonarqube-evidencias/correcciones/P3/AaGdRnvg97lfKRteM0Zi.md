# AaGdRnvg97lfKRteM0Zi

- **Regla / severidad:** `java:S5778` / MAJOR.
- **Archivo y línea original:** `src/test/java/com/isanorte/constructora_api/service/implementation/DireccionClienteServiceTest.java:65`.
- **Problema o recomendación:** Refactor the code of the lambda to have only one invocation possibly throwing a runtime exception.
- **Evidencia:** [captura real ANTES](../../antes/AaGdRnvg97lfKRteM0Zi.png) y [metadatos](../../antes/AaGdRnvg97lfKRteM0Zi.json).

## ANTES

```java
assertThrows(ModelNotFoundException.class, () -> service.delete(UUID.randomUUID(), direccionId));
```

## DESPUÉS

```java
El punto reportado ya no está presente en la revisión actual.
```

## Sustento técnico

El archivo o la construcción señalada desapareció por evolución previa. No se recreó código para reproducir una alerta histórica.

**Estado final propuesto:** HISTÓRICA YA RESUELTA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
