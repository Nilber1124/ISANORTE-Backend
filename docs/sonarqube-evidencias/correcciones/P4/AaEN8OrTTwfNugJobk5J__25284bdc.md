# AaEN8OrTTwfNugJobk5J

- **Regla / severidad:** `plsql:VarcharUsageCheck` / MAJOR.
- **Tipo informado:** CODE_SMELL.
- **Archivo y línea original:** `src/main/resources/db/migration/V1__baseline_schema_actual.sql:222`.
- **Mensaje de SonarQube:** Use VARCHAR2 instead of VARCHAR.
- **Estado original:** OPEN.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OrTTwfNugJobk5J__25284bdc.png) y [metadatos](../../antes/AaEN8OrTTwfNugJobk5J__25284bdc.json).

## ANTES

```sql
grupo VARCHAR(100),
```

## DESPUÉS

Sin cambio en el SQL; se conserva exactamente el fragmento original:

```sql
grupo VARCHAR(100),
```

## Justificación técnica

La recomendación presupone el dialecto Oracle. Este proyecto usa PostgreSQL, donde `VARCHAR` es un tipo nativo y `VARCHAR2` no es su sustituto nativo. Cambiarlo no corregiría un defecto de PostgreSQL, reduciría portabilidad y además alteraría una migración Flyway ya aplicada. Se conserva el SQL original y su checksum.

**Verificación realizada:** Se comprobó el dialecto PostgreSQL del proyecto, la ubicación en una migración Flyway y que el fragmento reportado declara o convierte mediante `VARCHAR`.

**Estado final propuesto:** FALSO POSITIVO DIALECTO.

**Limitaciones:** no se modificó ni ejecutó la migración aplicada; no se ejecutaron pruebas, compilación ni un nuevo análisis SonarQube. La captura real DESPUÉS queda pendiente del análisis posterior de la rama.
