# AaEN8OrBTwfNugJobk3v

- **Regla / severidad:** `plsql:S1192` / CRITICAL.
- **Tipo informado:** CODE_SMELL.
- **Archivo y línea original:** `src/main/resources/db/migration/V4__contenido_inicial_isanorte.sql:231`.
- **Mensaje de SonarQube:** Define a constant instead of duplicating this literal 6 times.
- **Estado original:** OPEN.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OrBTwfNugJobk3v.png) y [metadatos](../../antes/AaEN8OrBTwfNugJobk3v.json).

## ANTES

```sql
NULL, '/images/isadecor-fondo.jpg', NULL, TRUE, TRUE,
```

## DESPUÉS

Sin cambio en el SQL; se conserva exactamente el fragmento original:

```sql
NULL, '/images/isadecor-fondo.jpg', NULL, TRUE, TRUE,
```

## Justificación técnica

El literal forma parte de datos semilla declarativos (filas `VALUES`, atributos, rutas, estados o valores repetidos del catálogo). La repetición representa valores de registros independientes; PostgreSQL SQL no ofrece constantes locales reutilizables dentro de ese bloque sin introducir una CTE, función o reestructuración adicional. No se demuestra un defecto funcional ni de seguridad. Como la migración Flyway ya fue aplicada, editarla invalidaría el checksum registrado. Si el conjunto necesita evolución, deberá hacerse mediante una migración nueva o mediante el generador de datos semilla, no reescribiendo el historial.

**Verificación realizada:** Se revisaron el archivo, la línea y el fragmento originales; la alerta corresponde a repetición declarativa dentro de una migración aplicada y no a código ejecutable con una constante reutilizable segura.

**Estado final propuesto:** RECOMENDACION CONSERVADA.

**Limitaciones:** no se modificó ni ejecutó la migración aplicada; no se ejecutaron pruebas, compilación ni un nuevo análisis SonarQube. La captura real DESPUÉS queda pendiente del análisis posterior de la rama.
