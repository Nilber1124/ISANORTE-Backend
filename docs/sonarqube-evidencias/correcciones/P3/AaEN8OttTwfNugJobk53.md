# AaEN8OttTwfNugJobk53

- **Regla / severidad:** `java:S5853` / MINOR.
- **Archivo y línea original:** `src/test/java/com/isanorte/constructora_api/config/DemoProductSeederTest.java:63`.
- **Problema o recomendación:** Join these multiple assertions subject to one assertion chain.
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OttTwfNugJobk53.png) y [metadatos](../../antes/AaEN8OttTwfNugJobk53.json).

## ANTES

```java
assertThat(products).allMatch(product -> product.getEstado() == EstadoPublicacion.PUBLICADO);
```

## DESPUÉS

```java
assertThat(products).allMatch(product -> product.getEstado() == EstadoPublicacion.PUBLICADO);
```

## Sustento técnico

Las aserciones separadas identifican con precisión qué invariante del catálogo falló. Unificarlas es estilístico y no mejora el comportamiento.

**Estado final propuesto:** RECOMENDACIÓN SIN CAMBIO.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
