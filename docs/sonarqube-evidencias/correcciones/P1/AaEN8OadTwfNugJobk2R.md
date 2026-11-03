# AaEN8OadTwfNugJobk2R — repetición regex en actualización de sitio

- **Regla / severidad:** `java:S5998` / MAJOR.
- **Archivo y línea original:** `ConfiguracionSitioUpdateRequest.java:7`.
- **Alerta:** repetición capaz de desbordar la pila con entradas grandes.
- **Evidencia ANTES:** [captura](../../antes/AaEN8OadTwfNugJobk2R.png) y [metadatos](../../antes/AaEN8OadTwfNugJobk2R.json).

**ANTES:** `[a-z0-9]+(?:-[a-z0-9]+)*`.

**DESPUÉS:** cuantificadores posesivos `[a-z0-9]++(?:-[a-z0-9]++)*+`, sin cambiar el lenguaje aceptado.

**Pruebas:** validación Jakarta de actualización válida y entrada adversaria extensa bajo timeout; integración de actualización de contenido dinámico exitosa. Resultado: 83 unitarias y 51 integraciones sin fallos.

**Estado:** error real corregido.
