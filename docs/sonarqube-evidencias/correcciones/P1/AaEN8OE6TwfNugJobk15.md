# AaEN8OE6TwfNugJobk15 — validación de correo con backtracking

- **Regla / severidad:** `java:S8786` / MAJOR.
- **Archivo y línea original:** `AdminInitializer.java:43`.
- **Alerta:** regex con rendimiento superlineal por backtracking.
- **Evidencia ANTES:** [captura](../../antes/AaEN8OE6TwfNugJobk15.png) y [metadatos](../../antes/AaEN8OE6TwfNugJobk15.json).

**ANTES:** `email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")`. Los cuantificadores solapados podían reconsiderar gran parte de una entrada adversaria.

**DESPUÉS:** `isValidEmail` localiza un único `@`, el último punto y espacios mediante recorridos lineales. Mantiene las condiciones funcionales sin regex.

**Pruebas:** correo válido, configuración vacía y correo adversario de 300 000 caracteres bajo timeout; no se persiste ni codifica contraseña al fallar. 83 unitarias exitosas.

**Estado:** error real corregido.
