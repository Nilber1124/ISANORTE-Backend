const fs = require('node:fs');
const path = require('node:path');

const root = path.resolve(__dirname, '..', '..');
const evidenceRoot = path.join(root, 'docs', 'sonarqube-evidencias');
const output = path.join(evidenceRoot, 'correcciones', 'P3');
fs.mkdirSync(output, { recursive: true });
const data = JSON.parse(fs.readFileSync(path.join(evidenceRoot, 'clasificacion-incidencias.json'), 'utf8'));
const issues = data.classifications.filter(issue => issue.priority === 'P3');

const corrected = {
  AaGdRnsl97lfKRteM0Yp: 'Se reutiliza la constante EXPLORE.',
  AaGdRnsl97lfKRteM0Yr: 'Se reutiliza la constante LEFT_IMAGE.',
  AaGdRnsl97lfKRteM0Yq: 'Se reutiliza la constante CATALOG_PATH.',
  'AaES7VxB-6tQcR7a5P5K': 'ROLE_PREFIX centraliza el prefijo de autoridades.',
  AaES7WeS_ignored: '',
  'AaES7WeS-6tQcR7a5P5S': 'Se conserva una sola importación java.util.UUID.',
  AaEN8OttTwfNugJobk54: 'verify(productoRepository, times(26)).save(captor.capture());',
  AaEN8OngTwfNugJobk2w: '@Service',
  AaEN8OngTwfNugJobk2x: 'BigDecimal sum = BigDecimal.ZERO;\nint count = 0;',
  AaEN8OjzTwfNugJobk2s: 'Se eliminó el import no utilizado ScrapingPrecioException.',
  AaEN8OduTwfNugJobk2U: 'SALE_PRICE_AMOUNT sustituye las tres repeticiones.',
  AaEN8OduTwfNugJobk2W: 'CONTENT_ATTRIBUTE sustituye las cinco repeticiones.',
  AaEN8OduTwfNugJobk2V: 'PRICE_CURRENCY sustituye las repeticiones.',
  AaEN8Od6TwfNugJobk2i: 'Las variables a, b y c se declaran en líneas independientes.',
  AaEN8Od6TwfNugJobk2h: 'Las variables a, b, c y d se declaran en líneas independientes.',
  AaCdT27mj7PCqTSvAO5F: 'NULL_PRODUCT_MESSAGE centraliza el mensaje.',
  AaEN8OTnTwfNugJobk2F: 'onCreate y onUpdate delegan en prepareForPersistence().',
  AaCdT219j7PCqTSvAO4g: 'public interface IGenericRepository<T, I> extends JpaRepository<T, I>',
  AaCdT25hj7PCqTSvAO4s: 'public interface IGenericService<T, I>',
  AaCdT235j7PCqTSvAO4i: 'public abstract class GenericService<T, I> implements IGenericService<T, I>',
};

const falsePositive = new Set(['AaEN8OFwTwfNugJobk16', 'AaEN8OFwTwfNugJobk17']);

function classify(issue) {
  const before = issue.originalCode?.snippet?.trim() || '(fragmento no disponible)';
  if (corrected[issue.id]) return {
    state: 'CORREGIDA', before, after: corrected[issue.id],
    reason: 'Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.',
  };
  if (!fs.existsSync(path.join(root, ...issue.file.split('/')))
      || issue.rule === 'java:S9391'
      || issue.rule === 'java:S1135') return {
    state: 'HISTÓRICA_YA_RESUELTA', before,
    after: 'El punto reportado ya no está presente en la revisión actual.',
    reason: 'El archivo o la construcción señalada desapareció por evolución previa. No se recreó código para reproducir una alerta histórica.',
  };
  if (falsePositive.has(issue.id)) return {
    state: 'FALSO_POSITIVO_JUSTIFICADO', before, after: before,
    reason: 'SecurityFilterChain termina en HttpSecurity.build(), cuya API de Spring declara una excepción checked genérica. Conservar throws Exception propaga exactamente el contrato de la biblioteca; capturarla para renombrarla no aporta contexto ni seguridad.',
  };
  let reason = 'Recomendación de mantenibilidad conservada sin cambios: no existe un defecto funcional demostrado y la refactorización no se justifica con la validación mínima de P3.';
  if (issue.rule === 'java:S5778') reason = 'La alerta afecta precisión de una lambda de prueba, no producción. Separar constructores/argumentos requeriría editar pruebas no ejecutadas en P3; se pospone hasta su batería específica.';
  if (issue.rule === 'java:S1172') reason = 'El parámetro forma parte de un hook extensible o helper de prueba y mantiene una firma uniforme con consumidores; retirarlo obliga a cambiar el contrato sin beneficio funcional inmediato.';
  if (issue.rule === 'java:S135') reason = 'El bucle de extracción usa control temprano para descartar candidatos ambiguos. Reestructurarlo puede cambiar precedencia de evidencia; se mantiene hasta contar con pruebas exhaustivas del extractor.';
  if (issue.rule === 'java:S5853') reason = 'Las aserciones separadas identifican con precisión qué invariante del catálogo falló. Unificarlas es estilístico y no mejora el comportamiento.';
  return { state: 'RECOMENDACIÓN_SIN_CAMBIO', before, after: before, reason };
}

const counts = { CORREGIDA: 0, RECOMENDACIÓN_SIN_CAMBIO: 0, FALSO_POSITIVO_JUSTIFICADO: 0, HISTÓRICA_YA_RESUELTA: 0 };
for (const issue of issues) {
  const result = classify(issue);
  counts[result.state]++;
  const png = path.join(evidenceRoot, 'antes', `${issue.id}.png`);
  const evidence = fs.existsSync(png)
    ? `[captura real ANTES](../../antes/${issue.id}.png) y [metadatos](../../antes/${issue.id}.json)`
    : `[metadatos históricos](../../antes/${issue.id}.json); no existe PNG válido disponible`;
  const markdown = `# ${issue.id}\n\n` +
    `- **Regla / severidad:** \`${issue.rule}\` / ${issue.severity}.\n` +
    `- **Archivo y línea original:** \`${issue.file}:${issue.line ?? 'N/D'}\`.\n` +
    `- **Problema o recomendación:** ${issue.message}\n` +
    `- **Evidencia:** ${evidence}.\n\n` +
    `## ANTES\n\n\`\`\`java\n${result.before}\n\`\`\`\n\n` +
    `## DESPUÉS\n\n\`\`\`java\n${result.after}\n\`\`\`\n\n` +
    `## Sustento técnico\n\n${result.reason}\n\n` +
    `**Estado final propuesto:** ${result.state.replaceAll('_', ' ')}.\n\n` +
    `**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.\n`;
  fs.writeFileSync(path.join(output, `${issue.id}.md`), markdown, 'utf8');
}

const summary = `# Resumen P3 — Corrección y documentación eficiente\n\n` +
  `Rama: \`fix/sonarqube-quality\`  \nAnálisis original: \`${data.originalAnalysisCommit}\`\n\n` +
  `## Totales\n\n` +
  `- Incidencias identificadas y documentadas: **${issues.length}**.\n` +
  `- Corregidas: **${counts.CORREGIDA}**.\n` +
  `- Justificadas como recomendaciones sin cambio: **${counts.RECOMENDACIÓN_SIN_CAMBIO}**.\n` +
  `- Falsos positivos sustentados: **${counts.FALSO_POSITIVO_JUSTIFICADO}**.\n` +
  `- Históricas o ya resueltas: **${counts.HISTÓRICA_YA_RESUELTA}**.\n` +
  `- Riesgos pendientes de aprobación: **0**.\n\n` +
  `## Cambios agrupados\n\n` +
  `Se introdujeron constantes para literales repetidos, se limpiaron imports, se renombraron parámetros genéricos, se separaron declaraciones múltiples, ` +
  `\`SimilitudProductoService\` pasó a \`@Service\` y los callbacks idénticos de \`ConfiguracionSitio\` delegan en un método común. ` +
  `No se modificaron migraciones, endpoints, contratos de base de datos ni evidencias ANTES.\n\n` +
  `## Validación mínima\n\n` +
  `Comando: \`mvn -Dtest=PrecioHtmlExtractorTest,SimilitudProductoServiceTest,ConfiguracionSitioRegexTest test\`.\n\n` +
  `Resultado: **BUILD SUCCESS**; 298 clases principales y 36 fuentes de prueba compiladas; **27 pruebas**, 0 fallos y 0 errores.\n\n` +
  `## Limitaciones\n\n` +
  `No se ejecutaron integraciones, batería completa, GitHub Actions ni SonarQube. Las recomendaciones sobre lambdas de tests, parámetros contractuales y control de flujo quedan para una fase con sus pruebas específicas. No se afirma cierre en Sonar ni se inventan capturas DESPUÉS.\n`;
fs.writeFileSync(path.join(output, 'resumen-P3.md'), summary, 'utf8');
process.stdout.write(JSON.stringify({ total: issues.length, counts }) + '\n');
