# Preparación del análisis final y evidencias DESPUÉS

Fecha de revisión: 2026-11-02 (America/Lima).  
Esta revisión es únicamente preparatoria: no se ejecutó SonarScanner, no se publicó código y no se modificaron Java, SQL, migraciones ni evidencias ANTES.

## 1. Estado local

- Rama activa: `fix/sonarqube-quality`.
- `HEAD`: `8ade3af0b38cfb81842c84f16580125d5eaf3088`.
- `develop` local apunta también a `8ade3af0b38cfb81842c84f16580125d5eaf3088`; no fue modificada.
- `origin/develop` apunta a `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`, revisión del análisis original.
- La rama activa no contiene seis commits presentes en `origin/develop`. Las correcciones y la documentación P0–P4 están en el árbol de trabajo, no en un commit; por ello todavía no existe una revisión Git exacta que represente el resultado.
- Estado expandido: 65 archivos versionados modificados y 1.314 archivos nuevos, 1.379 entradas en total. La mayoría de los nuevos archivos son las evidencias y expedientes (`docs`: 1.298 entradas); también existen cambios/nuevos archivos en `src`, `tools`, `.gitignore`, `README.md` y `sonarqube-issues.json`.
- Expedientes disponibles: P0 (8 + resumen), P1 (22 + resumen), P2 (104 + resumen), P3 (54 + resumen) y P4 (221 + resumen).

Esta divergencia debe resolverse antes del análisis. En el árbol actual faltan 13 archivos referenciados por la clasificación original, que concentran 49 incidencias: 12 P2, 11 P3 y 26 P4. Entre ellos están `Pedido`, `DireccionCliente`, `Catalogo`, sus servicios/pruebas y las migraciones V13/V14. Analizar ahora asociaría un árbol sucio a `8ade3af…` y produciría falsas desapariciones por código ausente, no por correcciones demostradas.

## 2. Sistema de compilación y ejecución anterior

El proyecto es Maven de un solo módulo:

- Maven Wrapper: `mvnw` y `mvnw.cmd`.
- Coordenadas: `com.isanorte:constructora-api:0.0.1-SNAPSHOT`.
- Empaquetado Maven predeterminado: JAR.
- Spring Boot `4.1.1` y Java `21`.
- Compilación: `./mvnw compile` o `.\mvnw.cmd compile`.
- Pruebas: `./mvnw test` o `.\mvnw.cmd test`.
- No hay plugin Sonar ni JaCoCo configurado en `pom.xml`.

Sobre el análisis original se comprobó lo siguiente:

- GitHub devuelve **cero** ejecuciones de Actions para el repositorio.
- No existen workflows en el árbol actual, `HEAD`, `origin/develop` ni el historial revisado.
- El commit `73615cec…` tiene un check `SonarCloud Code Analysis` de la app oficial, iniciado a las `2026-11-02T17:19:42Z`, con Quality Gate fallido y enlace a la rama `develop`.
- Ese check demuestra la integración/decoration de la app con GitHub, pero no identifica qué scanner subió el análisis.
- El análisis contiene reglas Java y PL/SQL. La documentación actual de SonarQube Cloud indica que PL/SQL no está soportado por Automatic Analysis. Sumado a la ausencia de workflows, la explicación compatible es una ejecución de SonarScanner desde una terminal local u otro CI externo al repositorio. El comando exacto y su versión no quedaron registrados y no deben inventarse.

Por ello, el nuevo análisis debe registrar explícitamente scanner, parámetros, log, SHA y fecha; no se puede afirmar que se está “repitiendo el mismo comando” anterior.

## 3. Configuración SonarQube Cloud encontrada

- Proyecto: `Nilber1124_ISANORTE-Backend`.
- Organización: `nilber1124`.
- Visibilidad informada por la API oficial: pública.
- Rama principal en SonarQube Cloud: `develop`.
- Último análisis: `2026-11-02T17:19:42+0000`, revisión `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`.
- La API `api/project_branches/list` devuelve únicamente `develop`; `fix/sonarqube-quality` todavía no tiene análisis registrado.
- No existe `sonar-project.properties`, `.sonarcloud.properties`, configuración Sonar en `pom.xml` ni directorio `.github/workflows`. Tampoco aparecen workflows Sonar en `HEAD`, `origin/develop` o el historial consultado.
- El reporte original contiene Java y PL/SQL. SonarQube Cloud indica que PL/SQL requiere análisis basado en CI/scanner; el análisis automático de GitHub no cubre PL/SQL. Por tanto, debe conservarse un análisis basado en SonarScanner.

La API pública no expone el plan de suscripción. La documentación vigente indica que el plan Free admite únicamente la rama principal, mientras Team y OSS admiten análisis de ramas. La presencia actual de una sola rama no demuestra ni descarta la licencia. Antes del escaneo, un administrador debe comprobar en SonarQube Cloud que la organización permite branch analysis para `fix/sonarqube-quality`. No debe probarse enviando un análisis a `develop`.

Referencias oficiales:

- [Planes y disponibilidad de branch analysis](https://docs.sonarsource.com/sonarqube-cloud/administering-sonarcloud/managing-subscription/subscription-plans)
- [Conceptos de análisis automático, CI y ramas](https://docs.sonarsource.com/sonarqube-cloud/analyzing-source-code/concepts)
- [Parámetros de proyecto y rama](https://docs.sonarsource.com/sonarqube-cloud/analyzing-source-code/analysis-parameters/parameters-not-settable-in-ui)
- [SonarScanner para Maven](https://docs.sonarsource.com/sonarqube-server/analyzing-source-code/scanners/sonarscanner-for-maven)

## 4. Revisión de los seis commits faltantes

Los commits, en orden, son:

1. `47bb831` — actualización de migraciones y configuración.
2. `6f28a29` — merge de `feature/migracion` en `develop`.
3. `47b68a1` — paginación del catálogo público.
4. `91859ec` — entidad y API de catálogos.
5. `9c949ab` — pedidos, direcciones, inventario y migraciones relacionadas.
6. `73615ce` — `trigger sonar`, revisión analizada.

Se agregan pedidos/direcciones, catálogos, paginación, stock y bloqueo optimista; se incorporan V13/V14; se renombran migraciones V8–V10; cambia el objetivo Flyway de pruebas de `2.5` a `2.7`; y se añaden pruebas y contratos OpenAPI. No son prescindibles para la comparación porque forman parte del código que originó las 409 incidencias.

Hay siete rutas modificadas tanto localmente como en esos commits:

- `.gitignore`: remoto ignora `.env`; local ignora `application.properties`. Deben conservarse ambas protecciones.
- `SecurityConfig.java`: remoto protege el endpoint de pedidos; local mantiene correcciones de CSRF y la constante de roles. Deben combinarse.
- `ArchivoController.java`: remoto añade carga PDF y centraliza la subida; local retira `@CrossOrigin("*")`. La versión integrada debe conservar ambos cambios.
- `PublicContentController.java`: remoto usa `UUID` para pedidos y añade paginación; local elimina un import que en la base antigua estaba sin uso. Tras integrar, `UUID` vuelve a ser necesario.
- `Producto.java`: remoto añade stock, `@Version` y su callback; local usa `BusinessTime`. Deben coexistir.
- `UnidadNegocio.java`: remoto añade la relación con `Catalogo`; local introduce `BusinessTime` y una constante de mensaje. Deben coexistir.
- `application-test.properties`: remoto eleva Flyway a `2.7`; local añade `cors.allowed-origins`. Deben conservarse ambos valores.

Además, `src/main/resources/application.properties` existe localmente como archivo ignorado y puede contener configuración sensible, mientras el commit remoto lo incorpora como archivo versionado. Esto puede bloquear la integración o reintroducir secretos. Debe respaldarse fuera del repositorio, revisarse y reconciliarse con `application.example.properties`; nunca debe añadirse al commit por accidente.

## 5. Estrategia recomendada para una revisión auditable

**Sí es necesario actualizar la rama de trabajo antes del análisis.** Técnicamente el scanner podría procesar el árbol actual, pero el resultado no sería comparable con el análisis original: omitiría 49 incidencias porque sus archivos no existen y asociaría cambios sin commit al SHA equivocado.

La estrategia más conservadora es preservar primero el estado actual y después integrar el commit exacto analizado, siempre sobre `fix/sonarqube-quality`:

1. Generar un manifiesto SHA-256 de `docs/sonarqube-evidencias/antes/` y de los expedientes P0–P4, más una copia de respaldo fuera del repositorio. No usar `git clean` ni depender solo de `stash` para 1.314 archivos nuevos.
2. Revisar archivos nuevos y modificados para excluir credenciales, `application.properties`, temporales y artefactos. Respaldar la configuración local sensible fuera del repositorio.
3. Con autorización, crear un **commit de salvaguarda** en `fix/sonarqube-quality` con las correcciones, pruebas, scripts y evidencias intencionales. Este commit hace recuperable el trabajo antes de resolver la divergencia.
4. Con autorización separada, integrar en la rama de trabajo el commit exacto `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`. Para máxima trazabilidad se recomienda un merge explícito de ese SHA en la rama de trabajo, no `git pull`: preserva el commit de salvaguarda y no reescribe su identidad. Un rebase lineal es posible, pero tiene mayor riesgo y reescribe el commit local.
5. Resolver únicamente las rutas solapadas indicadas arriba, sin modificar `develop`. Verificar que los 409 expedientes, las capturas ANTES y sus hashes siguen intactos.
6. Revisar de forma focalizada las 49 incidencias cuyos archivos hoy faltan. No hace falta repetir las 409 auditorías, pero estas 49 no pueden darse por corregidas o históricas hasta examinarlas sobre la base real. Las migraciones aplicadas se conservan sin editar.
7. Ejecutar una compilación y solo las pruebas focalizadas que correspondan a los conflictos o correcciones recuperadas. Registrar resultados reales.
8. Crear, con autorización, el commit final de resolución/correcciones focalizadas. Registrar `git rev-parse HEAD`, fecha UTC y comprobar un árbol limpio. Ese SHA será la revisión auditable DESPUÉS.

Después, el método de menor impacto es un análisis local autorizado con SonarScanner for Maven 5.5.0.6356 y `sonar.branch.name=fix/sonarqube-quality`. No requiere crear un workflow, pero sí permiso `Execute Analysis`, un token temporal y soporte de branch analysis.

Antes de ejecutar el scanner:

1. Confirmar que el SHA final contiene `73615cec…` como ancestro y que el árbol está limpio.
2. Confirmar en Administración/Suscripción de SonarQube Cloud que está habilitado el análisis de ramas y que `develop` seguirá siendo la principal.
3. Introducir `SONAR_TOKEN` mediante un prompt seguro o secreto temporal; no escribirlo en el comando, archivos, logs ni historial.

Comando propuesto, a ejecutar solo tras autorización:

```powershell
.\mvnw.cmd -B -DskipTests clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:5.5.0.6356:sonar `
  -Dsonar.host.url=https://sonarcloud.io `
  -Dsonar.organization=nilber1124 `
  -Dsonar.projectKey=Nilber1124_ISANORTE-Backend `
  -Dsonar.branch.name=fix/sonarqube-quality `
  -Dsonar.branch.target=develop `
  -Dsonar.sources=src/main `
  -Dsonar.tests=src/test
```

`SONAR_TOKEN` debe existir solo en el entorno del proceso y eliminarse al terminar. `-DskipTests` evita repetir la batería, pero `clean verify` recompila el árbol y elimina binarios obsoletos. No debe usarse el comando sin `sonar.branch.name`: podría actualizar la rama principal de SonarQube Cloud.

GitHub Actions no es el método inmediato porque no existe workflow ni configuración en el repositorio. Elegirlo requeriría añadir un workflow, guardar `SONAR_TOKEN` como secret, realizar commit y push de la rama. Si no hay branch analysis, la alternativa técnicamente soportada sería un análisis de Pull Request contra la rama principal, pero requiere push y crear PR; ambas acciones están fuera de la autorización actual.

## 6. Registro verificable del nuevo análisis

Cuando termine el escaneo:

1. Guardar el SHA limpio analizado, fecha UTC, rama, versión del scanner y resultado de la tarea de Compute Engine en `docs/sonarqube-evidencias/despues/analisis.json`.
2. Consultar y conservar las respuestas JSON de:
   - `api/project_analyses/search?project=Nilber1124_ISANORTE-Backend&branch=fix%2Fsonarqube-quality&ps=1`;
   - `api/project_branches/list?project=Nilber1124_ISANORTE-Backend`;
   - `api/issues/search?componentKeys=Nilber1124_ISANORTE-Backend&branch=fix%2Fsonarqube-quality&ps=500`, paginando si fuera necesario.
3. Verificar que revisión y fecha de la API coincidan con el SHA registrado. Si no coinciden, no iniciar capturas.
4. Verificar que Java y los SQL de `src/main/resources/db/migration` estén incluidos en el nuevo análisis. Una alerta ausente en un archivo no analizado no demuestra corrección.

## 7. Comparación de las 409 incidencias

Construir un inventario DESPUÉS sin reemplazar `sonarqube-issues.json`:

1. Relacionar primero por ID exacto.
2. Si Sonar asigna otro ID en la rama, relacionar por regla + archivo + hash de ubicación; como respaldo, usar regla + archivo + mensaje normalizado + línea próxima.
3. Clasificar cada incidencia original como:
   - `PRESENTE_MISMO_ID`;
   - `PRESENTE_ID_NUEVO_O_REUBICADA`;
   - `ESTADO_CAMBIADO`;
   - `AUSENTE_EN_ANALISIS_NUEVO`;
   - `NO_VERIFICABLE_ARCHIVO_NO_ANALIZADO`.
4. Para una incidencia ausente, revisar su expediente P0–P4 y el diff entre los dos commits. Solo proponer `CORREGIDA` cuando el cambio y el análisis lo sustenten. Los falsos positivos P4 y las recomendaciones conservadas pueden seguir presentes y deben registrarse como resultado esperado, no como fallo del proceso.
5. Conservar ID original y, si corresponde, ID nuevo. La rama puede generar claves distintas; no debe forzarse una igualdad inexistente.

## 8. Capturas reales DESPUÉS

El script actual `tools/sonarqube-evidencias/capturar-incidencia.cjs` está limitado a `antes/`, consulta solo `OPEN/CONFIRMED` y no añade el parámetro de rama. Antes de usarlo para DESPUÉS debe incorporarse un modo separado que:

- escriba exclusivamente en `docs/sonarqube-evidencias/despues/`;
- use `branch=fix/sonarqube-quality` tanto en API como en las URL de la interfaz;
- cargue el inventario comparado, no vuelva a tratar el JSON original como si todas las incidencias siguieran abiertas;
- conserve la disambiguación de nombres de Windows y el ID original en los metadatos;
- valide rama visible, revisión del análisis, regla, archivo, línea, mensaje e ID actual cuando exista;
- reutilice una sola sesión autorizada, con reanudación y progreso por lotes;
- nunca sobrescriba `antes/` ni una captura DESPUÉS ya validada con una defectuosa.

Para alertas presentes, la captura debe mostrar la rama, incidencia, regla, archivo, línea, mensaje y código cargado. Para alertas ausentes no existe una página de incidencia que capturar: debe guardarse una captura real de la búsqueda en la rama con filtros de regla y archivo mostrando cero coincidencias, acompañada por el JSON de la consulta, revisión y fecha. Esa evidencia demuestra ausencia en ese análisis, no por sí sola la causa de la ausencia.

## 9. Riesgos, límites y autorizaciones pendientes

- **Bloqueo actual:** no hay commit del código corregido, la rama parte de una revisión seis commits anterior y faltan archivos asociados a 49 incidencias originales.
- **Capacidad no verificable públicamente:** falta confirmar el plan y branch analysis en la interfaz administrativa.
- **Token:** se requiere un token con `Execute Analysis`, proporcionado temporalmente por el usuario; no debe almacenarse.
- **Acciones que requieren autorización expresa, por separado:**
  1. Crear manifiestos/copias de respaldo y mover temporalmente la configuración local sensible fuera del repositorio.
  2. Crear el commit de salvaguarda en `fix/sonarqube-quality`.
  3. Integrar `73615cec…` mediante merge explícito o, si se prefiere historia lineal, rebase; resolver los siete solapamientos.
  4. Modificar Java/pruebas solo donde sea necesario para reconciliar los conflictos y las 49 incidencias recuperadas; no editar migraciones aplicadas.
  5. Ejecutar compilación y pruebas focalizadas posteriores a la integración.
  6. Crear el commit final auditable y, si se desea trazabilidad GitHub completa, hacer push únicamente de `fix/sonarqube-quality`.
  7. Confirmar branch analysis y proporcionar temporalmente un token con `Execute Analysis`.
  8. Ejecutar el scanner, acción que publica resultados en SonarQube Cloud.
  9. Adaptar el script al modo DESPUÉS, abrir una sesión Playwright si caducó y capturar las evidencias reales.
  10. Solo si branch analysis no está disponible: crear workflow/secret y/o Pull Request como alternativa; ninguna de estas acciones está autorizada ahora.
- No se recomienda analizar el árbol sucio, enviar resultados a `develop`, crear un proyecto Sonar alternativo ni interpretar la ausencia de una alerta como corrección automática.

Hasta recibir esas autorizaciones, el proceso debe detenerse aquí.
