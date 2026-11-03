# AaEN8OsHTwfNugJobk5u

- **Regla / severidad:** `java:S5778` / MAJOR.
- **Archivo y línea original:** `src/test/java/com/isanorte/constructora_api/service/implementation/scraping/PrecioHtmlExtractorTest.java:114`.
- **Problema o recomendación:** Refactor the code of the lambda to have only one invocation possibly throwing a runtime exception.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OsHTwfNugJobk5u.png) y [metadatos](../../antes/AaEN8OsHTwfNugJobk5u.json).

## ANTES

```java
assertEquals(NOT_COMPARABLE, assertThrows(ScrapingPrecioException.class, () -> extractor.extraer(Jsoup.parse("""
```

## DESPUÉS

```java
assertEquals(NOT_COMPARABLE, assertThrows(ScrapingPrecioException.class, () -> extractor.extraer(Jsoup.parse("""
```

## Sustento técnico

La alerta afecta precisión de una lambda de prueba, no producción. Separar constructores/argumentos requeriría editar pruebas no ejecutadas en P3; se pospone hasta su batería específica.

**Estado final propuesto:** RECOMENDACIÓN SIN CAMBIO.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
