#!/usr/bin/env node

const fs = require("node:fs");
const path = require("node:path");
const { execFileSync } = require("node:child_process");

const COMMIT = "73615cec38bbd6051dd9aad7a2ef9de06a5e3f93";
const DOCS = path.resolve("docs", "sonarqube-evidencias");

const generic = {
  category: "Mantenibilidad",
  solutionGroup: "Revisión localizada",
  cause: "El código incumple la regla estática indicada por SonarQube.",
  correction: "Aplicar una refactorización localizada que elimine la causa sin cambiar el contrato observable.",
  risk: "MEDIO",
  tests: "Prueba unitaria del comportamiento afectado y prueba de regresión del archivo.",
  disposition: "CORREGIR",
  rationale: "No hay evidencia suficiente para tratarla como falso positivo.",
  priority: "P3",
};

const catalog = {
  "plsql:VarcharUsageCheck": {
    category: "Configuración del analizador / dialecto SQL", solutionGroup: "Falso positivo PostgreSQL VARCHAR",
    cause: "La regla presupone Oracle PL/SQL y recomienda VARCHAR2, pero el proyecto usa PostgreSQL y Flyway PostgreSQL.",
    correction: "No cambiar VARCHAR por VARCHAR2. Configurar el análisis para reconocer PostgreSQL o excluir estas migraciones de reglas PL/SQL incompatibles.",
    risk: "ALTO", tests: "Validación Flyway sobre PostgreSQL real y arranque de contexto; no modificar migraciones aplicadas.",
    disposition: "FALSO_POSITIVO_PROBABLE", rationale: "VARCHAR es válido en PostgreSQL; VARCHAR2 no es el tipo apropiado para este motor.", priority: "P4",
  },
  "plsql:NullComparison": {
    category: "Configuración del analizador / dialecto SQL", solutionGroup: "Falso positivo PostgreSQL cadena vacía",
    cause: "El analizador Oracle interpreta la cadena vacía como NULL; PostgreSQL diferencia '' de NULL y el CHECK valida texto no vacío.",
    correction: "Conservar las comparaciones <> '' y ajustar el perfil/dialecto de análisis; no sustituirlas por IS NOT NULL porque cambiaría la validación.",
    risk: "ALTO", tests: "Migración V11 en PostgreSQL y pruebas de constraints con NULL, cadena vacía y texto válido.",
    disposition: "FALSO_POSITIVO_PROBABLE", rationale: "La recomendación de Oracle no conserva la semántica PostgreSQL del constraint.", priority: "P4",
  },
  "plsql:S1192": {
    category: "Mantenibilidad SQL", solutionGroup: "Literales repetidos en migraciones inmutables",
    cause: "Scripts de datos Flyway repiten literales porque SQL estático no ofrece constantes Java y las migraciones ya aplicadas son inmutables.",
    correction: "Preferir una migración nueva con CTE/VALUES reutilizable solo si mejora claridad; para migraciones aplicadas, aceptar el riesgo o excluir duplicación.",
    risk: "ALTO", tests: "Validación de checksum Flyway, migración desde base vacía y comprobación de datos sembrados.",
    disposition: "RIESGO_ACEPTABLE_PROBABLE", rationale: "Editar migraciones aplicadas rompe checksums; el beneficio es solo de mantenibilidad.", priority: "P4",
  },
  "java:S6437": {
    category: "Seguridad", solutionGroup: "Secretos fuera del repositorio", cause: "Una credencial fue detectada en configuración versionada.",
    correction: "Revocar/rotar la credencial, retirarla del historial utilizable y cargarla exclusivamente desde variables o un gestor de secretos.",
    risk: "ALTO", tests: "Secret scan, arranque sin secreto por defecto, prueba de configuración con variable y verificación operativa de rotación.",
    disposition: "CORREGIR_Y_ROTAR", rationale: "Eliminar el texto no invalida una credencial ya expuesta.", priority: "P0",
  },
  "secrets:S6703": {
    category: "Seguridad", solutionGroup: "Secretos fuera del repositorio", cause: "Una contraseña de base de datos fue detectada en código/configuración.",
    correction: "Rotar la contraseña y exigir SPRING_DATASOURCE_PASSWORD o gestor de secretos sin valor sensible predeterminado.",
    risk: "ALTO", tests: "Secret scan, conexión con secreto inyectado y fallo seguro cuando falta la variable.",
    disposition: "CORREGIR_Y_ROTAR", rationale: "La exposición de credenciales es un riesgo directo.", priority: "P0",
  },
  "java:S4502": {
    category: "Seguridad", solutionGroup: "Modelo CSRF para API JWT", cause: "Spring Security desactiva CSRF globalmente.",
    correction: "Documentar y probar que la API es stateless, usa Bearer JWT y no autentica con cookies; si aparece autenticación por cookies, habilitar CSRF para esas rutas.",
    risk: "ALTO", tests: "Pruebas MockMvc de sesión STATELESS, ausencia de cookies de autenticación y autorización de rutas mutables.",
    disposition: "REVISAR_RIESGO_ACEPTABLE", rationale: "Puede ser seguro en una API stateless con tokens no enviados automáticamente por el navegador.", priority: "P0",
  },
  "java:S5122": {
    category: "Seguridad", solutionGroup: "CORS centralizado", cause: "CORS se habilitó en un controlador sin que la regla pudiera demostrar orígenes restringidos.",
    correction: "Centralizar CorsConfigurationSource y limitar orígenes, métodos y cabeceras mediante configuración validada.",
    risk: "ALTO", tests: "MockMvc con Origin permitido/no permitido y preflight OPTIONS.", disposition: "CORREGIR_O_JUSTIFICAR", rationale: "La seguridad depende de la lista efectiva de orígenes.", priority: "P0",
  },
  "java:S5693": {
    category: "Seguridad", solutionGroup: "Límites multipart administrados", cause: "Los límites de 10/15 MB superan el umbral genérico de 8 MB.",
    correction: "Conservar límites si los endpoints siguen restringidos a administradores; añadir rate limiting/proxy limits si cambia la exposición.",
    risk: "MEDIO", tests: "Carga autorizada bajo el límite, rechazo sobre el límite y acceso anónimo denegado.",
    disposition: "FALSO_POSITIVO_VERIFICADO", rationale: "SonarQube registra FALSE_POSITIVE con justificación de endpoints JWT administrativos.", priority: "P0",
  },
  "java:S6809": {
    category: "Fiabilidad transaccional", solutionGroup: "Evitar self-invocation transaccional", cause: "Una llamada directa dentro del mismo bean evita el proxy de Spring y puede omitir @Transactional.",
    correction: "Extraer el método transaccional a un colaborador inyectado o mover la frontera transaccional al método público invocado externamente.",
    risk: "ALTO", tests: "Integración con base de datos que verifique commit/rollback y propagación ante excepciones.", disposition: "CORREGIR", rationale: "Puede producir escrituras parciales.", priority: "P1",
  },
  "java:S8989": {
    category: "Fiabilidad transaccional", solutionGroup: "Rollback explícito", cause: "@Transactional no declara el comportamiento ante checked exceptions.",
    correction: "Definir rollbackFor/noRollbackFor según el contrato o convertir la excepción a una excepción de dominio no comprobada.",
    risk: "ALTO", tests: "Integración que fuerce la excepción y compruebe rollback completo.", disposition: "CORREGIR", rationale: "La ambigüedad puede dejar datos parciales.", priority: "P1",
  },
  "java:S6218": {
    category: "Fiabilidad", solutionGroup: "Igualdad profunda de arrays", cause: "equals/hashCode/toString generados comparan la identidad del array y no su contenido.",
    correction: "Usar Arrays.equals/deepEquals, Arrays.hashCode/deepHashCode y representación defensiva; considerar una clase en vez de record si hay arrays mutables.",
    risk: "ALTO", tests: "Objetos con arrays distintos pero mismo contenido, hash collections, null y copia defensiva.", disposition: "CORREGIR", rationale: "La igualdad inconsistente causa errores de caché y colecciones.", priority: "P1",
  },
  "java:S5998": {
    category: "Fiabilidad", solutionGroup: "Regex sin recursión peligrosa", cause: "Una repetición anidada puede desbordar la pila con entradas grandes.",
    correction: "Simplificar la regex, usar cuantificadores posesivos/grupos atómicos cuando corresponda o validación imperativa con límite de longitud.",
    risk: "ALTO", tests: "Casos válidos/inválidos, entrada máxima y prueba de tiempo/stack con cadenas adversarias.", disposition: "CORREGIR", rationale: "Puede causar fallo o denegación de servicio.", priority: "P1",
  },
  "java:S8786": {
    category: "Rendimiento y seguridad", solutionGroup: "Regex lineal", cause: "Backtracking superlineal ante entradas adversarias.",
    correction: "Reescribir la expresión para complejidad lineal, acotar entrada y precompilar Pattern.",
    risk: "ALTO", tests: "Benchmark/timeout con entradas largas adversarias y casos funcionales existentes.", disposition: "CORREGIR", rationale: "Riesgo de ReDoS o bloqueo de scraping.", priority: "P1",
  },
  "java:S5866": {
    category: "Fiabilidad", solutionGroup: "Regex Unicode explícita", cause: "La regex usa clases Unicode sin la bandera u/UNICODE_CHARACTER_CLASS adecuada.",
    correction: "Activar la bandera Unicode compatible y validar caracteres españoles, símbolos y separadores.",
    risk: "MEDIO", tests: "Precios/textos ASCII y Unicode, tildes, ñ y espacios no separables.", disposition: "CORREGIR", rationale: "La coincidencia puede fallar con texto real no ASCII.", priority: "P1",
  },
  "java:S5850": {
    category: "Fiabilidad", solutionGroup: "Precedencia regex explícita", cause: "La alternancia y los anclajes tienen precedencia ambigua.",
    correction: "Agrupar alternativas con grupos no capturantes y anclar el conjunto completo.",
    risk: "MEDIO", tests: "Tabla parametrizada de valores aceptados/rechazados y entradas parciales.", disposition: "CORREGIR", rationale: "La regex puede aceptar valores fuera del formato.", priority: "P1",
  },
  "java:S8688": {
    category: "Fiabilidad temporal", solutionGroup: "Clock/ZoneId explícito", cause: "LocalDateTime.now() depende de la zona horaria implícita del proceso.",
    correction: "Definir una zona de negocio o inyectar Clock; usar el mismo criterio en entidades, servicios y pruebas.",
    risk: "MEDIO", tests: "Clock fijo, cambio de zona/DST, persistencia y comparación de fechas.", disposition: "CORREGIR", rationale: "Despliegues con zonas diferentes producen marcas inconsistentes.", priority: "P2",
  },
  "java:S3776": {
    category: "Mantenibilidad", solutionGroup: "Reducir complejidad cognitiva", cause: "El método combina demasiadas decisiones y niveles de anidamiento.",
    correction: "Extraer validaciones y operaciones con nombres de dominio, usar retornos tempranos y separar responsabilidades sin alterar el flujo.",
    risk: "ALTO", tests: "Caracterización antes de refactorizar, ramas felices, bordes, excepciones y verify/never.", disposition: "CORREGIR", rationale: "La refactorización es útil pero puede introducir regresiones si no se caracteriza primero.", priority: "P2",
  },
  "java:S3358": {
    category: "Mantenibilidad", solutionGroup: "Eliminar ternarios anidados", cause: "Un ternario anidado oculta ramas y precedencia.",
    correction: "Extraer la decisión a if/else o método con nombre y pruebas por rama.", risk: "MEDIO", tests: "Prueba de cada combinación que alimenta el ternario.", disposition: "CORREGIR", rationale: "Mejora legibilidad preservando semántica.", priority: "P2",
  },
  "java:S1141": {
    category: "Mantenibilidad", solutionGroup: "Extraer try anidado", cause: "Un bloque try dentro de otro mezcla recuperación y operación principal.",
    correction: "Extraer la operación interna y su política de error a un método colaborador.", risk: "MEDIO", tests: "Éxito y excepción en ambos niveles, causa preservada y efectos laterales.", disposition: "CORREGIR", rationale: "Reduce rutas de error difíciles de razonar.", priority: "P2",
  },
  "java:S107": {
    category: "Diseño", solutionGroup: "Objeto de parámetros", cause: "El método recibe demasiados parámetros relacionados.",
    correction: "Introducir request/criteria/value object validado, manteniendo compatibilidad temporal si es API pública.", risk: "ALTO", tests: "Contrato, validación, mapeo y consumidores del método.", disposition: "CORREGIR_GRADUALMENTE", rationale: "Cambiar firma afecta múltiples consumidores.", priority: "P2",
  },
  "java:S1192": {
    category: "Mantenibilidad", solutionGroup: "Constantes Java compartidas", cause: "Literales con significado de dominio se repiten.",
    correction: "Extraer constantes privadas con nombre; no compartir constantes entre conceptos coincidentes por accidente.", risk: "BAJO", tests: "Compilación y pruebas existentes del componente.", disposition: "CORREGIR", rationale: "Cambio mecánico de bajo riesgo si se respeta el significado.", priority: "P3",
  },
  "java:S5778": {
    category: "Calidad de pruebas", solutionGroup: "Lambda assertThrows de una invocación", cause: "La lambda contiene varias llamadas capaces de lanzar excepción y vuelve ambiguo el origen.",
    correction: "Preparar datos fuera de la lambda y dejar una sola invocación bajo assertThrows.", risk: "BAJO", tests: "Ejecutar la propia prueba y confirmar tipo/mensaje e interacciones.", disposition: "CORREGIR", rationale: "Aclara qué llamada se espera que falle.", priority: "P3",
  },
  "java:S8924": {
    category: "Estilo de pruebas", solutionGroup: "Static imports Mockito", cause: "Se invocan times/never mediante Mockito en vez de import estático.",
    correction: "Usar imports estáticos coherentes con el resto de tests.", risk: "BAJO", tests: "Compilación y ejecución del test afectado.", disposition: "CORREGIR", rationale: "Cambio de estilo sin semántica.", priority: "P3",
  },
  "java:S5853": {
    category: "Calidad de pruebas", solutionGroup: "Cadena AssertJ única", cause: "Varias aserciones operan sobre el mismo objeto.",
    correction: "Unificarlas en una cadena AssertJ legible sin perder mensajes.", risk: "BAJO", tests: "Ejecutar el test afectado.", disposition: "CORREGIR", rationale: "Cambio solo de estructura de aserción.", priority: "P3",
  },
  "java:S1128": {
    category: "Limpieza", solutionGroup: "Imports", cause: "Hay un import duplicado o no utilizado.", correction: "Eliminar únicamente el import señalado y ordenar imports.",
    risk: "BAJO", tests: "Compilación del módulo.", disposition: "CORREGIR", rationale: "Cambio mecánico.", priority: "P3",
  },
  "java:S1172": {
    category: "Diseño", solutionGroup: "Parámetros no usados", cause: "La firma declara parámetros que la implementación ignora.",
    correction: "Eliminar el parámetro y actualizar llamadas, o documentar/usar el parámetro si forma parte de un contrato requerido.", risk: "MEDIO", tests: "Compilación, consumidores y pruebas del método.", disposition: "REVISAR_CONTRATO", rationale: "Puede ser falso positivo si implementa una interfaz o callback estable.", priority: "P3",
  },
  "java:S1659": {
    category: "Legibilidad", solutionGroup: "Una declaración por línea", cause: "Varias variables se declaran en una sola sentencia.", correction: "Separar declaraciones sin alterar inicialización.",
    risk: "BAJO", tests: "Compilación y pruebas existentes.", disposition: "CORREGIR", rationale: "Cambio mecánico.", priority: "P3",
  },
  "java:S119": {
    category: "Convenciones", solutionGroup: "Genéricos con nombre convencional", cause: "El parámetro genérico no sigue T/E/K/V o nombre aceptado.", correction: "Renombrar el parámetro genérico de forma consistente en interfaz e implementación.",
    risk: "BAJO", tests: "Compilación completa.", disposition: "CORREGIR", rationale: "No cambia bytecode ni API binaria relevante.", priority: "P3",
  },
  "java:S9391": {
    category: "Legibilidad", solutionGroup: "Loop versus Stream", cause: "El loop implementa filter/map/collect que Sonar considera expresable con Stream.",
    correction: "Convertir solo si mejora claridad y preserva orden/efectos; de lo contrario aceptar justificadamente el loop.", risk: "MEDIO", tests: "Orden, duplicados, colección vacía y efectos laterales.", disposition: "REVISAR_RIESGO_ACEPTABLE", rationale: "Stream no siempre es más legible ni más eficiente.", priority: "P3",
  },
  "java:S5673": {
    category: "Arquitectura Spring", solutionGroup: "Estereotipo Service", cause: "Una clase de servicio usa @Component.", correction: "Cambiar a @Service si representa lógica de negocio; conservar @Component si no lo es y documentar.",
    risk: "BAJO", tests: "Carga de contexto e inyección del bean.", disposition: "CORREGIR_O_JUSTIFICAR", rationale: "Ambas anotaciones registran bean; cambia semántica documental.", priority: "P3",
  },
  "java:S112": {
    category: "Errores", solutionGroup: "Excepción específica", cause: "Se lanza/captura una excepción genérica.", correction: "Usar excepción de biblioteca o dominio con causa preservada y manejo global coherente.",
    risk: "MEDIO", tests: "Tipo, mensaje, causa y respuesta HTTP si atraviesa controlador.", disposition: "CORREGIR", rationale: "El tipo genérico dificulta recuperación.", priority: "P3",
  },
  "java:S1130": {
    category: "Limpieza", solutionGroup: "Throws innecesario", cause: "La firma declara una excepción que el cuerpo no puede lanzar.", correction: "Eliminar throws y adaptar consumidores si fuera necesario.",
    risk: "BAJO", tests: "Compilación y carga de contexto.", disposition: "CORREGIR", rationale: "Simplifica contrato.", priority: "P3",
  },
  "java:S135": {
    category: "Legibilidad", solutionGroup: "Control de loop", cause: "El loop usa múltiples break/continue.", correction: "Extraer predicados o método de procesamiento y reducir saltos sin cambiar orden.",
    risk: "MEDIO", tests: "Entradas con cada ruta de salto, límites y colección vacía.", disposition: "CORREGIR", rationale: "La refactorización puede alterar el control si no se caracteriza.", priority: "P3",
  },
  "java:S5843": {
    category: "Mantenibilidad", solutionGroup: "Regex dividida y documentada", cause: "La regex supera la complejidad permitida.", correction: "Dividir en patrones con nombre o validación por etapas y precompilar.",
    risk: "MEDIO", tests: "Suite parametrizada de coincidencias y no coincidencias reales.", disposition: "CORREGIR", rationale: "Reduce fragilidad de mantenimiento.", priority: "P2",
  },
  "java:S4144": {
    category: "Mantenibilidad", solutionGroup: "Callbacks duplicados", cause: "Dos métodos tienen implementación idéntica.", correction: "Extraer método común o delegar ambos callbacks; verificar si las reglas temporales deberían diferir.",
    risk: "MEDIO", tests: "Callbacks create/update con timestamps y estados nulos.", disposition: "CORREGIR", rationale: "Evita divergencia futura.", priority: "P3",
  },
  "java:S1135": {
    category: "Deuda explícita", solutionGroup: "Resolver TODO", cause: "Quedó un TODO accionable en una prueba.", correction: "Implementar la verificación pendiente o convertirla en tarea rastreable y retirar el comentario.",
    risk: "BAJO", tests: "Ejecutar la prueba de integración afectada.", disposition: "CORREGIR", rationale: "El TODO indica cobertura incompleta.", priority: "P3",
  },
};

const pendingStates = {
  "AaGdRnvH97lfKRteM0Zb": ["RESOLVED", "FALSE_POSITIVE", "FALSE-POSITIVE", "La API registra falso positivo: carga máxima de 10 MB en endpoints multipart protegidos por JWT de administrador; necesidad de negocio documentada."],
  "AaGdRnvH97lfKRteM0Zc": ["RESOLVED", "FALSE_POSITIVE", "FALSE-POSITIVE", "La API registra falso positivo: 15 MB limita la petición multipart total y los endpoints exigen rol administrador."],
  "AaES7WjO-6tQcR7a5P5V": ["CLOSED", "FIXED", "FIXED", "SonarQube cerró la detección del secreto por análisis. Debe verificarse externamente que la credencial fue rotada."],
  "AaES7WjO-6tQcR7a5P5W": ["CLOSED", "FIXED", "FIXED", "SonarQube cerró la contraseña expuesta por análisis. El cierre no demuestra por sí solo la rotación."],
  "AaEN8OBpTwfNugJobk14": ["CLOSED", "FIXED", "FIXED", "El análisis dejó de detectar la ausencia de política rollback en DemoProductSeeder."],
  "AaEN8OkrTwfNugJobk2t": ["CLOSED", "FIXED", "FIXED", "El análisis dejó de detectar la complejidad cognitiva en CotizacionService."],
  "AaEN8OpSTwfNugJobk21": ["CLOSED", "FIXED", "FIXED", "El análisis dejó de detectar la configuración CORS localizada en ArchivoController."],
  "AaEN8OngTwfNugJobk2y": ["CLOSED", "FIXED", "REMOVED", "La ubicación/código que originó la incidencia fue eliminada del análisis actual."],
  "AaCyaha0yz3W5ou-kdGf": ["CLOSED", "FIXED", "FIXED", "La lambda assertThrows dejó de incumplir la regla en el análisis posterior."],
  "AaCyaha0yz3W5ou-kdGg": ["CLOSED", "FIXED", "FIXED", "La lambda assertThrows dejó de incumplir la regla en el análisis posterior."],
  "AaCyaha0yz3W5ou-kdGh": ["CLOSED", "FIXED", "FIXED", "La lambda assertThrows dejó de incumplir la regla en el análisis posterior."],
  "AaCyaha0yz3W5ou-kdGi": ["CLOSED", "FIXED", "FIXED", "La lambda assertThrows dejó de incumplir la regla en el análisis posterior."],
  "AaEN5JebD6mrdRS6eNmR": ["CLOSED", "FIXED", "FIXED", "El loop señalado dejó de incumplir la regla en el análisis posterior."],
  "AaEN5JlyD6mrdRS6eNmS": ["CLOSED", "FIXED", "REMOVED", "La ubicación/código que originó la incidencia fue eliminada del análisis actual."],
  "AaCdT29Lj7PCqTSvAO5c": ["CLOSED", "FIXED", "FIXED", "SonarQube cerró la detección del secreto por análisis; se requiere evidencia operativa de rotación."],
  "AaCdT29Lj7PCqTSvAO5d": ["CLOSED", "FIXED", "FIXED", "SonarQube cerró la contraseña expuesta por análisis; se requiere evidencia operativa de rotación."],
  "AaCdT25rj7PCqTSvAO4t": ["CLOSED", "FIXED", "FIXED", "El análisis dejó de detectar LocalDateTime.now() sin zona en ImagenProyecto."],
};

function sourcePath(component) {
  const marker = component.indexOf(":");
  return marker === -1 ? component : component.slice(marker + 1);
}

function contextAtCommit(file, line) {
  try {
    const content = execFileSync("git", ["show", `${COMMIT}:${file}`], {
      encoding: "utf8",
      maxBuffer: 10 * 1024 * 1024,
      stdio: ["ignore", "pipe", "ignore"],
    });
    const lines = content.split(/\r?\n/);
    if (!line || line < 1 || line > lines.length) return { available: true, line: null, snippet: null };
    return { available: true, line, snippet: lines[line - 1].trim() };
  } catch (error) {
    return { available: false, line: line ?? null, snippet: null, reason: "El archivo no existe en el commit original o no pudo leerse." };
  }
}

function markdownCell(value) {
  return String(value ?? "—").replaceAll("|", "\\|").replaceAll("\n", " ");
}

function main() {
  const exported = JSON.parse(fs.readFileSync("sonarqube-issues.json", "utf8"));
  const index = JSON.parse(fs.readFileSync(path.join(DOCS, "indice.json"), "utf8"));
  const evidenceById = new Map(index.entries.map((entry) => [entry.issueId, entry]));
  const classifications = exported.issues.map((issue, order) => {
    const rule = { ...generic, ...(catalog[issue.rule] ?? {}) };
    const file = sourcePath(issue.component);
    const line = issue.line ?? issue.textRange?.startLine ?? null;
    const pending = pendingStates[issue.key];
    return {
      order: order + 1,
      id: issue.key,
      rule: issue.rule,
      file,
      line,
      severity: issue.severity,
      type: issue.type,
      message: issue.message,
      originalStatus: issue.issueStatus ?? issue.status,
      category: rule.category,
      solutionGroup: rule.solutionGroup,
      technicalCause: rule.cause,
      recommendedCorrection: rule.correction,
      modificationRisk: rule.risk,
      requiredTests: rule.tests,
      proposedDisposition: pending?.[1] === "FALSE_POSITIVE" ? "FALSO_POSITIVO_VERIFICADO" : rule.disposition,
      dispositionRationale: pending?.[3] ?? rule.rationale,
      priority: rule.priority,
      originalCode: contextAtCommit(file, line),
      evidence: evidenceById.get(issue.key) ?? null,
      currentApiState: pending ? { status: pending[0], issueStatus: pending[1], resolution: pending[2], justification: pending[3] } : null,
    };
  });

  const generatedAt = new Date().toISOString();
  fs.writeFileSync(path.join(DOCS, "clasificacion-incidencias.json"), `${JSON.stringify({ generatedAt, originalAnalysisCommit: COMMIT, total: classifications.length, classifications }, null, 2)}\n`, "utf8");

  const classificationLines = [
    "# Clasificación de las 409 incidencias",
    "",
    `Análisis original: \`${COMMIT}\``,
    "",
    "La causa, corrección, riesgo y pruebas se derivan del catálogo técnico por regla; `originalCode` en el JSON conserva la línea exacta disponible en el commit original.",
    "",
    "| # | ID | Regla | Archivo:línea | Severidad | Tipo | Grupo de solución | Disposición | Riesgo |",
    "|---:|---|---|---|---|---|---|---|---|",
  ];
  for (const item of classifications) {
    classificationLines.push(`| ${item.order} | \`${item.id}\` | \`${item.rule}\` | \`${markdownCell(item.file)}:${item.line ?? "—"}\` | ${item.severity} | ${item.type} | ${markdownCell(item.solutionGroup)} | ${item.proposedDisposition} | ${item.modificationRisk} |`);
  }
  fs.writeFileSync(path.join(DOCS, "clasificacion-incidencias.md"), `${classificationLines.join("\n")}\n`, "utf8");

  const pendingItems = classifications.filter((item) => item.currentApiState);
  const pendingLines = [
    "# Estado actual de las 17 incidencias sin captura",
    "",
    `Consulta de solo lectura a la API oficial de SonarQube Cloud: \`${generatedAt}\``,
    "",
    "Endpoint: `/api/issues/search?issues=<17 IDs>&ps=100&additionalFields=_all`.",
    "",
    "| ID | Regla | status | issueStatus | resolution | Justificación verificada | Evidencia histórica |",
    "|---|---|---|---|---|---|---|",
  ];
  for (const item of pendingItems) {
    const state = item.currentApiState;
    pendingLines.push(`| \`${item.id}\` | \`${item.rule}\` | ${state.status} | ${state.issueStatus} | ${state.resolution} | ${markdownCell(state.justification)} | Registro original en \`sonarqube-issues.json\` y código en \`${COMMIT}\`; sin PNG válido |`);
  }
  pendingLines.push("", "## Totales", "", "- 2 `RESOLVED / FALSE_POSITIVE / FALSE-POSITIVE`.", "- 13 `CLOSED / FIXED / FIXED`.", "- 2 `CLOSED / FIXED / REMOVED`.", "", "Los cuatro hallazgos de secretos cerrados como FIXED requieren comprobar la rotación fuera de SonarQube; la ausencia posterior del literal no demuestra que la credencial haya sido revocada.", "");
  fs.writeFileSync(path.join(DOCS, "pendientes-estado-actual.md"), pendingLines.join("\n"), "utf8");

  const byRule = new Map();
  const byFile = new Map();
  for (const item of classifications) {
    const ruleKey = item.rule;
    if (!byRule.has(ruleKey)) byRule.set(ruleKey, { count: 0, item });
    byRule.get(ruleKey).count += 1;
    const fileGroup = byFile.get(item.file) ?? { count: 0, rules: new Set(), priorities: new Set() };
    fileGroup.count += 1;
    fileGroup.rules.add(item.rule);
    fileGroup.priorities.add(item.priority);
    byFile.set(item.file, fileGroup);
  }
  const priorityOrder = { P0: 0, P1: 1, P2: 2, P3: 3, P4: 4 };
  const ruleGroups = [...byRule.entries()].sort((a, b) => priorityOrder[a[1].item.priority] - priorityOrder[b[1].item.priority] || b[1].count - a[1].count);
  const fileGroups = [...byFile.entries()].sort((a, b) => b[1].count - a[1].count);

  const plan = [
    "# Plan de corrección de SonarQube",
    "",
    `Referencia inmutable del análisis original: \`${COMMIT}\`.`,
    "",
    "Este documento no autoriza cambios de código. Cada bloque requiere aprobación y debe conservar las evidencias ANTES.",
    "",
    "## Orden propuesto",
    "",
    "1. **P0 — Seguridad:** secretos, CSRF, CORS y límites de carga. Verificar primero los 4 secretos ya cerrados y documentar rotación; no reabrir ni cambiar estados automáticamente.",
    "2. **P1 — Fiabilidad:** límites transaccionales, igualdad de arrays y expresiones regulares con riesgo de error/ReDoS.",
    "3. **P2 — Cambios estructurales:** zona horaria, complejidad cognitiva, firmas extensas y regex complejas. Crear pruebas de caracterización antes de refactorizar.",
    "4. **P3 — Mantenibilidad y tests:** literales Java, imports, lambdas assertThrows, estilo y contratos menores.",
    "5. **P4 — Perfil SQL:** no modificar migraciones aplicadas. Resolver 221 alertas de dialecto/duplicación mediante configuración, falso positivo o riesgo aceptado con revisión humana.",
    "",
    "## Dependencias y controles",
    "",
    "- Definir primero la política temporal (`Clock`/`ZoneId`) antes de resolver las 88 incidencias S8688.",
    "- Corregir fronteras `@Transactional` antes de refactorizar complejidad en los mismos servicios.",
    "- Crear pruebas de caracterización antes de tocar scraping, regex o métodos con complejidad alta.",
    "- No editar migraciones Flyway ya aplicadas: cualquier cambio funcional SQL debe ir en una migración nueva.",
    "- Ejecutar la skill `generate-unit-tests` por cada clase Java modificada y las pruebas focalizadas correspondientes.",
    "- Reanalizar la rama corregida y generar evidencias DESPUÉS enlazadas por el mismo ID; las incidencias cerradas/removidas se documentan por estado, no con capturas inventadas.",
    "",
    "## Grupos por regla y solución",
    "",
    "| Prioridad | Regla | Cantidad | Grupo | Disposición | Riesgo | Corrección recomendada | Pruebas |",
    "|---|---|---:|---|---|---|---|---|",
  ];
  for (const [ruleName, group] of ruleGroups) {
    const item = group.item;
    plan.push(`| ${item.priority} | \`${ruleName}\` | ${group.count} | ${markdownCell(item.solutionGroup)} | ${item.proposedDisposition} | ${item.modificationRisk} | ${markdownCell(item.recommendedCorrection)} | ${markdownCell(item.requiredTests)} |`);
  }
  plan.push("", "## Agrupación por archivo", "", "| Archivo | Incidencias | Reglas | Prioridades |", "|---|---:|---|---|");
  for (const [file, group] of fileGroups) {
    plan.push(`| \`${markdownCell(file)}\` | ${group.count} | ${[...group.rules].map((rule) => `\`${rule}\``).join(", ")} | ${[...group.priorities].sort().join(", ")} |`);
  }
  plan.push("", "## Criterio de finalización por lote", "", "- Tests focalizados y regresión pasan.", "- No se altera contrato sin documentarlo.", "- `git diff --check` limpio.", "- Nuevo análisis SonarQube ejecutado sobre la rama corregida.", "- Evidencia DESPUÉS y resultado de pruebas añadidos al ID correspondiente.", "", "## Artefactos de auditoría", "", "- `clasificacion-incidencias.json`: detalle completo de las 409 incidencias, incluido contexto del commit original.", "- `clasificacion-incidencias.md`: índice humano por ID.", "- `pendientes-estado-actual.md`: estado oficial de las 17 incidencias sin PNG válido.", "");
  fs.writeFileSync(path.join(DOCS, "plan-correcciones.md"), `${plan.join("\n")}\n`, "utf8");

  console.log(JSON.stringify({ total: classifications.length, ruleGroups: byRule.size, fileGroups: byFile.size, pending: pendingItems.length }, null, 2));
}

main();
