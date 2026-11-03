# AaEN8OBpTwfNugJobk14 — rollback de excepción comprobada

- **Regla / severidad:** `java:S8989` / CRITICAL.
- **Archivo y línea original:** `DemoProductSeeder.java:51`.
- **Alerta:** especificar `rollbackFor` o `noRollbackFor` para excepciones comprobadas.
- **Evidencia ANTES:** [metadatos históricos](../../antes/AaEN8OBpTwfNugJobk14.json); no existe PNG porque Sonar ya la reportaba FIXED/CLOSED.

**ANTES:** `@Transactional` sobre `run(...) throws IOException`. Spring no revierte por checked exceptions de forma predeterminada. Aunque hoy la lectura ocurre antes de guardar, el contrato no protegía futuras operaciones previas a un fallo de E/S.

**DESPUÉS:** `@Transactional(rollbackFor = IOException.class)`. Se conserva el contrato y se explicita rollback completo.

**Pruebas:** `DemoProductSeederTest` cubre carga, idempotencia y errores; `DemoProductSeederTransactionTest` verifica por reflexión la política exacta. Resultado conjunto P1: 83/83 unitarias y 51/51 integraciones exitosas.

**Estado:** incidencia histórica reforzada/corregida; captura DESPUÉS pendiente de análisis real.
