# Preparación del análisis final y evidencias DESPUÉS

Fecha de revisión: 2026-11-03 (America/Lima).

Esta fase es únicamente preparatoria. No se ejecutó SonarScanner, no se publicaron resultados, no se
hicieron migraciones y no se generaron capturas DESPUÉS.

## 1. Revisión Git

- Rama: `fix/sonarqube-quality`.
- HEAD revisado: `3edef0f8ee9fdbde744a479b78012887f912fa27`.
- Merge integrado: `dac299598226c80f250804332a0af8b33e737968`.
- Commit original analizado: `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`.
- `develop` local permanece en `8ade3af0b38cfb81842c84f16580125d5eaf3088`.
- Los 70 commits anteriores, las 409 documentaciones y las 129 correcciones documentadas están
  contenidos en la rama.

La edición de este informe deja un cambio documental local. Antes del análisis debe existir una revisión
Git limpia y auditable: registrar de nuevo `git rev-parse HEAD`, comprobar `git status --porcelain` vacío
y usar ese SHA como revisión esperada. No debe analizarse un árbol con cambios de aplicación sin commit.

## 2. Riesgo Flyway V8–V10

El merge incorporó tres renombrados. Git los detecta como renombres al 100 % y los blobs antes/después
son idénticos; no cambió el SQL:

| Nombre anterior | Nombre integrado | Blob Git idéntico | SHA-256 actual |
|---|---|---|---|
| `V8__create_resenas_producto.sql` | `V10__create_resenas_producto.sql` | `c5ae21b89bb740b24da9fb76b6286ca0f91dd687` | `4348e5a2deab0ae760b546e192a005c39a4f4c0a38f70ef53b39e2c1c9e55839` |
| `V9__cuentas_clientes.sql` | `V8__cuentas_clientes.sql` | `bd108727f76ddab8ed95861240fe7da2b84e44b1` | `31b3eaa95cb71b801787d3c19f8ae754d93c3fd2bc5870042deb50682e4b7cd2` |
| `V10__carrito_cliente.sql` | `V9__carrito_cliente.sql` | `29746678d19b45ca366139df2f120cc2a79e1e9f` | `b8f6af9ff37e737015bb58a6a1f2973bf02d1a99ea307dc15e6c0e49fce280f7` |

### Compatibilidad

Que el contenido sea idéntico no garantiza compatibilidad con una base ya migrada. Flyway identifica
cada migración por versión, descripción, tipo y checksum guardados en `flyway_schema_history`.

- Una base vacía es compatible con el conjunto integrado; las pruebas focalizadas validaron 23
  migraciones y alcanzaron la versión de prueba `2.7`.
- Una base que ya registró la secuencia antigua puede asociar V8 a reseñas, V9 a cuentas y V10 a carrito.
  El árbol integrado presenta V8 cuentas, V9 carrito y V10 reseñas. Flyway puede informar checksum o
  descripción distintos, migraciones faltantes o versiones ya aplicadas con otro contenido.
- No se ejecutó `migrate`, `repair`, `baseline` ni acceso a una base real. Tampoco se modificó SQL.

La compatibilidad solo puede confirmarse leyendo, con acceso autorizado y de solo lectura, como mínimo:

```sql
SELECT installed_rank, version, description, type, script, checksum, success
FROM flyway_schema_history
WHERE version IN ('8', '9', '10')
ORDER BY installed_rank;
```

Si existen registros antiguos, no debe ejecutarse `repair` automáticamente ni editar migraciones
aplicadas. La decisión exige respaldo de la base y un plan aprobado por el responsable de datos; puede
requerir restaurar los nombres históricos y crear migraciones nuevas para la evolución posterior.

## 3. Estado verificado de SonarQube Cloud

- Organización: `nilber1124`.
- Proyecto: `Nilber1124_ISANORTE-Backend`.
- La API oficial `api/project_branches/list` devuelve únicamente `develop`.
- Rama principal: `develop`.
- Último análisis visible: `2026-11-02T17:19:42+0000`.
- Revisión analizada: `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`.
- Quality Gate: `ERROR`; resumen de la rama: 13 bugs, 1 vulnerabilidad y 378 code smells.
- `fix/sonarqube-quality` todavía no existe como rama analizada.

La API pública de organización confirma la organización, pero no expone su plan. La documentación
oficial vigente establece:

- Free: solo análisis de la rama principal.
- Team y Enterprise: análisis de ramas ilimitado.
- OSS: análisis de ramas y Pull Requests ilimitado para organizaciones admitidas.

Por tanto, la capacidad de analizar `fix/sonarqube-quality` **no está confirmada**. La existencia de un
proyecto público o de una sola rama no permite inferir el plan. Un administrador debe revisar el plan en
SonarQube Cloud y confirmar que `sonar.branch.name=fix/sonarqube-quality` está permitido. No se debe
probar enviando un análisis accidental a `develop`.

Referencias oficiales:

- [Planes y disponibilidad de branch analysis](https://docs.sonarsource.com/sonarqube-cloud/administering-sonarcloud/managing-subscription/subscription-plans)
- [Parámetros de rama y proyecto](https://docs.sonarsource.com/sonarqube-cloud/analyzing-source-code/analysis-parameters/parameters-not-settable-in-ui)
- [Scanners soportados](https://docs.sonarsource.com/sonarqube-cloud/analyzing-source-code/scanners)
- [Análisis de Java y necesidad de bytecode](https://docs.sonarsource.com/sonarqube-cloud/advanced-setup/languages/java)
- [SonarScanner for Maven](https://docs.sonarsource.com/sonarqube-server/analyzing-source-code/scanners/sonarscanner-for-maven)

## 4. Requisitos del scanner

El proyecto es Maven de un solo módulo y usa Java 21:

- `pom.xml`: `<java.version>21</java.version>`.
- Wrapper: versión `3.3.4`, distribución Maven `3.9.16`, modo `only-script`.
- SonarScanner for Maven fijado en `5.5.0.6356` para evitar cambios implícitos de versión.
- Java 21 activo; la documentación oficial requiere Maven 3.2.5+ y Java 21 cuando no se usa
  autoprovisionamiento de JRE.
- Se requiere un token temporal cuyo usuario tenga `Execute Analysis`. Debe proporcionarse únicamente
  mediante `SONAR_TOKEN`, nunca como argumento, archivo o log.
- Se requiere acceso HTTPS a `https://sonarcloud.io` y al repositorio Maven para resolver el plugin si
  no está en caché.
- El árbol debe estar limpio y los binarios Java deben corresponder al mismo HEAD.

No hay `sonar-project.properties`, configuración Sonar en `pom.xml` ni workflow de GitHub Actions. El
análisis original incluyó Java y SQL/PLSQL, por lo que se propone un análisis local con Maven que incluya
`src/main` y no Automatic Analysis.

### Limitación local del wrapper

En este Windows, `mvnw.cmd` descarga/selecciona correctamente Maven 3.9.16, pero actualmente falla antes
de iniciar Maven con un error interno `icm` del script del wrapper. La compilación y las pruebas se
ejecutaron con la distribución 3.9.16 descargada por el propio wrapper. Antes del scanner debe verificarse:

```powershell
java -version
.\mvnw.cmd -version
```

Si el segundo comando continúa fallando, no debe interpretarse como un fallo de SonarQube. Puede usarse
`mvn.cmd` 3.9.16 ya instalado/descargado, tras verificar `mvn.cmd -version`, o corregirse el entorno del
wrapper en una tarea separada. No se modificó el wrapper durante esta preparación.

## 5. Comando propuesto, no ejecutado

Solo después de confirmar branch analysis, disponer del token temporal y tener un árbol limpio:

```powershell
$EXPECTED_SHA = git rev-parse HEAD
if (git status --porcelain) { throw "El árbol no está limpio" }

.\mvnw.cmd -B -DskipTests clean verify `
  org.sonarsource.scanner.maven:sonar-maven-plugin:5.5.0.6356:sonar `
  -Dsonar.host.url=https://sonarcloud.io `
  -Dsonar.organization=nilber1124 `
  -Dsonar.projectKey=Nilber1124_ISANORTE-Backend `
  -Dsonar.branch.name=fix/sonarqube-quality `
  -Dsonar.branch.target=develop `
  -Dsonar.sources=src/main `
  -Dsonar.tests=src/test `
  -Dsonar.sourceEncoding=UTF-8
```

`SONAR_TOKEN` debe existir solo en el entorno del proceso y eliminarse al terminar. `-DskipTests` evita
repetir las pruebas ya ejecutadas, mientras `clean verify` reconstruye bytecode coherente y evita recursos
obsoletos por los renombrados Flyway. Si se usa el Maven 3.9.16 descargado en lugar del wrapper, los
objetivos y parámetros deben ser exactamente los mismos.

No omitir `sonar.branch.name`: hacerlo podría enviar el análisis como rama principal. Tras el comando,
el SHA devuelto por la API debe coincidir con `$EXPECTED_SHA`; si no coincide, no deben iniciarse capturas.

## 6. Registro obligatorio del análisis

Crear `docs/sonarqube-evidencias/despues/analisis.json` sin secretos, con:

- proyecto, organización y rama;
- SHA esperado y SHA informado por SonarQube Cloud;
- fecha UTC de inicio y fin;
- Java, Maven y versión fijada del scanner;
- comando con el token omitido;
- URL/ID de la tarea de Compute Engine;
- estado final de la tarea y Quality Gate;
- cantidad de incidencias devueltas y archivos/lenguajes analizados.

Consultar y conservar las respuestas JSON de:

```text
api/project_analyses/search?project=Nilber1124_ISANORTE-Backend&branch=fix%2Fsonarqube-quality&ps=1
api/project_branches/list?project=Nilber1124_ISANORTE-Backend
api/issues/search?componentKeys=Nilber1124_ISANORTE-Backend&branch=fix%2Fsonarqube-quality&ps=500
```

Las consultas deben paginarse si el total supera el tamaño de página. No iniciar Playwright hasta que la
tarea de Compute Engine termine y revisión/rama/fecha coincidan.

## 7. Comparación de las 409 incidencias

No sobrescribir `sonarqube-issues.json`. Crear un inventario DESPUÉS independiente y comparar:

1. **Persistente por ID:** el mismo ID aparece en la rama nueva; registrar estado, regla, archivo, línea y mensaje.
2. **Persistente movida:** el ID cambió, pero coinciden regla, componente y mensaje/contexto; documentar ambos IDs sin asumir corrección.
3. **Ausente en el análisis nuevo:** no aparece por ID ni por regla/componente; verificar que el archivo y lenguaje sí fueron analizados.
4. **Histórica:** el archivo o construcción tampoco existe en la revisión integrada; conservar la justificación previa.
5. **Justificada/conservada:** falsos positivos PostgreSQL, recomendaciones o riesgos pendientes que se espera que permanezcan.
6. **Nueva:** no pertenece a las 409 originales; registrarla por separado, sin reutilizar IDs.

Una alerta ausente no demuestra por sí sola que fue corregida: debe relacionarse con el diff, el expediente,
la presencia del archivo en el alcance y el SHA exacto analizado.

## 8. Capturas DESPUÉS con Playwright

El script actual `tools/sonarqube-evidencias/capturar-incidencia.cjs` está diseñado para ANTES. Antes de
ejecutarlo se debe implementar y revisar un modo `despues` que:

- use una sola sesión manual autorizada y no almacene credenciales;
- exija `branch=fix/sonarqube-quality` en API y URL de interfaz;
- escriba exclusivamente en `docs/sonarqube-evidencias/despues/`;
- nunca sobrescriba `docs/sonarqube-evidencias/antes/`;
- valide ID, rama, regla, archivo, línea, mensaje y código renderizado;
- espere dinámicamente el visor, con dos intentos y máximo 30 segundos por intento;
- conserve una evidencia válida si un intento posterior falla;
- registre fallidas, no disponibles y pendientes sin inventar capturas;
- muestre progreso por lotes de 50 y permita reanudación.

### Alertas persistentes

Guardar `despues/ID.png` y `despues/ID.json`. La captura debe mostrar rama, incidencia, regla, archivo,
línea, mensaje y código cargado.

### Alertas ausentes

No existe una página de incidencia que capturar. Guardar una captura real de la búsqueda de incidencias
en la rama, filtrada por regla y componente, mostrando cero coincidencias; acompañarla con la respuesta
JSON, SHA, fecha y filtros. Usar un nombre disambiguado como `ID-ausencia.png`. Esta evidencia demuestra
ausencia en ese análisis, no automáticamente la causa.

### Expedientes

Actualizar cada expediente P0–P4 con enlaces relativos a la evidencia DESPUÉS, revisión analizada,
resultado de comparación y limitaciones. No cambiar retrospectivamente la captura ANTES ni afirmar
“corregida” cuando el resultado solo demuestre ausencia sin contexto.

## 9. Acciones pendientes que requieren autorización

1. Acceso administrativo de solo lectura para confirmar el plan y branch analysis.
2. Dejar la rama en un HEAD limpio y registrar el SHA definitivo tras este informe.
3. Proporcionar temporalmente un token con `Execute Analysis`, sin compartirlo ni persistirlo.
4. Ejecutar SonarScanner; esta acción publica un análisis en SonarQube Cloud.
5. Adaptar/revisar el script Playwright al modo DESPUÉS.
6. Iniciar sesión manualmente si la sesión del navegador expiró y ejecutar las capturas.
7. Acceso de solo lectura a `flyway_schema_history` para confirmar compatibilidad de una base real.

Si el plan es Free y no admite ramas, detenerse. Las alternativas serían migrar a Team/Enterprise/OSS o
realizar análisis de Pull Request contra la principal; esta última requiere push y PR y no está autorizada.
Nunca debe usarse `develop` como sustituto para evitar la limitación del plan.
