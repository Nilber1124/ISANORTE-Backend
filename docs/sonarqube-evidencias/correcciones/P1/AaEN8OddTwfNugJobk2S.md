# AaEN8OddTwfNugJobk2S — semántica de array en Pagina

- **Regla / severidad:** `java:S6218` / MAJOR / BUG.
- **Archivo y línea original:** `PaginaExternaClient.java:23`.
- **Alerta:** `equals`, `hashCode` y `toString` del record no consideran el contenido del array.
- **Evidencia ANTES:** [captura](../../antes/AaEN8OddTwfNugJobk2S.png) y [metadatos](../../antes/AaEN8OddTwfNugJobk2S.json).

**ANTES:** `record Pagina(..., byte[] html, ...) {}` comparaba identidad del array y exponía estado mutable.

**DESPUÉS:** constructor y accessor realizan copia defensiva; igualdad/hash usan `Arrays.equals/hashCode`; `toString` representa el contenido, incluido `null`.

**Pruebas:** arrays distintos con igual contenido, contenido diferente, `null`, mutación del origen y del valor retornado. 83 unitarias exitosas.

**Estado:** error real corregido.
