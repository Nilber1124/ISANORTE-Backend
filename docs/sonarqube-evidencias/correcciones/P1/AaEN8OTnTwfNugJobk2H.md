# AaEN8OTnTwfNugJobk2H — backtracking al recortar guiones

- **Regla / severidad:** `java:S8786` / MAJOR.
- **Archivo y línea original:** `ConfiguracionSitio.java:187`.
- **Alerta:** regex potencialmente superlineal.
- **Evidencia ANTES:** [captura](../../antes/AaEN8OTnTwfNugJobk2H.png) y [metadatos](../../antes/AaEN8OTnTwfNugJobk2H.json).

**ANTES:** `.replaceAll("^-+|-+$", "")` podía explorar alternativas sobre secuencias largas.

**DESPUÉS:** dos bucles acotados avanzan desde los extremos y una sola `substring` produce el resultado: complejidad lineal y memoria predecible.

**Pruebas:** 600 000 guiones alrededor de una clave bajo timeout, además de normalización funcional e integración. 83 unitarias y 51 integraciones exitosas.

**Estado:** error real corregido.
