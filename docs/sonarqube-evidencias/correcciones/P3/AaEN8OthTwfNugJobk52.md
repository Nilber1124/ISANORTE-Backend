# AaEN8OthTwfNugJobk52

- **Regla / severidad:** `java:S1172` / MAJOR.
- **Archivo y línea original:** `src/test/java/com/isanorte/constructora_api/DynamicContentApiIntegrationTest.java:766`.
- **Problema o recomendación:** Remove these unused method parameters "principal", "order".
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OthTwfNugJobk52.png) y [metadatos](../../antes/AaEN8OthTwfNugJobk52.json).

## ANTES

```java
private void projectImage(Proyecto project, String url, String alt, boolean principal, int order) {
```

## DESPUÉS

```java
private void projectImage(Proyecto project, String url, String alt, boolean principal, int order) {
```

## Sustento técnico

El parámetro forma parte de un hook extensible o helper de prueba y mantiene una firma uniforme con consumidores; retirarlo obliga a cambiar el contrato sin beneficio funcional inmediato.

**Estado final propuesto:** RECOMENDACIÓN SIN CAMBIO.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
