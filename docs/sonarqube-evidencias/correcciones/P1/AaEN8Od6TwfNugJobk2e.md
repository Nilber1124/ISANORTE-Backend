# AaEN8Od6TwfNugJobk2e — semántica de array en Destino

- **Regla / severidad:** `java:S6218` / MAJOR / BUG.
- **Archivo y línea original:** `UrlExternaPolicy.java:15`.
- **Alerta:** métodos del record comparan la identidad del array de direcciones.
- **Evidencia ANTES:** [captura](../../antes/AaEN8Od6TwfNugJobk2e.png) y [metadatos](../../antes/AaEN8Od6TwfNugJobk2e.json).

**ANTES:** `record Destino(URI uri, InetAddress[] direcciones) {}`.

**DESPUÉS:** copias defensivas de direcciones y `Arrays.equals/hashCode/toString`, manteniendo `URI` inmutable. Esto también impide cambiar las IP validadas después de la política SSRF.

**Pruebas:** igualdad por contenido, hash, diferencia de IP, arrays nulos y mutaciones del origen/accessor; continúan pasando 35 escenarios SSRF. 83 unitarias exitosas.

**Estado:** error real corregido.
