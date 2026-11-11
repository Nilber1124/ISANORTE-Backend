# AaGdRnvH97lfKRteM0Zc — límite de petición multipart

- **Regla / severidad:** `java:S5693` / MAJOR / VULNERABILITY.
- **Ubicación original:** `src/main/resources/application.properties:29`.
- **Estado original y actual:** OPEN en el análisis de referencia; `RESOLVED / FALSE_POSITIVE` en la API de SonarQube Cloud.
- **Evidencia ANTES:** [metadatos históricos](../../antes/AaGdRnvH97lfKRteM0Zc.json). No existe PNG válido porque la incidencia dejó de estar OPEN/CONFIRMED antes de la captura.

## Problema y análisis

La petición multipart total permitía 15 MiB, por encima del umbral genérico de 8 MiB. Es un máximo explícito, acotado y coherente con un archivo de hasta 10 MiB más la sobrecarga multipart. Las operaciones de carga requieren autenticación y autorización administrativa. Disminuir el valor sin revisar el contrato funcional podría rechazar solicitudes legítimas.

## Código anterior y decisión

```properties
spring.servlet.multipart.max-request-size=15MB
```

No se modificó el valor. La disposición es **falso positivo justificado**. Si estos endpoints se exponen a otro perfil, deben reevaluarse rate limiting, límites del proxy y cuotas.

## Pruebas y resultado

La suite `CorsConfigTest,SecurityConfigIntegrationTest` validó la cadena de seguridad: 19 pruebas, 0 fallos, 0 errores. No se ejecutó una prueba multipart de tamaño al no existir cambio funcional en esta incidencia.

**Estado P0:** falso positivo justificado y verificado; sin cambio de código específico.
