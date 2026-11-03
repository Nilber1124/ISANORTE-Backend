# AaEN8OpSTwfNugJobk21 — CORS permisivo en controlador

- **Regla / severidad:** `java:S5122` / MAJOR / VULNERABILITY.
- **Ubicación original:** `src/main/java/com/isanorte/constructora_api/controller/ArchivoController.java:15`.
- **Estado original y actual:** FIXED en el reporte; `CLOSED / FIXED` en la API.
- **Evidencia ANTES:** [metadatos históricos](../../antes/AaEN8OpSTwfNugJobk21.json); no hay PNG válido por cierre previo.

## Problema y riesgo

El controlador de archivos autorizaba cualquier origen mediante una anotación local. Además existían dos mecanismos globales CORS, con patrón Vercel comodín, todas las cabeceras y credenciales habilitadas. Esa combinación ampliaba innecesariamente la superficie y hacía difícil razonar sobre la política efectiva.

## Código anterior

```java
@CrossOrigin(origins = "*")
public class ArchivoController {
```

## Código corregido

La anotación se eliminó. Una sola `CorsConfigurationSource` acepta únicamente orígenes HTTP(S) exactos, métodos y cabeceras explícitos, rechaza comodines/rutas y no permite credenciales CORS:

```java
config.setAllowedOrigins(origins);
config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
config.setAllowCredentials(false);
```

## Pruebas y resultado

Se probaron normalización, duplicados, nulos/vacíos, comodines, esquemas/rutas inválidos, origen permitido y origen atacante. Resultado conjunto: 19 pruebas, 0 fallos, 0 errores; BUILD SUCCESS.

**Estado P0:** corregida y reforzada; captura DESPUÉS pendiente del próximo análisis real.
