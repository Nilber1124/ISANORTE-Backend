# AaGdRnuW97lfKRteM0Yy

- **Regla / severidad:** `java:S1192` / CRITICAL.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/repository/specification/ProductoCatalogSpecifications.java:35`.
- **Problema o recomendación:** Define a constant instead of duplicating this literal "unidadNegocio" 3 times.
- **Evidencia:** [captura real ANTES](../../antes/AaGdRnuW97lfKRteM0Yy.png) y [metadatos](../../antes/AaGdRnuW97lfKRteM0Yy.json).

## ANTES

```java
return (root, query, cb) -> cb.equal(root.get("unidadNegocio").get("id"), unidadId);
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
private static final String FIELD_BUSINESS_UNIT = "unidadNegocio";
```

La modificación conserva la funcionalidad remota y elimina la causa técnica de la alerta. Validación focalizada: compilación con Java 21 y 49 pruebas exitosas, sin fallos ni errores.

**Estado actualizado:** CORREGIDA TRAS LA INTEGRACIÓN. La captura real DESPUÉS continúa pendiente de un nuevo análisis de SonarQube Cloud.
