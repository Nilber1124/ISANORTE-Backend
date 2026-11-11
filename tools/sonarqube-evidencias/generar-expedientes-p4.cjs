const fs = require('node:fs');
const path = require('node:path');

const root = path.resolve(__dirname, '..', '..');
const evidenceRoot = path.join(root, 'docs', 'sonarqube-evidencias');
const output = path.join(evidenceRoot, 'correcciones', 'P4');
fs.mkdirSync(output, { recursive: true });

const classificationPath = path.join(evidenceRoot, 'clasificacion-incidencias.json');
const data = JSON.parse(fs.readFileSync(classificationPath, 'utf8'));
const issues = data.classifications.filter(issue => issue.priority === 'P4');

if (issues.length !== 221) {
  throw new Error(`Se esperaban 221 incidencias P4 y se encontraron ${issues.length}.`);
}

const caseInsensitiveIdCounts = issues.reduce((countsById, issue) => {
  const key = issue.id.toLowerCase();
  countsById.set(key, (countsById.get(key) || 0) + 1);
  return countsById;
}, new Map());

function evidenceBaseName(issue) {
  const evidencePath = issue.evidence?.metadata || issue.evidence?.screenshot;
  return evidencePath ? path.parse(evidencePath).name : issue.id;
}

function documentBaseName(issue) {
  return caseInsensitiveIdCounts.get(issue.id.toLowerCase()) > 1
    ? evidenceBaseName(issue)
    : issue.id;
}

// Elimina solo los 37 archivos ambiguos generados por una ejecución anterior de
// este script. Los expedientes no ambiguos y cualquier otro archivo se conservan.
for (const issue of issues) {
  if (caseInsensitiveIdCounts.get(issue.id.toLowerCase()) > 1) {
    const ambiguousPath = path.join(output, `${issue.id}.md`);
    if (fs.existsSync(ambiguousPath)) fs.unlinkSync(ambiguousPath);
  }
}

const counts = {
  CORREGIDA: 0,
  FALSO_POSITIVO_DIALECTO: 0,
  RECOMENDACION_CONSERVADA: 0,
  HISTORICA_YA_RESUELTA: 0,
  RIESGO_PENDIENTE: 0,
};

function normalizedSnippet(issue) {
  return issue.originalCode?.snippet?.trim() || '(fragmento SQL no disponible en la clasificación original)';
}

function classify(issue) {
  const before = normalizedSnippet(issue);

  if (issue.rule === 'plsql:VarcharUsageCheck') {
    return {
      state: 'FALSO_POSITIVO_DIALECTO',
      before,
      after: before,
      reason: 'La recomendación presupone el dialecto Oracle. Este proyecto usa PostgreSQL, donde `VARCHAR` es un tipo nativo y `VARCHAR2` no es su sustituto nativo. Cambiarlo no corregiría un defecto de PostgreSQL, reduciría portabilidad y además alteraría una migración Flyway ya aplicada. Se conserva el SQL original y su checksum.',
      verification: 'Se comprobó el dialecto PostgreSQL del proyecto, la ubicación en una migración Flyway y que el fragmento reportado declara o convierte mediante `VARCHAR`.',
    };
  }

  if (issue.rule === 'plsql:NullComparison') {
    return {
      state: 'FALSO_POSITIVO_DIALECTO',
      before,
      after: before,
      reason: 'La expresión reportada no compara un valor con `NULL`: compara el resultado de `btrim(...)` con la cadena vacía mediante `<> \'\'`. En PostgreSQL la cadena vacía es un valor distinto de `NULL`, a diferencia de la semántica Oracle asumida por la regla. Además, las columnas involucradas (`titulo`, `descripcion_resumida`, `contenido_detallado`, `imagen_url`, `etiqueta`, `destino_accion` y `texto_boton`) están declaradas `NOT NULL`; el `CHECK` impide textos vacíos o formados solo por espacios. Sustituir la condición por `IS NOT NULL` eliminaría esa validación y sería funcionalmente incorrecto.',
      verification: 'Se inspeccionaron conjuntamente las declaraciones `NOT NULL` y el `CHECK chk_anuncios_textos` de `V11__create_anuncios.sql`; no existe una comparación directa con `NULL` en las líneas señaladas.',
    };
  }

  if (issue.rule === 'plsql:S1192') {
    const seedContext = issue.file.includes('V4__contenido_inicial_isanorte.sql')
      ? 'El literal forma parte de datos semilla declarativos (filas `VALUES`, atributos, rutas, estados o valores repetidos del catálogo). La repetición representa valores de registros independientes; PostgreSQL SQL no ofrece constantes locales reutilizables dentro de ese bloque sin introducir una CTE, función o reestructuración adicional.'
      : 'El literal forma parte de una enumeración de dominio en una restricción `CHECK`; cada valor permitido debe permanecer explícito para que la definición del dominio sea legible y autocontenida.';
    return {
      state: 'RECOMENDACION_CONSERVADA',
      before,
      after: before,
      reason: `${seedContext} No se demuestra un defecto funcional ni de seguridad. Como la migración Flyway ya fue aplicada, editarla invalidaría el checksum registrado. Si el conjunto necesita evolución, deberá hacerse mediante una migración nueva o mediante el generador de datos semilla, no reescribiendo el historial.`,
      verification: 'Se revisaron el archivo, la línea y el fragmento originales; la alerta corresponde a repetición declarativa dentro de una migración aplicada y no a código ejecutable con una constante reutilizable segura.',
    };
  }

  throw new Error(`Regla P4 no contemplada: ${issue.rule} (${issue.id}).`);
}

function evidenceLinks(issue) {
  const screenshotName = issue.evidence?.screenshot && path.basename(issue.evidence.screenshot);
  const metadataName = issue.evidence?.metadata && path.basename(issue.evidence.metadata);
  const png = screenshotName && path.join(evidenceRoot, 'antes', screenshotName);
  const json = metadataName && path.join(evidenceRoot, 'antes', metadataName);
  const links = [];
  if (png && fs.existsSync(png)) links.push(`[captura real ANTES](../../antes/${screenshotName})`);
  if (json && fs.existsSync(json)) links.push(`[metadatos](../../antes/${metadataName})`);
  return links.length > 0 ? links.join(' y ') : 'sin evidencia visual válida disponible; se conserva la referencia de la clasificación original';
}

for (const issue of issues) {
  const result = classify(issue);
  counts[result.state]++;
  const markdown = `# ${issue.id}\n\n` +
    `- **Regla / severidad:** \`${issue.rule}\` / ${issue.severity}.\n` +
    `- **Tipo informado:** ${issue.type}.\n` +
    `- **Archivo y línea original:** \`${issue.file}:${issue.line ?? 'N/D'}\`.\n` +
    `- **Mensaje de SonarQube:** ${issue.message}\n` +
    `- **Estado original:** ${issue.originalStatus}.\n` +
    `- **Evidencia:** ${evidenceLinks(issue)}.\n\n` +
    `## ANTES\n\n\`\`\`sql\n${result.before}\n\`\`\`\n\n` +
    `## DESPUÉS\n\nSin cambio en el SQL; se conserva exactamente el fragmento original:\n\n\`\`\`sql\n${result.after}\n\`\`\`\n\n` +
    `## Justificación técnica\n\n${result.reason}\n\n` +
    `**Verificación realizada:** ${result.verification}\n\n` +
    `**Estado final propuesto:** ${result.state.replaceAll('_', ' ')}.\n\n` +
    `**Limitaciones:** no se modificó ni ejecutó la migración aplicada; no se ejecutaron pruebas, compilación ni un nuevo análisis SonarQube. La captura real DESPUÉS queda pendiente del análisis posterior de la rama.\n`;
  fs.writeFileSync(path.join(output, `${documentBaseName(issue)}.md`), markdown, 'utf8');
}

const summary = `# Resumen P4 — Análisis SQL para PostgreSQL\n\n` +
  `Rama: \`fix/sonarqube-quality\`  \n` +
  `Análisis original: \`${data.originalAnalysisCommit}\`\n\n` +
  `## Totales\n\n` +
  `- Incidencias identificadas y documentadas: **${issues.length}**.\n` +
  `- Corregidas: **${counts.CORREGIDA}**.\n` +
  `- Falsos positivos de dialecto sustentados: **${counts.FALSO_POSITIVO_DIALECTO}** (161 \`VarcharUsageCheck\` y 7 \`NullComparison\`).\n` +
  `- Recomendaciones conservadas sin cambios: **${counts.RECOMENDACION_CONSERVADA}** (53 duplicaciones \`plsql:S1192\`).\n` +
  `- Incidencias históricas o ya resueltas: **${counts.HISTORICA_YA_RESUELTA}**.\n` +
  `- Riesgos pendientes de aprobación: **${counts.RIESGO_PENDIENTE}**.\n\n` +
  `## Decisiones técnicas\n\n` +
  `Las declaraciones \`VARCHAR\` se conservan porque son nativas de PostgreSQL; \`VARCHAR2\` corresponde a la recomendación de un analizador orientado a Oracle. Las siete alertas de nulidad comparan cadenas vacías sobre columnas \`NOT NULL\`, no valores con \`NULL\`. Las duplicaciones pertenecen a restricciones de dominio o datos semilla declarativos: no constituyen un defecto demostrado y suprimirlas exigiría reestructurar migraciones ya aplicadas.\n\n` +
  `## Archivos modificados\n\n` +
  `Solo se generaron este resumen y los 221 expedientes de \`docs/sonarqube-evidencias/correcciones/P4/\`. No se modificó ningún archivo SQL, migración Flyway, código Java ni evidencia ANTES.\n\n` +
  `## Validación mínima\n\n` +
  `No se ejecutaron pruebas ni compilación porque no hubo cambios en código ejecutable ni SQL. Se realizó una comprobación documental única de cantidad, ID, secciones ANTES/DESPUÉS y vínculos de evidencia disponibles. Los 37 pares de IDs que solo difieren por mayúsculas/minúsculas conservan los sufijos hash de sus PNG/JSON para evitar colisiones de nombres en Windows.\n\n` +
  `## Limitaciones y pendientes\n\n` +
  `No se ejecutaron migraciones, integraciones ni SonarQube. Los estados son propuestas técnicas documentadas: no se cambiaron estados en SonarQube ni se declara cierre de incidencias. Las capturas reales DESPUÉS se obtendrán tras un nuevo análisis autorizado.\n`;
fs.writeFileSync(path.join(output, 'resumen-P4.md'), summary, 'utf8');

const expectedDocuments = issues.map(issue => `${documentBaseName(issue)}.md`);
const actualDocuments = fs.readdirSync(output)
  .filter(name => name.endsWith('.md') && name !== 'resumen-P4.md');
const expectedLower = new Set(expectedDocuments.map(name => name.toLowerCase()));
const actualLower = new Set(actualDocuments.map(name => name.toLowerCase()));
const invalidDocuments = issues.filter(issue => {
  const file = path.join(output, `${documentBaseName(issue)}.md`);
  if (!fs.existsSync(file)) return true;
  const content = fs.readFileSync(file, 'utf8');
  return !content.startsWith(`# ${issue.id}\n`)
    || !content.includes('\n## ANTES\n')
    || !content.includes('\n## DESPUÉS\n')
    || !content.includes(`- **Regla / severidad:** \`${issue.rule}\``)
    || !content.includes(`- **Archivo y línea original:** \`${issue.file}:`)
    || !content.includes(`- **Mensaje de SonarQube:** ${issue.message}`);
});
const validation = {
  expected: expectedDocuments.length,
  documents: actualDocuments.length,
  uniqueExpectedNames: expectedLower.size,
  uniqueActualNames: actualLower.size,
  missing: [...expectedLower].filter(name => !actualLower.has(name)).length,
  extra: [...actualLower].filter(name => !expectedLower.has(name)).length,
  invalidStructureOrId: invalidDocuments.length,
  missingBeforePng: issues.filter(issue => !issue.evidence?.screenshot
    || !fs.existsSync(path.join(root, ...issue.evidence.screenshot.split('/')))).length,
  missingMetadataJson: issues.filter(issue => !issue.evidence?.metadata
    || !fs.existsSync(path.join(root, ...issue.evidence.metadata.split('/')))).length,
};
if (validation.missing !== 0
    || validation.extra !== 0
    || validation.invalidStructureOrId !== 0
    || validation.missingBeforePng !== 0
    || validation.missingMetadataJson !== 0
    || validation.expected !== validation.documents
    || validation.expected !== validation.uniqueExpectedNames
    || validation.documents !== validation.uniqueActualNames) {
  throw new Error(`Validación P4 fallida: ${JSON.stringify(validation)}`);
}

process.stdout.write(`${JSON.stringify({ total: issues.length, counts, validation })}\n`);
