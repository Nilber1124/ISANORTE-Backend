# AaGdRnvH97lfKRteM0Zb — límite de archivo multipart

- **Regla / severidad:** `java:S5693` / MAJOR / VULNERABILITY.
- **Ubicación original:** `src/main/resources/application.properties:28`.
- **Estado original y actual:** OPEN en el análisis de referencia; `RESOLVED / FALSE_POSITIVE` en la API de SonarQube Cloud.
- **Evidencia ANTES:** [metadatos históricos](../../antes/AaGdRnvH97lfKRteM0Zb.json). No existe PNG válido porque la incidencia dejó de estar OPEN/CONFIRMED antes de la captura.

## Problema y análisis

SonarQube señaló que el máximo de archivo superaba su umbral genérico de 8 MiB. El valor respondía al requisito funcional de imágenes y los endpoints de carga están bajo `/api/**`, protegidos mediante JWT Bearer y autorización administrativa. El límite es finito y no equivale a aceptar cargas sin restricción. Reducirlo sin un requisito funcional nuevo rompería cargas válidas.

## Código anterior y decisión

```properties
spring.servlet.multipart.max-file-size=10MB
```

No se alteró el límite para silenciar la regla. La disposición es **falso positivo justificado**. Deben mantenerse además los límites del proxy y la observación de consumo de recursos en producción.

## Pruebas y resultado

La suite `CorsConfigTest,SecurityConfigIntegrationTest` comprobó la cadena de seguridad stateless y la denegación de rutas administrativas sin credenciales: 19 pruebas, 0 fallos, 0 errores. No se ejecutó una prueba multipart de tamaño porque esta incidencia no produjo un cambio de comportamiento.

**Estado P0:** falso positivo justificado y verificado; sin cambio de código específico.
