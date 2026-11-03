# AaGdRnsl97lfKRteM0Yp

- **Regla / severidad:** `java:S1192` / CRITICAL.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/config/DemoHomeIsadecorSeeder.java:52`.
- **Problema o recomendación:** Define a constant instead of duplicating this literal "Explorar" 3 times.
- **Evidencia:** [captura real ANTES](../../antes/AaGdRnsl97lfKRteM0Yp.png) y [metadatos](../../antes/AaGdRnsl97lfKRteM0Yp.json).

## ANTES

```java
item("CINTA-WPC", "Paneles WPC", "Acabados decorativos para muros.", "/images/isadecor-left-1.jpg", null, UbicacionAnuncio.CINTA_HOME, TipoAccionAnuncio.CATEGORIA, "wall-panels", "Explorar", 0),
```

## DESPUÉS

```java
Se reutiliza la constante EXPLORE.
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
