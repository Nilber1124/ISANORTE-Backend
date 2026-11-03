# AaEN8OZpTwfNugJobk2Q — repetición regex en alta de sitio

- **Regla / severidad:** `java:S5998` / MAJOR.
- **Archivo y línea original:** `ConfiguracionSitioRequest.java:15`.
- **Alerta:** repetición capaz de desbordar la pila con entradas grandes.
- **Evidencia ANTES:** [captura](../../antes/AaEN8OZpTwfNugJobk2Q.png) y [metadatos](../../antes/AaEN8OZpTwfNugJobk2Q.json).

**ANTES:** `[a-z0-9]+(?:-[a-z0-9]+)*` usaba repeticiones anidadas retrocedibles.

**DESPUÉS:** `[a-z0-9]++(?:-[a-z0-9]++)*+`, equivalente para claves válidas, pero posesivo y sin backtracking.

**Pruebas:** claves válidas, separadores iniciales/repetidos, guion bajo y entrada adversaria de 500 001 caracteres bajo timeout. 83 unitarias y 21 integraciones de contenido exitosas.

**Estado:** error real corregido.
