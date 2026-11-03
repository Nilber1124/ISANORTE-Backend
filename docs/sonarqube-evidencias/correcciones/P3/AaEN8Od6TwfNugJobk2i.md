# AaEN8Od6TwfNugJobk2i

- **Regla / severidad:** `java:S1659` / MINOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/implementation/scraping/UrlExternaPolicy.java:81`.
- **Problema o recomendación:** Declare "b" and all following declarations on a separate line.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8Od6TwfNugJobk2i.png) y [metadatos](../../antes/AaEN8Od6TwfNugJobk2i.json).

## ANTES

```java
int a = bytes[0] & 255, b = bytes[1] & 255, c = bytes[2] & 255;
```

## DESPUÉS

```java
Las variables a, b y c se declaran en líneas independientes.
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
