# AaGdRnsl97lfKRteM0Yr

- **Regla / severidad:** `java:S1192` / CRITICAL.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/config/DemoHomeIsadecorSeeder.java:53`.
- **Problema o recomendación:** Define a constant instead of duplicating this literal "/images/isadecor-left-2.jpg" 3 times.
- **Evidencia:** [captura real ANTES](../../antes/AaGdRnsl97lfKRteM0Yr.png) y [metadatos](../../antes/AaGdRnsl97lfKRteM0Yr.json).

## ANTES

```java
item("CINTA-SPC", "Mármol SPC", "Superficies contemporáneas de demostración.", "/images/isadecor-left-2.jpg", null, UbicacionAnuncio.CINTA_HOME, TipoAccionAnuncio.CATEGORIA, "revestimientos", "Explorar", 1),
```

## DESPUÉS

```java
Se reutiliza la constante LEFT_IMAGE.
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
