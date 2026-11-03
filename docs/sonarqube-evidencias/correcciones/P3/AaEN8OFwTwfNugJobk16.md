# AaEN8OFwTwfNugJobk16

- **Regla / severidad:** `java:S112` / MAJOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/config/SecurityConfig.java:57`.
- **Problema o recomendación:** Replace generic exceptions with specific library exceptions or a custom exception.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OFwTwfNugJobk16.png) y [metadatos](../../antes/AaEN8OFwTwfNugJobk16.json).

## ANTES

```java
org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource) throws Exception {
```

## DESPUÉS

```java
org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource) throws Exception {
```

## Sustento técnico

SecurityFilterChain termina en HttpSecurity.build(), cuya API de Spring declara una excepción checked genérica. Conservar throws Exception propaga exactamente el contrato de la biblioteca; capturarla para renombrarla no aporta contexto ni seguridad.

**Estado final propuesto:** FALSO POSITIVO JUSTIFICADO.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
