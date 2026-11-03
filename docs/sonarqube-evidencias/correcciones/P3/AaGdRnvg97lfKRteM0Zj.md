# AaGdRnvg97lfKRteM0Zj

- **Regla / severidad:** `java:S8924` / MINOR.
- **Archivo y línea original:** `src/test/java/com/isanorte/constructora_api/service/implementation/DireccionClienteServiceTest.java:66`.
- **Problema o recomendación:** Use a static import for "never".
- **Evidencia:** [captura real ANTES](../../antes/AaGdRnvg97lfKRteM0Zj.png) y [metadatos](../../antes/AaGdRnvg97lfKRteM0Zj.json).

## ANTES

```java
verify(direccionRepository, org.mockito.Mockito.never()).delete(org.mockito.ArgumentMatchers.any());
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
verify(direccionRepository, never()).delete(org.mockito.ArgumentMatchers.any());
```

La modificación conserva la funcionalidad remota y elimina la causa técnica de la alerta. Validación focalizada: compilación con Java 21 y 49 pruebas exitosas, sin fallos ni errores.

**Estado actualizado:** CORREGIDA TRAS LA INTEGRACIÓN. La captura real DESPUÉS continúa pendiente de un nuevo análisis de SonarQube Cloud.
