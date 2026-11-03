# AaGdRnvb97lfKRteM0Zf

- **Regla / severidad:** `java:S5778` / MAJOR.
- **Archivo y línea original:** `src/test/java/com/isanorte/constructora_api/service/implementation/PedidoServiceTest.java:63`.
- **Problema o recomendación:** Refactor the code of the lambda to have only one invocation possibly throwing a runtime exception.
- **Evidencia:** [captura real ANTES](../../antes/AaGdRnvb97lfKRteM0Zf.png) y [metadatos](../../antes/AaGdRnvb97lfKRteM0Zf.json).

## ANTES

```java
assertThrows(IllegalStateException.class,
```

## DESPUÉS

```java
El punto reportado ya no está presente en la revisión actual.
```

## Sustento técnico

El archivo o la construcción señalada desapareció por evolución previa. No se recreó código para reproducir una alerta histórica.

**Estado final propuesto:** HISTÓRICA YA RESUELTA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
