# AaEN8OduTwfNugJobk2T

- **Regla / severidad:** `java:S135` / MINOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/scraping/PrecioHtmlExtractor.java:74`.
- **Problema o recomendación:** Reduce the total number of break and continue statements in this loop to use at most one.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OduTwfNugJobk2T.png) y [metadatos](../../antes/AaEN8OduTwfNugJobk2T.json).

## ANTES

```java
for (Element element : product.select("[itemprop=price]")) {
```

## DESPUÉS

```java
for (Element element : product.select("[itemprop=price]")) {
```

## Sustento técnico

El bucle de extracción usa control temprano para descartar candidatos ambiguos. Reestructurarlo puede cambiar precedencia de evidencia; se mantiene hasta contar con pruebas exhaustivas del extractor.

**Estado final propuesto:** RECOMENDACIÓN SIN CAMBIO.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
