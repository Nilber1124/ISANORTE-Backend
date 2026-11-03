# Evidencias de SonarQube Cloud

Las capturas se generan desde la interfaz real de SonarQube Cloud y se relacionan con la clave estable de cada incidencia.

## Trazabilidad del análisis original

- Proyecto: `Nilber1124_ISANORTE-Backend`
- Rama de trabajo verificada: `fix/sonarqube-quality`
- Revisión exacta de `develop` analizada por SonarQube Cloud: `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`
- Fecha del análisis: `2026-11-02T17:19:42+0000`
- UUID de análisis indicado por la incidencia piloto: `f1aaf36f-0a62-4365-b5a7-7bd4c1eb7da8`
- `develop` local al iniciar la recopilación: `8ade3af0b38cfb81842c84f16580125d5eaf3088`

La revisión analizada fue obtenida de la API de SonarQube Cloud usando la misma sesión autorizada de la captura. La interfaz identifica la rama analizada como `develop`. El `develop` local no contiene actualmente esa revisión, por lo que ambas referencias se conservan por separado y no se equiparan.

## Procedimiento

La automatización está en `tools/sonarqube-evidencias/capturar-incidencia.cjs`. Usa una instalación aislada de Playwright y una sesión efímera: no solicita contraseñas ni tokens y no conserva el estado autenticado al cerrar Chromium.

Para las capturas **ANTES**, se usa `--phase antes`. Para las capturas **DESPUÉS**, primero debe completarse el nuevo análisis de la rama corregida y luego se usa `--phase despues`. En ambos casos, el script valida que la vista contenga el mensaje, la regla, el archivo y la línea antes de guardar el PNG.

## Resultado de la recopilación ANTES

- 409 incidencias procesadas.
- 392 capturas válidas con código renderizado.
- 17 incidencias pendientes porque ya no figuran como `OPEN` o `CONFIRMED`.
- 0 capturas fallidas, incompletas, incorrectas o no verificables en la auditoría final.

Consulta [el índice general](indice.md), [el resumen](resumen-capturas.md) y [la auditoría visual](revision-capturas.json).

## Auditoría y plan de corrección

- [Estado oficial de las 17 incidencias sin captura](pendientes-estado-actual.md)
- [Clasificación humana de las 409 incidencias](clasificacion-incidencias.md)
- [Clasificación estructurada con contexto original](clasificacion-incidencias.json)
- [Plan de corrección priorizado](plan-correcciones.md)

Estos documentos son preparatorios: no implican que el código haya sido modificado ni que una incidencia se considere corregida sin un nuevo análisis.

Cuando dos IDs solo difieren en mayúsculas/minúsculas, se añade un hash corto al nombre para evitar colisiones en Windows. El ID original permanece íntegro en el índice y en los metadatos.
