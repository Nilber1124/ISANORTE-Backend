# AaEN8OixTwfNugJobk2p

- **Regla / severidad:** `java:S1141` / MAJOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/ScrapingPrecioService.java:39`.
- **Descripción exacta:** Extract this nested try block into a separate method.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OixTwfNugJobk2p.png) y [metadatos](../../antes/AaEN8OixTwfNugJobk2p.json).

## ANTES

```java
try { next = destino.uri().resolve(pagina.location()); }
```

## DESPUÉS

```java
URI next = resolveRedirect(destino.uri(), pagina.location());
```

## Sustento técnico

La resolución de redirecciones y su traducción de error se extrajeron, eliminando el try anidado sin cambiar la política SSRF.

**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.

**Estado final propuesto:** CORREGIDA. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.
