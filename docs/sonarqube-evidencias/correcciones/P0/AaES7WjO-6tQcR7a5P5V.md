# AaES7WjO-6tQcR7a5P5V — secreto versionado

- **Regla / severidad:** `java:S6437` / BLOCKER / VULNERABILITY.
- **Ubicación original:** `src/main/resources/application.properties:16`.
- **Estado original y actual:** FIXED en el reporte; `CLOSED / FIXED` en la API.
- **Evidencia ANTES:** [metadatos históricos](../../antes/AaES7WjO-6tQcR7a5P5V.json). SonarQube ya no ofrecía una vista OPEN/CONFIRMED para obtener PNG.

## Problema y riesgo

Una credencial estuvo en configuración versionada. El cierre de la detección confirma que el analizador ya no encuentra ese texto en la revisión actual, pero no demuestra que el proveedor haya invalidado la credencial. Una credencial expuesta debe considerarse comprometida.

## Código anterior y corrección

El valor histórico se omite deliberadamente:

```properties
spring.datasource.password=<VALOR_SENSIBLE_REDACTADO>
```

La configuración local sensible queda excluida mediante `.gitignore`; `application.example.properties` exige inyección externa, sin valor predeterminado:

```properties
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
```

README documenta variables o gestor de secretos y la obligación de rotar credenciales expuestas.

## Pruebas y resultado

La compilación y las 19 pruebas de seguridad/CORS terminaron correctamente. Se verificó que `application.properties` no está versionado y que la plantilla no contiene valores sensibles predeterminados.

**Estado P0:** exposición en código corregida; **rotación/revocación pendiente de intervención externa y evidencia operativa**.
