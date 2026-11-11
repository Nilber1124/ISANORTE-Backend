# AaGdRnt197lfKRteM0Yx

- **Regla / severidad:** `java:S107` / MAJOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/IPublicContentService.java:22`.
- **Descripción exacta:** Method has 10 parameters, which is greater than 7 authorized.
- **Evidencia:** [captura real ANTES](../../antes/AaGdRnt197lfKRteM0Yx.png) y [metadatos](../../antes/AaGdRnt197lfKRteM0Yx.json).

## ANTES

```java
PublicProductCatalogResponse findProductCatalog(
```

## DESPUÉS

```java
PublicProductCatalogResponse findProductCatalog(
```

## Sustento técnico

La firma forma parte de un contrato/interfaz o de datos de inicialización. Introducir un objeto parámetro cambiaría consumidores y no corrige un fallo funcional; se propone para una refactorización de API posterior.

**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.

**Estado final propuesto:** RECOMENDACIÓN SIN CAMBIO. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.
