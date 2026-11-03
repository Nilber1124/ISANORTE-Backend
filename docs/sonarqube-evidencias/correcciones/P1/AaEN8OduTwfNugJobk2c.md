# AaEN8OduTwfNugJobk2c — detección de estilos con backtracking

- **Regla / severidad:** `java:S8786` / MAJOR.
- **Archivo y línea original:** `PrecioHtmlExtractor.java:196`.
- **Alerta:** regex con comportamiento superlineal.
- **Evidencia ANTES:** [captura](../../antes/AaEN8OduTwfNugJobk2c.png) y [metadatos](../../antes/AaEN8OduTwfNugJobk2c.json).

**ANTES:** `style.matches("(?i).*(display...none|line-through).*")`, con comodines globales y alternancia.

**DESPUÉS:** patrón precompilado sin `.*`, evaluado con `find()`, y flags Unicode. La búsqueda es lineal y conserva ambos indicadores.

**Pruebas:** estilo adversario largo seguido de `display:none` bajo timeout y regresión completa del extractor. 83 unitarias exitosas.

**Estado:** error real corregido.
