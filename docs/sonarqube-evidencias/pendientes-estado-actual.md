# Estado actual de las 17 incidencias sin captura

Consulta de solo lectura a la API oficial de SonarQube Cloud: `2026-11-02T22:03:58.249Z`

Endpoint: `/api/issues/search?issues=<17 IDs>&ps=100&additionalFields=_all`.

| ID | Regla | status | issueStatus | resolution | Justificación verificada | Evidencia histórica |
|---|---|---|---|---|---|---|
| `AaGdRnvH97lfKRteM0Zb` | `java:S5693` | RESOLVED | FALSE_POSITIVE | FALSE-POSITIVE | La API registra falso positivo: carga máxima de 10 MB en endpoints multipart protegidos por JWT de administrador; necesidad de negocio documentada. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaGdRnvH97lfKRteM0Zc` | `java:S5693` | RESOLVED | FALSE_POSITIVE | FALSE-POSITIVE | La API registra falso positivo: 15 MB limita la petición multipart total y los endpoints exigen rol administrador. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaES7WjO-6tQcR7a5P5V` | `java:S6437` | CLOSED | FIXED | FIXED | SonarQube cerró la detección del secreto por análisis. Debe verificarse externamente que la credencial fue rotada. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaES7WjO-6tQcR7a5P5W` | `secrets:S6703` | CLOSED | FIXED | FIXED | SonarQube cerró la contraseña expuesta por análisis. El cierre no demuestra por sí solo la rotación. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaEN8OBpTwfNugJobk14` | `java:S8989` | CLOSED | FIXED | FIXED | El análisis dejó de detectar la ausencia de política rollback en DemoProductSeeder. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaEN8OkrTwfNugJobk2t` | `java:S3776` | CLOSED | FIXED | FIXED | El análisis dejó de detectar la complejidad cognitiva en CotizacionService. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaEN8OpSTwfNugJobk21` | `java:S5122` | CLOSED | FIXED | FIXED | El análisis dejó de detectar la configuración CORS localizada en ArchivoController. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaEN8OngTwfNugJobk2y` | `java:S9391` | CLOSED | FIXED | REMOVED | La ubicación/código que originó la incidencia fue eliminada del análisis actual. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaCyaha0yz3W5ou-kdGf` | `java:S5778` | CLOSED | FIXED | FIXED | La lambda assertThrows dejó de incumplir la regla en el análisis posterior. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaCyaha0yz3W5ou-kdGg` | `java:S5778` | CLOSED | FIXED | FIXED | La lambda assertThrows dejó de incumplir la regla en el análisis posterior. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaCyaha0yz3W5ou-kdGh` | `java:S5778` | CLOSED | FIXED | FIXED | La lambda assertThrows dejó de incumplir la regla en el análisis posterior. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaCyaha0yz3W5ou-kdGi` | `java:S5778` | CLOSED | FIXED | FIXED | La lambda assertThrows dejó de incumplir la regla en el análisis posterior. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaEN5JebD6mrdRS6eNmR` | `java:S9391` | CLOSED | FIXED | FIXED | El loop señalado dejó de incumplir la regla en el análisis posterior. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaEN5JlyD6mrdRS6eNmS` | `java:S9391` | CLOSED | FIXED | REMOVED | La ubicación/código que originó la incidencia fue eliminada del análisis actual. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaCdT29Lj7PCqTSvAO5c` | `java:S6437` | CLOSED | FIXED | FIXED | SonarQube cerró la detección del secreto por análisis; se requiere evidencia operativa de rotación. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaCdT29Lj7PCqTSvAO5d` | `secrets:S6703` | CLOSED | FIXED | FIXED | SonarQube cerró la contraseña expuesta por análisis; se requiere evidencia operativa de rotación. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |
| `AaCdT25rj7PCqTSvAO4t` | `java:S8688` | CLOSED | FIXED | FIXED | El análisis dejó de detectar LocalDateTime.now() sin zona en ImagenProyecto. | Registro original en `sonarqube-issues.json` y código en `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`; sin PNG válido |

## Totales

- 2 `RESOLVED / FALSE_POSITIVE / FALSE-POSITIVE`.
- 13 `CLOSED / FIXED / FIXED`.
- 2 `CLOSED / FIXED / REMOVED`.

Los cuatro hallazgos de secretos cerrados como FIXED requieren comprobar la rotación fuera de SonarQube; la ausencia posterior del literal no demuestra que la credencial haya sido revocada.
