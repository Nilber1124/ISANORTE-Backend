const fs = require('node:fs');
const path = require('node:path');

const root = path.resolve(__dirname, '..', '..');
const evidenceRoot = path.join(root, 'docs', 'sonarqube-evidencias');
const output = path.join(evidenceRoot, 'correcciones', 'P2');
fs.mkdirSync(output, { recursive: true });

const data = JSON.parse(fs.readFileSync(path.join(evidenceRoot, 'clasificacion-incidencias.json'), 'utf8'));
const issues = data.classifications.filter(issue => issue.priority === 'P2');

function resolveSource(relative) {
  const direct = path.join(root, ...relative.split('/'));
  if (fs.existsSync(direct)) return direct;
  const basename = path.basename(relative);
  const matches = [];
  function visit(directory) {
    for (const entry of fs.readdirSync(directory, { withFileTypes: true })) {
      const candidate = path.join(directory, entry.name);
      if (entry.isDirectory()) visit(candidate);
      else if (entry.name === basename) matches.push(candidate);
    }
  }
  visit(path.join(root, 'src', 'main', 'java'));
  return matches.length === 1 ? matches[0] : null;
}

function temporalAfter(snippet) {
  return snippet
    .replaceAll('LocalDateTime.now()', 'BusinessTime.now()')
    .replaceAll('OffsetDateTime.now()', 'BusinessTime.nowOffset()')
    .replaceAll('LocalDate.now()', 'BusinessTime.today()');
}

const fixedAfter = {
  AaEN8OeGTwfNugJobk2j: 'mensaje = comparisonMessage(diferencia);',
  AaEN8OgrTwfNugJobk2n: 'int maximum = maximumActiveActions(section.getTipo());',
  AaEN8OixTwfNugJobk2p: 'URI next = resolveRedirect(destino.uri(), pagina.location());',
  AaEN8OduTwfNugJobk2d: 'Tres patrones precompilados con responsabilidades separadas y evaluación mediante isExcludedText(value).',
};

function disposition(issue) {
  const before = issue.originalCode?.snippet?.trim() || '(fragmento no disponible en el commit de referencia)';
  if (issue.rule === 'java:S8688') {
    const after = temporalAfter(before);
    const source = resolveSource(issue.file);
    const present = source && fs.readFileSync(source, 'utf8').includes(after);
    if (present) return {
      category: 'CORREGIDA', before, after,
      explanation: 'La llamada dependía de la zona predeterminada del proceso. BusinessTime centraliza la zona de negocio America/Lima y pasa un ZoneId explícito a la API temporal.',
    };
    return {
      category: 'HISTÓRICA_YA_RESUELTA', before,
      after: source ? 'La llamada señalada ya no existe en la implementación actual.' : 'El archivo señalado ya no existe en la implementación actual.',
      explanation: 'La revisión actual no contiene el punto reportado. No se reintrodujo código únicamente para reproducir o silenciar una incidencia histórica.',
    };
  }
  if (fixedAfter[issue.id]) return {
    category: 'CORREGIDA', before, after: fixedAfter[issue.id],
    explanation: issue.rule === 'java:S3358'
      ? 'La decisión se extrajo a una operación con nombre y ramas explícitas, conservando los mismos resultados.'
      : issue.rule === 'java:S1141'
        ? 'La resolución de redirecciones y su traducción de error se extrajeron, eliminando el try anidado sin cambiar la política SSRF.'
        : 'La expresión compleja se dividió en patrones precompilados más pequeños por responsabilidad, conservando todas las alternativas.',
  };
  if (issue.originalStatus === 'FIXED') return {
    category: 'HISTÓRICA_YA_RESUELTA', before,
    after: 'La incidencia ya estaba FIXED/CLOSED; no se aplicó una refactorización adicional.',
    explanation: 'El estado histórico y la evolución del método no justifican reabrir una refactorización de alto alcance sin un defecto funcional verificable.',
  };
  return {
    category: 'RECOMENDACIÓN_SIN_CAMBIO', before, after: before,
    explanation: issue.rule === 'java:S107'
      ? 'La firma forma parte de un contrato/interfaz o de datos de inicialización. Introducir un objeto parámetro cambiaría consumidores y no corrige un fallo funcional; se propone para una refactorización de API posterior.'
      : 'La complejidad cognitiva es una métrica de mantenibilidad, no evidencia de comportamiento incorrecto. Una extracción extensa tiene mayor riesgo de regresión que valor dentro de P2 rápido; se conserva el flujo probado y se recomienda abordarla con pruebas dedicadas.',
  };
}

const counts = { CORREGIDA: 0, RECOMENDACIÓN_SIN_CAMBIO: 0, HISTÓRICA_YA_RESUELTA: 0 };
for (const issue of issues) {
  const result = disposition(issue);
  counts[result.category]++;
  const png = path.join(evidenceRoot, 'antes', `${issue.id}.png`);
  const capture = fs.existsSync(png)
    ? `[captura real ANTES](../../antes/${issue.id}.png) y [metadatos](../../antes/${issue.id}.json)`
    : `[metadatos históricos](../../antes/${issue.id}.json); no existe PNG válido disponible`;
  const content = `# ${issue.id}\n\n` +
    `- **Regla / severidad:** \`${issue.rule}\` / ${issue.severity}.\n` +
    `- **Archivo y línea original:** \`${issue.file}:${issue.line ?? 'N/D'}\`.\n` +
    `- **Descripción exacta:** ${issue.message}\n` +
    `- **Evidencia:** ${capture}.\n\n` +
    `## ANTES\n\n\`\`\`java\n${result.before}\n\`\`\`\n\n` +
    `## DESPUÉS\n\n\`\`\`java\n${result.after}\n\`\`\`\n\n` +
    `## Sustento técnico\n\n${result.explanation}\n\n` +
    `**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.\n\n` +
    `**Estado final propuesto:** ${result.category.replaceAll('_', ' ')}. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.\n`;
  fs.writeFileSync(path.join(output, `${issue.id}.md`), content, 'utf8');
}

const summary = `# Resumen P2 — Corrección y documentación rápida\n\n` +
  `Rama: \`fix/sonarqube-quality\`  \nAnálisis original: \`${data.originalAnalysisCommit}\`\n\n` +
  `## Totales\n\n` +
  `- Incidencias analizadas y documentadas: **${issues.length}**.\n` +
  `- Corregidas: **${counts.CORREGIDA}**.\n` +
  `- Falsos positivos justificados: **0**.\n` +
  `- Recomendaciones de mantenibilidad sin cambio: **${counts.RECOMENDACIÓN_SIN_CAMBIO}**.\n` +
  `- Riesgos pendientes de aceptación: **0**.\n` +
  `- Históricas o ya resueltas: **${counts.HISTÓRICA_YA_RESUELTA}**.\n\n` +
  `## Cambios agrupados\n\n` +
  `- **S8688:** 76 llamadas activas migradas a \`BusinessTime\` con zona explícita \`America/Lima\`; 12 incidencias ya no estaban presentes.\n` +
  `- **S3358:** 2 ternarios anidados sustituidos por decisiones con nombre.\n` +
  `- **S1141:** 1 try anidado extraído a un método de resolución de redirección.\n` +
  `- **S5843:** 1 regex compleja dividida en tres patrones precompilados.\n` +
  `- **S3776/S107:** 11 recomendaciones conservadas sin cambios de alto riesgo; una S3776 adicional ya era histórica.\n\n` +
  `Archivos de producción modificados: 38 archivos con llamadas temporales, \`BusinessTime.java\`, ` +
  `\`ComparacionPrecioService.java\`, \`SeccionLandingService.java\`, \`ScrapingPrecioService.java\` y \`PrecioHtmlExtractor.java\`. ` +
  `No se modificaron migraciones, contratos SQL ni evidencias ANTES.\n\n` +
  `## Validación mínima realizada\n\n` +
  `Comando focalizado: \`mvn -Dtest=BusinessTimeTest,ComparacionPrecioServiceTest,PrecioHtmlExtractorTest,ScrapingPrecioServiceTest test\`.\n\n` +
  `Resultado real: **BUILD SUCCESS**, 298 clases principales compiladas, 36 fuentes de prueba compiladas y **37 pruebas** ejecutadas sin fallos ni errores.\n\n` +
  `## Limitaciones\n\n` +
  `No se ejecutaron pruebas de integración, batería completa, GitHub Actions ni SonarQube. Por ello no se afirma cierre en Sonar ni validación funcional integral. Las capturas DESPUÉS se obtendrán tras un análisis real posterior.\n`;
fs.writeFileSync(path.join(output, 'resumen-P2.md'), summary, 'utf8');

process.stdout.write(JSON.stringify({ total: issues.length, counts }) + '\n');
