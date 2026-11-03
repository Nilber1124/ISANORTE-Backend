# Resumen P4 — Análisis SQL para PostgreSQL

Rama: `fix/sonarqube-quality`  
Análisis original: `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`

## Totales

- Incidencias identificadas y documentadas: **221**.
- Corregidas: **0**.
- Falsos positivos de dialecto sustentados: **168** (161 `VarcharUsageCheck` y 7 `NullComparison`).
- Recomendaciones conservadas sin cambios: **53** (53 duplicaciones `plsql:S1192`).
- Incidencias históricas o ya resueltas: **0**.
- Riesgos pendientes de aprobación: **0**.

## Decisiones técnicas

Las declaraciones `VARCHAR` se conservan porque son nativas de PostgreSQL; `VARCHAR2` corresponde a la recomendación de un analizador orientado a Oracle. Las siete alertas de nulidad comparan cadenas vacías sobre columnas `NOT NULL`, no valores con `NULL`. Las duplicaciones pertenecen a restricciones de dominio o datos semilla declarativos: no constituyen un defecto demostrado y suprimirlas exigiría reestructurar migraciones ya aplicadas.

## Archivos modificados

Solo se generaron este resumen y los 221 expedientes de `docs/sonarqube-evidencias/correcciones/P4/`. No se modificó ningún archivo SQL, migración Flyway, código Java ni evidencia ANTES.

## Validación mínima

No se ejecutaron pruebas ni compilación porque no hubo cambios en código ejecutable ni SQL. Se realizó una comprobación documental única de cantidad, ID, secciones ANTES/DESPUÉS y vínculos de evidencia disponibles. Los 37 pares de IDs que solo difieren por mayúsculas/minúsculas conservan los sufijos hash de sus PNG/JSON para evitar colisiones de nombres en Windows.

## Limitaciones y pendientes

No se ejecutaron migraciones, integraciones ni SonarQube. Los estados son propuestas técnicas documentadas: no se cambiaron estados en SonarQube ni se declara cierre de incidencias. Las capturas reales DESPUÉS se obtendrán tras un nuevo análisis autorizado.
