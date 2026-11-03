# AaEN8OqqTwfNugJobk3V

- **Regla / severidad:** `plsql:S1192` / CRITICAL.
- **Tipo informado:** CODE_SMELL.
- **Archivo y línea original:** `src/main/resources/db/migration/V2__contenido_dinamico_dominio.sql:12`.
- **Mensaje de SonarQube:** Define a constant instead of duplicating this literal 3 times.
- **Estado original:** OPEN.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OqqTwfNugJobk3V.png) y [metadatos](../../antes/AaEN8OqqTwfNugJobk3V.json).

## ANTES

```sql
tipo IN ('HERO', 'EMPRESA', 'SERVICIOS', 'UNIDAD_NEGOCIO', 'PROYECTOS', 'CONTACTO', 'CTA', 'PERSONALIZADA')
```

## DESPUÉS

Sin cambio en el SQL; se conserva exactamente el fragmento original:

```sql
tipo IN ('HERO', 'EMPRESA', 'SERVICIOS', 'UNIDAD_NEGOCIO', 'PROYECTOS', 'CONTACTO', 'CTA', 'PERSONALIZADA')
```

## Justificación técnica

El literal forma parte de una enumeración de dominio en una restricción `CHECK`; cada valor permitido debe permanecer explícito para que la definición del dominio sea legible y autocontenida. No se demuestra un defecto funcional ni de seguridad. Como la migración Flyway ya fue aplicada, editarla invalidaría el checksum registrado. Si el conjunto necesita evolución, deberá hacerse mediante una migración nueva o mediante el generador de datos semilla, no reescribiendo el historial.

**Verificación realizada:** Se revisaron el archivo, la línea y el fragmento originales; la alerta corresponde a repetición declarativa dentro de una migración aplicada y no a código ejecutable con una constante reutilizable segura.

**Estado final propuesto:** RECOMENDACION CONSERVADA.

**Limitaciones:** no se modificó ni ejecutó la migración aplicada; no se ejecutaron pruebas, compilación ni un nuevo análisis SonarQube. La captura real DESPUÉS queda pendiente del análisis posterior de la rama.
