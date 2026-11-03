# AaEN8OnsTwfNugJobk20 — limpieza de título con regex costosa

- **Regla / severidad:** `java:S8786` / MAJOR.
- **Archivo y línea original:** `AbstractHtmlCompetidorProvider.java:114`.
- **Alerta:** regex potencialmente superlineal.
- **Evidencia ANTES:** [captura](../../antes/AaEN8OnsTwfNugJobk20.png) y [metadatos](../../antes/AaEN8OnsTwfNugJobk20.json).

**ANTES:** `replaceAll("\\s*[|–-]\\s*(Pisopak|Decorplas).*$", "")` sobre títulos remotos.

**DESPUÉS:** recorrido determinista que busca los tres separadores y elimina el sufijo solo cuando comienza con `Pisopak` o `Decorplas`. No altera guiones internos del producto y evita backtracking.

**Pruebas:** sufijos con barra, guion largo, guiones internos, `null` y entrada con 200 000 espacios bajo timeout. 83 unitarias exitosas.

**Estado:** error real corregido.
