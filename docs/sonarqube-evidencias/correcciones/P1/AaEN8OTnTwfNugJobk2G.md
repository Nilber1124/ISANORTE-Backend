# AaEN8OTnTwfNugJobk2G — precedencia ambigua al recortar guiones

- **Regla / severidad:** `java:S5850` / MAJOR / BUG.
- **Archivo y línea original:** `ConfiguracionSitio.java:187`.
- **Alerta:** agrupar la alternancia para hacer explícita la precedencia.
- **Evidencia ANTES:** [captura](../../antes/AaEN8OTnTwfNugJobk2G.png) y [metadatos](../../antes/AaEN8OTnTwfNugJobk2G.json).

**ANTES:** `.replaceAll("^-+|-+$", "")`, donde cada ancla pertenecía a una alternativa distinta.

**DESPUÉS:** `stripEdgeHyphens` recorre índices inicial/final sin regex. La intención queda explícita y no depende de precedencia.

**Pruebas:** normalización Unicode, espacios, múltiples guiones en ambos extremos y conservación del contenido central. Integración de persistencia/contenido exitosa.

**Estado:** error real corregido.
