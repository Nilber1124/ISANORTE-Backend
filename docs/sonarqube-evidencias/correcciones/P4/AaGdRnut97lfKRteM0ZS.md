# AaGdRnut97lfKRteM0ZS

- **Regla / severidad:** `plsql:NullComparison` / MAJOR.
- **Tipo informado:** BUG.
- **Archivo y línea original:** `src/main/resources/db/migration/V11__create_anuncios.sql:26`.
- **Mensaje de SonarQube:** Use IS NULL and IS NOT NULL instead of direct NULL comparisons.
- **Estado original:** OPEN.
- **Evidencia:** [captura real ANTES](../../antes/AaGdRnut97lfKRteM0ZS.png) y [metadatos](../../antes/AaGdRnut97lfKRteM0ZS.json).

## ANTES

```sql
btrim(titulo) <> '' AND btrim(descripcion_resumida) <> '' AND
```

## DESPUÉS

Sin cambio en el SQL; se conserva exactamente el fragmento original:

```sql
btrim(titulo) <> '' AND btrim(descripcion_resumida) <> '' AND
```

## Justificación técnica

La expresión reportada no compara un valor con `NULL`: compara el resultado de `btrim(...)` con la cadena vacía mediante `<> ''`. En PostgreSQL la cadena vacía es un valor distinto de `NULL`, a diferencia de la semántica Oracle asumida por la regla. Además, las columnas involucradas (`titulo`, `descripcion_resumida`, `contenido_detallado`, `imagen_url`, `etiqueta`, `destino_accion` y `texto_boton`) están declaradas `NOT NULL`; el `CHECK` impide textos vacíos o formados solo por espacios. Sustituir la condición por `IS NOT NULL` eliminaría esa validación y sería funcionalmente incorrecto.

**Verificación realizada:** Se inspeccionaron conjuntamente las declaraciones `NOT NULL` y el `CHECK chk_anuncios_textos` de `V11__create_anuncios.sql`; no existe una comparación directa con `NULL` en las líneas señaladas.

**Estado final propuesto:** FALSO POSITIVO DIALECTO.

**Limitaciones:** no se modificó ni ejecutó la migración aplicada; no se ejecutaron pruebas, compilación ni un nuevo análisis SonarQube. La captura real DESPUÉS queda pendiente del análisis posterior de la rama.
