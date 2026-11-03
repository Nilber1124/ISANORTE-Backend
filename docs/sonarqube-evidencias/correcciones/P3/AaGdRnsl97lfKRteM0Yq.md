# AaGdRnsl97lfKRteM0Yq

- **Regla / severidad:** `java:S1192` / CRITICAL.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/config/DemoHomeIsadecorSeeder.java:54`.
- **Problema o recomendación:** Define a constant instead of duplicating this literal "/isadecor/catalogo" 5 times.
- **Evidencia:** [captura real ANTES](../../antes/AaGdRnsl97lfKRteM0Yq.png) y [metadatos](../../antes/AaGdRnsl97lfKRteM0Yq.json).

## ANTES

```java
item("PROMO-PRINCIPAL", "Texturas que definen el ambiente", "Composición de muestra para presentar colecciones ISADECOR.", "/images/isadecor-fondo.jpg", null, UbicacionAnuncio.PROMOCION_PRINCIPAL, TipoAccionAnuncio.RUTA_INTERNA, "/isadecor/catalogo", "Ver catálogo", 0),
```

## DESPUÉS

```java
Se reutiliza la constante CATALOG_PATH.
```

## Sustento técnico

Mejora mecánica de mantenibilidad y bajo riesgo, sin modificar el contrato funcional.

**Estado final propuesto:** CORREGIDA.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
