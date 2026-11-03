#!/usr/bin/env node

const fs = require("node:fs");
const path = require("node:path");
const crypto = require("node:crypto");
const { execFileSync } = require("node:child_process");

const PROJECT_KEY = "Nilber1124_ISANORTE-Backend";
const SONAR_BASE_URL = "https://sonarcloud.io";
const LOGIN_TIMEOUT_MS = 15 * 60 * 1000;
const CODE_TIMEOUT_MS = 30 * 1000;
const POLL_INTERVAL_MS = 250;
const READY_STABILITY_MS = 750;

function argumentValue(name, fallback) {
  const index = process.argv.indexOf(name);
  return index === -1 ? fallback : process.argv[index + 1];
}

function loadPlaywright() {
  const installation = process.env.ISANORTE_PLAYWRIGHT_HOME;
  if (!installation) throw new Error("Define ISANORTE_PLAYWRIGHT_HOME.");
  return require(path.join(installation, "node_modules", "playwright"));
}

function loadExport() {
  return JSON.parse(fs.readFileSync(path.resolve("sonarqube-issues.json"), "utf8"));
}

function componentPath(component) {
  const separator = component.indexOf(":");
  return separator === -1 ? component : component.slice(separator + 1);
}

function buildFileStems(issues) {
  const groups = new Map();
  for (const issue of issues) {
    const normalized = issue.key.toLowerCase();
    groups.set(normalized, [...(groups.get(normalized) ?? []), issue.key]);
  }
  return new Map(issues.map((issue) => {
    const collision = groups.get(issue.key.toLowerCase()).length > 1;
    const suffix = crypto.createHash("sha256").update(issue.key).digest("hex").slice(0, 8);
    return [issue.key, collision ? `${issue.key}__${suffix}` : issue.key];
  }));
}

function issueUrl(issueKey) {
  return `${SONAR_BASE_URL}/project/issues?issueStatuses=OPEN%2CCONFIRMED&id=${encodeURIComponent(PROJECT_KEY)}&open=${encodeURIComponent(issueKey)}`;
}

function relative(file) {
  return path.relative(process.cwd(), file).replaceAll("\\", "/");
}

function readJsonIfAvailable(file) {
  try {
    return JSON.parse(fs.readFileSync(file, "utf8"));
  } catch {
    return null;
  }
}

async function waitForAuthenticatedSession(page) {
  const deadline = Date.now() + LOGIN_TIMEOUT_MS;
  while (Date.now() < deadline) {
    try {
      const response = await page.request.get(`${SONAR_BASE_URL}/api/authentication/validate`);
      if (response.ok() && (await response.json()).valid === true) return;
    } catch {
      // La navegación OAuth puede cancelar solicitudes brevemente.
    }
    await page.waitForTimeout(750);
  }
  throw new Error("No se detectó una sesión autenticada de SonarQube Cloud en 15 minutos.");
}

async function inspectIssuePage(page, issue) {
  return page.evaluate(({ issueKey, rule, file, line, message }) => {
    const isVisible = (element) => {
      const rect = element.getBoundingClientRect();
      const style = getComputedStyle(element);
      return rect.width > 0 && rect.height > 0 && style.visibility !== "hidden" && style.display !== "none";
    };
    const all = [...document.querySelectorAll("body *")];
    const leaves = all.filter((element) => element.children.length === 0 && isVisible(element));
    const bodyText = document.body.innerText;
    const urlIssueId = new URL(location.href).searchParams.get("open");
    const lineLabel = `L${line}`;
    const selectedLineVisible = all
      .filter((element) => element.textContent?.trim() === "Line affected" && isVisible(element))
      .some((element) => element.parentElement?.innerText.includes(lineLabel));

    const viewportWidth = innerWidth;
    const viewportHeight = innerHeight;
    const numericLines = leaves
      .map((element) => ({ text: element.textContent.trim(), rect: element.getBoundingClientRect() }))
      .filter(({ text, rect }) => /^\d+$/.test(text)
        && rect.x >= viewportWidth * 0.33
        && rect.x <= viewportWidth * 0.52
        && rect.y >= 340
        && rect.y <= viewportHeight * 0.96);
    const targetLines = numericLines.filter(({ text }) => Number(text) === Number(line));
    const targetX = targetLines[0]?.rect.x ?? null;
    const nearbyLineNumbers = targetX === null
      ? []
      : [...new Set(numericLines
        .filter(({ rect }) => Math.abs(rect.x - targetX) <= 45)
        .map(({ text }) => Number(text)))];
    const codeTokens = leaves
      .map((element) => ({ text: element.textContent.trim(), rect: element.getBoundingClientRect() }))
      .filter(({ text, rect }) => text.length > 0
        && !/^\d+$/.test(text)
        && rect.x >= viewportWidth * 0.42
        && rect.x <= viewportWidth * 0.86
        && rect.y >= 340
        && rect.y <= viewportHeight * 0.96
        && text !== message
        && text !== rule
        && text !== file);
    const renderedCodeCharacters = codeTokens.reduce((sum, token) => sum + token.text.length, 0);
    const affectedLineVisible = selectedLineVisible || targetLines.length > 0;

    const loaderSelectors = [
      "[role='progressbar']",
      "[aria-busy='true']",
      "[class*='spinner' i]",
      "[class*='skeleton' i]",
      "[class*='loading' i]",
    ];
    const activeLoaders = [...document.querySelectorAll(loaderSelectors.join(","))]
      .filter(isVisible)
      .filter((element) => {
        const rect = element.getBoundingClientRect();
        return rect.x >= viewportWidth * 0.33 && rect.y >= 330;
      }).length;
    const loadingTextVisible = leaves.some((element) => /^loading(?:\.\.\.)?$/i.test(element.textContent.trim()));
    const unavailable = /not found|does not exist|is no longer available/i.test(bodyText);
    const filePrefix = file.slice(0, Math.min(16, file.length));
    const fileVisible = bodyText.includes(file) || bodyText.includes(filePrefix);
    const baseDetailsVisible = bodyText.includes(message)
      && (bodyText.includes(rule) || bodyText.includes(rule.split(":").at(-1)))
      && fileVisible
      && affectedLineVisible;
    const sourceRendered = targetLines.length > 0
      && nearbyLineNumbers.length >= 3
      && codeTokens.length >= 3
      && renderedCodeCharacters >= 24;
    const ready = urlIssueId === issueKey
      && baseDetailsVisible
      && sourceRendered
      && activeLoaders === 0
      && !loadingTextVisible
      && !unavailable;

    return {
      ready,
      urlIssueId,
      expectedIssueId: issueKey,
      baseDetailsVisible,
      fileVisible,
      selectedLineVisible,
      affectedLineVisible,
      sourceRendered,
      targetLineElements: targetLines.length,
      nearbyLineNumbers: nearbyLineNumbers.length,
      codeTokens: codeTokens.length,
      renderedCodeCharacters,
      activeLoaders,
      loadingTextVisible,
      unavailable,
    };
  }, {
    issueKey: issue.key,
    rule: issue.rule,
    file: path.basename(componentPath(issue.component)),
    line: issue.line,
    message: issue.message,
  });
}

async function waitForCodeEvidence(page, issue) {
  const deadline = Date.now() + CODE_TIMEOUT_MS;
  let lastState = null;
  let readySince = null;
  while (Date.now() < deadline) {
    lastState = await inspectIssuePage(page, issue);
    if (lastState.ready) {
      readySince ??= Date.now();
      if (Date.now() - readySince >= READY_STABILITY_MS) {
        await page.evaluate(() => document.fonts?.ready);
        const confirmedState = await inspectIssuePage(page, issue);
        if (confirmedState.ready) return confirmedState;
        readySince = null;
        lastState = confirmedState;
      }
    } else {
      readySince = null;
    }
    if (lastState.unavailable) break;
    await page.waitForTimeout(POLL_INTERVAL_MS);
  }
  const error = new Error("El visor de código no quedó cargado y estable dentro de 30 segundos.");
  error.diagnostics = lastState;
  throw error;
}

async function inspectScreenshot(page, screenshot) {
  const source = `data:image/png;base64,${screenshot.toString("base64")}`;
  return page.evaluate(async (src) => {
    const image = new Image();
    image.src = src;
    await image.decode();
    const sourceX = Math.floor(image.width * 0.35);
    const sourceY = Math.floor(image.height * 0.37);
    const width = Math.floor(image.width * 0.5);
    const height = Math.floor(image.height * 0.53);
    const canvas = document.createElement("canvas");
    canvas.width = width;
    canvas.height = height;
    const context = canvas.getContext("2d", { willReadFrequently: true });
    context.drawImage(image, sourceX, sourceY, width, height, 0, 0, width, height);
    const pixels = context.getImageData(0, 0, width, height).data;
    let ink = 0;
    let gutterInk = 0;
    let codeInk = 0;
    const rowsWithInk = new Set();
    for (let y = 0; y < height; y += 2) {
      for (let x = 0; x < width; x += 2) {
        const offset = (y * width + x) * 4;
        const average = (pixels[offset] + pixels[offset + 1] + pixels[offset + 2]) / 3;
        if (average < 210) {
          ink += 1;
          rowsWithInk.add(y);
          if (x >= width * 0.07 && x <= width * 0.19) gutterInk += 1;
          if (x >= width * 0.2) codeInk += 1;
        }
      }
    }
    return {
      width: image.width,
      height: image.height,
      ink,
      gutterInk,
      codeInk,
      rowsWithInk: rowsWithInk.size,
      valid: ink >= 180 && gutterInk >= 12 && codeInk >= 100 && rowsWithInk.size >= 16,
    };
  }, source);
}

async function captureWithRetry(page, issue, candidatePath) {
  const attempts = [];
  for (let attempt = 1; attempt <= 2; attempt += 1) {
    try {
      await page.goto(issueUrl(issue.key), { waitUntil: "domcontentloaded", timeout: 30_000 });
      await page.waitForLoadState("networkidle", { timeout: 5_000 }).catch(() => {});
      const beforeCapture = await waitForCodeEvidence(page, issue);
      const screenshot = await page.screenshot({ fullPage: true });
      const afterCapture = await inspectIssuePage(page, issue);
      if (!afterCapture.ready) throw new Error("El visor dejó de estar listo durante la captura.");
      const screenshotValidation = await inspectScreenshot(page, screenshot);
      if (!screenshotValidation.valid) {
        const error = new Error("El PNG candidato no contiene suficiente código fuente renderizado.");
        error.diagnostics = screenshotValidation;
        throw error;
      }
      fs.writeFileSync(candidatePath, screenshot);
      return {
        attempts: attempt,
        beforeCapture,
        afterCapture,
        screenshotValidation,
        urlIssueId: afterCapture.urlIssueId,
      };
    } catch (error) {
      attempts.push({
        attempt,
        message: error instanceof Error ? error.message : String(error),
        diagnostics: error?.diagnostics ?? null,
      });
    }
  }
  const error = new Error("Los dos intentos de carga y validación del visor fallaron.");
  error.attempts = attempts;
  throw error;
}

async function findAnalysisRevision(page, analysisUuid) {
  const response = await page.request.get(
    `${SONAR_BASE_URL}/api/project_analyses/search?project=${encodeURIComponent(PROJECT_KEY)}&ps=100`
  );
  if (!response.ok()) return { available: false, reason: `HTTP ${response.status()}` };
  const data = await response.json();
  const analysis = data.analyses?.find(({ key }) => key === analysisUuid);
  if (!analysis) return { available: false, reason: "El UUID no aparece en los análisis consultados." };
  return {
    available: Boolean(analysis.revision),
    analysisUuid,
    revision: analysis.revision ?? null,
    date: analysis.date ?? null,
    projectVersion: analysis.projectVersion ?? null,
  };
}

async function fetchRemoteIssues(page) {
  const response = await page.request.get(
    `${SONAR_BASE_URL}/api/issues/search?componentKeys=${encodeURIComponent(PROJECT_KEY)}&issueStatuses=OPEN%2CCONFIRMED&ps=500`
  );
  if (!response.ok()) throw new Error(`No se pudo consultar el inventario remoto: HTTP ${response.status()}.`);
  const data = await response.json();
  return new Map((data.issues ?? []).map((issue) => [issue.key, issue]));
}

function gitCommitIsAvailable(revision) {
  if (!revision) return false;
  try {
    execFileSync("git", ["cat-file", "-e", `${revision}^{commit}`], { stdio: "ignore" });
    return true;
  } catch {
    return false;
  }
}

function apiMatchesExport(remote, issue) {
  return {
    id: remote.key === issue.key,
    rule: remote.rule === issue.rule,
    component: componentPath(remote.component) === componentPath(issue.component),
    line: remote.line === issue.line,
    message: remote.message === issue.message,
  };
}

function evidenceBase(issue, fileStem, revision, review) {
  return {
    capturedAt: null,
    phase: "antes",
    issue: {
      key: issue.key,
      rule: issue.rule,
      component: componentPath(issue.component),
      line: issue.line ?? null,
      message: issue.message,
      originalStatus: issue.issueStatus ?? issue.status,
      analysisUuid: issue.lastChangeAnalysisUuid,
    },
    fileStem,
    sourceUrl: issueUrl(issue.key),
    screenshot: null,
    analysisRevision: revision,
    validationResult: null,
    evidenceStatus: null,
    captureOrigin: null,
    previousCaptureReview: review ?? null,
    verification: null,
    failureReason: null,
  };
}

function loadReviewMap(root) {
  const report = readJsonIfAvailable(path.join(root, "revision-capturas.json"));
  return {
    report,
    byId: new Map((report?.entries ?? []).map((entry) => [entry.issueId, entry])),
  };
}

function writeMetadata(outputDirectory, fileStem, metadata) {
  const file = path.join(outputDirectory, `${fileStem}.json`);
  metadata.metadata = relative(file);
  fs.writeFileSync(file, `${JSON.stringify(metadata, null, 2)}\n`, "utf8");
}

function summaryFrom(results, total, reviewReport) {
  return {
    totalIssues: total,
    validCaptures: results.filter((result) => result.evidenceStatus === "correcta").length,
    previousCapturesConserved: results.filter((result) => result.captureOrigin === "conservada").length,
    defectiveCapturesRegenerated: results.filter((result) => result.captureOrigin === "regenerada").length,
    newCapturesGenerated: results.filter((result) => result.captureOrigin === "nueva").length,
    failedCaptures: results.filter((result) => result.evidenceStatus === "fallida").length,
    pendingIssues: results.filter((result) => result.evidenceStatus === "pendiente").length,
    notProcessed: total - results.length,
    previousReview: reviewReport?.counts ?? null,
  };
}

function writeReports(root, exported, results, revision, reviewReport, complete) {
  const byId = new Map(results.map((result) => [result.issue.key, result]));
  const summary = summaryFrom(results, exported.issues.length, reviewReport);
  const entries = exported.issues.map((issue, index) => {
    const result = byId.get(issue.key);
    return {
      order: index + 1,
      issueId: issue.key,
      rule: issue.rule,
      component: componentPath(issue.component),
      line: issue.line ?? null,
      originalStatus: issue.issueStatus ?? issue.status,
      evidenceStatus: result?.evidenceStatus ?? "pendiente_procesamiento",
      validationResult: result?.validationResult ?? null,
      captureOrigin: result?.captureOrigin ?? null,
      screenshot: result?.screenshot ?? null,
      metadata: result?.metadata ?? null,
      failureReason: result?.failureReason ?? null,
    };
  });
  fs.writeFileSync(path.join(root, "indice.json"), `${JSON.stringify({
    generatedAt: new Date().toISOString(),
    complete,
    projectKey: PROJECT_KEY,
    analysisRevision: revision,
    summary,
    entries,
  }, null, 2)}\n`, "utf8");

  const indexLines = [
    "# Índice de evidencias ANTES — SonarQube Cloud",
    "",
    `Estado: **${complete ? "completo" : "en progreso"}**`,
    "",
    `Commit analizado: \`${revision.revision ?? "no disponible"}\` (disponible en Git: \`${revision.gitCommitAvailable}\`)`,
    "",
    "| # | ID | Regla | Archivo | Línea | Validación | Origen | PNG | JSON |",
    "|---:|---|---|---|---:|---|---|---|---|",
  ];
  for (const entry of entries) {
    const png = entry.screenshot ? `[PNG](${entry.screenshot.replace("docs/sonarqube-evidencias/", "")})` : "—";
    const json = entry.metadata ? `[JSON](${entry.metadata.replace("docs/sonarqube-evidencias/", "")})` : "—";
    indexLines.push(`| ${entry.order} | \`${entry.issueId}\` | \`${entry.rule}\` | \`${entry.component.replaceAll("|", "\\|")}\` | ${entry.line ?? "—"} | ${entry.validationResult ?? entry.evidenceStatus} | ${entry.captureOrigin ?? "—"} | ${png} | ${json} |`);
  }
  fs.writeFileSync(path.join(root, "indice.md"), `${indexLines.join("\n")}\n`, "utf8");

  const failures = results.filter((result) => result.evidenceStatus !== "correcta");
  const reasons = new Map();
  for (const result of failures) {
    const reason = result.failureReason ?? "Sin motivo registrado.";
    reasons.set(reason, (reasons.get(reason) ?? 0) + 1);
  }
  const summaryLines = [
    "# Resumen de capturas ANTES — SonarQube Cloud",
    "",
    `Generado: \`${new Date().toISOString()}\``,
    "",
    `Estado: **${complete ? "completo" : "en progreso"}**`,
    "",
    `- Total de incidencias del JSON: ${summary.totalIssues}`,
    `- Capturas válidas: ${summary.validCaptures}`,
    `- Capturas anteriores conservadas: ${summary.previousCapturesConserved}`,
    `- Capturas defectuosas regeneradas: ${summary.defectiveCapturesRegenerated}`,
    `- Nuevas capturas generadas: ${summary.newCapturesGenerated}`,
    `- Capturas fallidas: ${summary.failedCaptures}`,
    `- Incidencias pendientes: ${summary.pendingIssues}`,
    `- Incidencias aún no procesadas: ${summary.notProcessed}`,
    "",
    "## Motivos de fallos o pendientes",
    "",
  ];
  if (reasons.size === 0) summaryLines.push("No hay fallos ni pendientes registrados.");
  for (const [reason, count] of reasons) summaryLines.push(`- ${count} × ${reason}`);
  summaryLines.push("", "## Ubicación", "", "Las evidencias se encuentran en `docs/sonarqube-evidencias/antes/` y se relacionan por ID en `docs/sonarqube-evidencias/indice.md`.", "");
  fs.writeFileSync(path.join(root, "resumen-capturas.md"), summaryLines.join("\n"), "utf8");
  return summary;
}

async function main() {
  const exported = loadExport();
  const limit = Number.parseInt(argumentValue("--limit", String(exported.issues.length)), 10);
  const issues = exported.issues.slice(0, limit);
  const fileStems = buildFileStems(exported.issues);
  const root = path.resolve("docs", "sonarqube-evidencias");
  const outputDirectory = path.join(root, "antes");
  const candidateDirectory = path.join(outputDirectory, ".candidatos");
  fs.mkdirSync(candidateDirectory, { recursive: true });
  const review = loadReviewMap(root);

  const { chromium } = loadPlaywright();
  const browser = await chromium.launch({ headless: false });
  const context = await browser.newContext({ viewport: { width: 1600, height: 1000 } });
  const page = await context.newPage();
  console.log(`Ventana abierta. Inicia sesión una sola vez para procesar ${issues.length} incidencias.`);
  await page.goto(issueUrl(issues[0].key), { waitUntil: "domcontentloaded", timeout: 60_000 });
  await waitForAuthenticatedSession(page);
  console.log("Sesión autenticada validada por la API de SonarQube Cloud.");

  const revision = await findAnalysisRevision(page, issues[0].lastChangeAnalysisUuid);
  revision.gitCommitAvailable = gitCommitIsAvailable(revision.revision);
  const remoteIssues = await fetchRemoteIssues(page);
  const results = [];

  for (const [position, issue] of issues.entries()) {
    const fileStem = fileStems.get(issue.key);
    const reviewEntry = review.byId.get(issue.key) ?? null;
    const finalPng = path.join(outputDirectory, `${fileStem}.png`);
    const finalJson = path.join(outputDirectory, `${fileStem}.json`);
    const existingMetadata = readJsonIfAvailable(finalJson);
    const correctedCaptureAlreadyValid = fs.existsSync(finalPng)
      && existingMetadata?.evidenceStatus === "correcta"
      && existingMetadata?.verification?.codeViewer?.afterCapture?.ready === true
      && existingMetadata?.verification?.codeViewer?.urlIssueId === issue.key;
    let result;

    if (correctedCaptureAlreadyValid || (reviewEntry?.classification === "CORRECTA" && fs.existsSync(finalPng))) {
      result = existingMetadata ?? evidenceBase(issue, fileStem, revision, reviewEntry);
      result.issue = evidenceBase(issue, fileStem, revision, reviewEntry).issue;
      result.fileStem = fileStem;
      result.screenshot = relative(finalPng);
      result.analysisRevision = revision;
      result.validationResult = correctedCaptureAlreadyValid ? "CORRECTA_REANUDADA" : "CORRECTA_CONSERVADA";
      result.evidenceStatus = "correcta";
      result.captureOrigin = correctedCaptureAlreadyValid
        ? (existingMetadata.captureOrigin ?? "reanudada")
        : "conservada";
      result.previousCaptureReview = reviewEntry;
      result.failureReason = null;
    } else {
      result = evidenceBase(issue, fileStem, revision, reviewEntry);
      const remote = remoteIssues.get(issue.key);
      if (!remote) {
        result.validationResult = "PENDIENTE_NO_DISPONIBLE";
        result.evidenceStatus = "pendiente";
        result.failureReason = "La incidencia ya no figura como OPEN o CONFIRMED en SonarQube Cloud.";
      } else if (issue.line == null) {
        result.validationResult = "PENDIENTE_SIN_VISOR";
        result.evidenceStatus = "pendiente";
        result.failureReason = `SonarQube no proporciona una línea/visor de código; archivo original: ${componentPath(issue.component)}.`;
      } else {
        const apiMatches = apiMatchesExport(remote, issue);
        if (!Object.values(apiMatches).every(Boolean)) {
          result.validationResult = "PENDIENTE_CAMBIO_REMOTO";
          result.evidenceStatus = "pendiente";
          result.failureReason = "La incidencia remota cambió respecto de sonarqube-issues.json.";
          result.verification = { apiMatches };
        } else {
          const candidate = path.join(candidateDirectory, `${fileStem}.png`);
          try {
            const visible = await captureWithRetry(page, issue, candidate);
            fs.copyFileSync(candidate, finalPng);
            fs.unlinkSync(candidate);
            result.capturedAt = new Date().toISOString();
            result.sourceUrl = page.url();
            result.screenshot = relative(finalPng);
            result.validationResult = "CORRECTA";
            result.evidenceStatus = "correcta";
            result.captureOrigin = fs.existsSync(finalJson) || reviewEntry?.pngExists ? "regenerada" : "nueva";
            result.verification = { apiMatches, codeViewer: visible };
          } catch (error) {
            result.validationResult = "FALLIDA_VISOR";
            result.evidenceStatus = "fallida";
            result.failureReason = error instanceof Error ? error.message : String(error);
            result.verification = { apiMatches, attempts: error?.attempts ?? null };
          }
        }
      }
    }

    writeMetadata(outputDirectory, fileStem, result);
    results.push(result);
    if (result.evidenceStatus !== "correcta") {
      console.log(`${issue.key}: ${result.validationResult} — ${result.failureReason}`);
    }
    if ((position + 1) % 50 === 0 || position + 1 === issues.length) {
      const partial = writeReports(root, exported, results, revision, review.report, false);
      console.log(`Progreso ${position + 1}/${issues.length}: ${partial.validCaptures} válidas, ${partial.failedCaptures} fallidas, ${partial.pendingIssues} pendientes.`);
    }
  }

  const summary = writeReports(root, exported, results, revision, review.report, issues.length === exported.issues.length);
  console.log(JSON.stringify({ analysisRevision: revision, summary }, null, 2));
  await browser.close();
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
