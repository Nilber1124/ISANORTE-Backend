# AaEN8OsHTwfNugJobk5s

- **Regla / severidad:** `java:S5778` / MAJOR.
- **Archivo y línea original:** `src/test/java/com/isanorte/constructora_api/service/implementation/scraping/PrecioHtmlExtractorTest.java:67`.
- **Problema o recomendación:** Refactor the code of the lambda to have only one invocation possibly throwing a runtime exception.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OsHTwfNugJobk5s.png) y [metadatos](../../antes/AaEN8OsHTwfNugJobk5s.json).

## ANTES

```java
assertEquals(PRICE_NOT_FOUND, assertThrows(ScrapingPrecioException.class,
```

## DESPUÉS

```java
assertEquals(PRICE_NOT_FOUND, assertThrows(ScrapingPrecioException.class,
```

## Sustento técnico

La alerta afecta precisión de una lambda de prueba, no producción. Separar constructores/argumentos requeriría editar pruebas no ejecutadas en P3; se pospone hasta su batería específica.

**Estado final propuesto:** RECOMENDACIÓN SIN CAMBIO.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
