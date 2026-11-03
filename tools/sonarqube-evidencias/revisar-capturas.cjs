#!/usr/bin/env node

const fs = require("node:fs");
const path = require("node:path");
const crypto = require("node:crypto");

function loadPlaywright() {
  const installation = process.env.ISANORTE_PLAYWRIGHT_HOME;
  if (!installation) throw new Error("Define ISANORTE_PLAYWRIGHT_HOME.");
  return require(path.join(installation, "node_modules", "playwright"));
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

function readMetadata(file) {
  try {
    return { value: JSON.parse(fs.readFileSync(file, "utf8")), error: null };
  } catch (error) {
    return { value: null, error: error instanceof Error ? error.message : String(error) };
  }
}

function metadataMatchesIssue(metadata, issue) {
  if (!metadata || metadata.issue?.key !== issue.key) return false;
  try {
    return new URL(metadata.sourceUrl).searchParams.get("open") === issue.key;
  } catch {
    return false;
  }
}

async function inspectImage(page, imagePath) {
  const source = `data:image/png;base64,${fs.readFileSync(imagePath).toString("base64")}`;
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
    let colored = 0;
    const rowsWithInk = new Set();
    for (let y = 0; y < height; y += 2) {
      for (let x = 0; x < width; x += 2) {
        const offset = (y * width + x) * 4;
        const red = pixels[offset];
        const green = pixels[offset + 1];
        const blue = pixels[offset + 2];
        const average = (red + green + blue) / 3;
        const isInk = average < 210;
        if (isInk) {
          ink += 1;
          rowsWithInk.add(y);
          if (x >= width * 0.07 && x <= width * 0.19) gutterInk += 1;
          if (x >= width * 0.2) codeInk += 1;
        }
        if (Math.max(red, green, blue) - Math.min(red, green, blue) > 35 && Math.min(red, green, blue) < 220) {
          colored += 1;
        }
      }
    }
    return {
      width: image.width,
      height: image.height,
      ink,
      gutterInk,
      codeInk,
      colored,
      rowsWithInk: rowsWithInk.size,
    };
  }, source);
}

function hasRenderedCode(metrics) {
  return metrics.ink >= 180 && metrics.gutterInk >= 12 && metrics.codeInk >= 100 && metrics.rowsWithInk >= 16;
}

async function main() {
  const root = path.resolve("docs", "sonarqube-evidencias");
  const before = path.join(root, "antes");
  const issues = JSON.parse(fs.readFileSync("sonarqube-issues.json", "utf8")).issues;
  const fileStems = buildFileStems(issues);
  const { chromium } = loadPlaywright();
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const entries = [];

  for (const [index, issue] of issues.entries()) {
    const fileStem = fileStems.get(issue.key);
    const pngPath = path.join(before, `${fileStem}.png`);
    const jsonPath = path.join(before, `${fileStem}.json`);
    const pngExists = fs.existsSync(pngPath);
    const metadataRead = fs.existsSync(jsonPath) ? readMetadata(jsonPath) : { value: null, error: "Metadatos ausentes." };
    let classification = "PENDIENTE";
    let reason = "No existe una captura previa.";
    let visualMetrics = null;

    if (pngExists && !metadataRead.value) {
      classification = "NO_VERIFICABLE";
      reason = metadataRead.error ?? "No fue posible leer los metadatos.";
    } else if (pngExists && !metadataMatchesIssue(metadataRead.value, issue)) {
      classification = "INCORRECTA";
      reason = "El ID de los metadatos o de la URL no coincide con el nombre de la evidencia.";
    } else if (pngExists) {
      try {
        visualMetrics = await inspectImage(page, pngPath);
        if (hasRenderedCode(visualMetrics)) {
          classification = "CORRECTA";
          reason = "El visor contiene líneas y código renderizado.";
        } else {
          classification = "INCOMPLETA";
          reason = "El visor está vacío, cargando o no contiene suficiente código renderizado.";
        }
      } catch (error) {
        classification = "NO_VERIFICABLE";
        reason = error instanceof Error ? error.message : String(error);
      }
    }

    const entry = {
      order: index + 1,
      issueId: issue.key,
      fileStem,
      rule: issue.rule,
      component: componentPath(issue.component),
      line: issue.line ?? null,
      classification,
      reason,
      pngExists,
      metadataExists: fs.existsSync(jsonPath),
      visualMetrics,
    };
    entries.push(entry);

    if (metadataRead.value) {
      metadataRead.value.existingCaptureReview = {
        reviewedAt: new Date().toISOString(),
        classification,
        reason,
        visualMetrics,
      };
      fs.writeFileSync(jsonPath, `${JSON.stringify(metadataRead.value, null, 2)}\n`, "utf8");
    }
  }

  await browser.close();
  const counts = Object.fromEntries(
    ["CORRECTA", "INCOMPLETA", "INCORRECTA", "NO_VERIFICABLE", "PENDIENTE"].map((classification) => [
      classification,
      entries.filter((entry) => entry.classification === classification).length,
    ])
  );
  const report = { generatedAt: new Date().toISOString(), total: entries.length, counts, entries };
  fs.writeFileSync(path.join(root, "revision-capturas.json"), `${JSON.stringify(report, null, 2)}\n`, "utf8");
  console.log(JSON.stringify({ total: report.total, counts }, null, 2));
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
