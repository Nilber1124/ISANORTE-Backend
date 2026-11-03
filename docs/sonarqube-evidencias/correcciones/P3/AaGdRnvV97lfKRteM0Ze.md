# AaGdRnvV97lfKRteM0Ze

- **Regla / severidad:** `java:S5778` / MAJOR.
- **Archivo y línea original:** `src/test/java/com/isanorte/constructora_api/service/implementation/ProductoServiceTest.java:59`.
- **Problema o recomendación:** Refactor the code of the lambda to have only one invocation possibly throwing a runtime exception.
- **Evidencia:** [captura real ANTES](../../antes/AaGdRnvV97lfKRteM0Ze.png) y [metadatos](../../antes/AaGdRnvV97lfKRteM0Ze.json).

## ANTES

```java
assertThrows(ModelNotFoundException.class,
```

## DESPUÉS

```java
assertThrows(ModelNotFoundException.class,
```

## Sustento técnico

La alerta afecta precisión de una lambda de prueba, no producción. Separar constructores/argumentos requeriría editar pruebas no ejecutadas en P3; se pospone hasta su batería específica.

**Estado final propuesto:** RECOMENDACIÓN SIN CAMBIO.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
