const fs = require('node:fs');
const path = require('node:path');

const root = path.resolve(__dirname, '..', '..');
const classification = JSON.parse(fs.readFileSync(
  path.join(root, 'docs', 'sonarqube-evidencias', 'clasificacion-incidencias.json'), 'utf8'));
const files = [...new Set(classification.classifications
  .filter(issue => issue.priority === 'P2' && issue.rule === 'java:S8688')
  .map(issue => issue.file))];

let changedFiles = 0;
let replacements = 0;
const missingFiles = [];
for (const relative of files) {
  let filename = path.join(root, ...relative.split('/'));
  if (!fs.existsSync(filename)) {
    const basename = path.basename(relative);
    const matches = [];
    const visit = directory => {
      for (const entry of fs.readdirSync(directory, { withFileTypes: true })) {
        const candidate = path.join(directory, entry.name);
        if (entry.isDirectory()) visit(candidate);
        else if (entry.name === basename) matches.push(candidate);
      }
    };
    visit(path.join(root, 'src', 'main', 'java'));
    if (matches.length === 0) {
      missingFiles.push(relative);
      continue;
    }
    if (matches.length !== 1) throw new Error(`No se pudo resolver ${relative}: ${matches.length} coincidencias`);
    filename = matches[0];
  }
  let source = fs.readFileSync(filename, 'utf8');
  const original = source;
  const substitutions = [
    [/LocalDateTime\.now\(\)/g, 'BusinessTime.now()'],
    [/OffsetDateTime\.now\(\)/g, 'BusinessTime.nowOffset()'],
    [/LocalDate\.now\(\)/g, 'BusinessTime.today()'],
  ];
  for (const [pattern, replacement] of substitutions) {
    source = source.replace(pattern, () => {
      replacements++;
      return replacement;
    });
  }
  if (source === original) continue;
  if (!source.includes('import com.isanorte.constructora_api.util.BusinessTime;')) {
    source = source.replace(/^(package [^;]+;\r?\n)/,
      '$1\nimport com.isanorte.constructora_api.util.BusinessTime;\n');
  }
  fs.writeFileSync(filename, source, 'utf8');
  changedFiles++;
}

process.stdout.write(JSON.stringify({ changedFiles, replacements, missingFiles }) + '\n');
