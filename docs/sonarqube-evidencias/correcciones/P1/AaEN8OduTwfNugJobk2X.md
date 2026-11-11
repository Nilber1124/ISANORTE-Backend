# AaEN8OduTwfNugJobk2X — case folding Unicode

- **Regla / severidad:** `java:S5866` / MAJOR / BUG.
- **Archivo y línea original:** `PrecioHtmlExtractor.java:22`.
- **Alerta:** usar también la bandera Unicode al aplicar case-insensitive sobre letras no ASCII.
- **Evidencia ANTES:** [captura](../../antes/AaEN8OduTwfNugJobk2X.png) y [metadatos](../../antes/AaEN8OduTwfNugJobk2X.json).

**ANTES:** patrón `(?i)` que incluía `í`; el plegado de mayúsculas Unicode no quedaba garantizado.

**DESPUÉS:** `Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE`. Se conserva la expresión y se corrige la equivalencia `í/Í`.

**Pruebas:** un precio dentro de clase `ENVÍO` en mayúsculas se excluye correctamente, además de 20 casos existentes de extracción/ambigüedad. 83 unitarias exitosas.

**Estado:** error real corregido.
