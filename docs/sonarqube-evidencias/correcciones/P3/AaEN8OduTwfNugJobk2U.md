# AaEN8OduTwfNugJobk2U

- **Regla / severidad:** `java:S1192` / CRITICAL.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/scraping/PrecioHtmlExtractor.java:56`.
- **Problema o recomendación:** Define a constant instead of duplicating this literal "product:sale_price:amount" 3 times.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OduTwfNugJobk2U.png) y [metadatos](../../antes/AaEN8OduTwfNugJobk2U.json).

## ANTES

```java
for (String key : List.of("product:sale_price:amount", "product:price:amount", "og:price:amount", "og:product:price:amount")) {
```

## DESPUÉS

```java
SALE_PRICE_AMOUNT sustituye las tres repeticiones.
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
