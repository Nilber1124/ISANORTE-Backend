# AaGdRnsl97lfKRteM0Ys

- **Regla / severidad:** `java:S107` / MAJOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/config/DemoHomeIsadecorSeeder.java:71`.
- **Descripción exacta:** Method has 10 parameters, which is greater than 7 authorized.
- **Evidencia:** [captura real ANTES](../../antes/AaGdRnsl97lfKRteM0Ys.png) y [metadatos](../../antes/AaGdRnsl97lfKRteM0Ys.json).

## ANTES

```java
private DemoItem item(String key, String title, String description, String image,
```

## DESPUÉS

```java
private DemoItem item(String key, String title, String description, String image,
```

## Sustento técnico

La firma forma parte de un contrato/interfaz o de datos de inicialización. Introducir un objeto parámetro cambiaría consumidores y no corrige un fallo funcional; se propone para una refactorización de API posterior.

**Validación mínima:** compilación de 298 clases y 37 pruebas focalizadas exitosas. No se ejecutó la batería integral ni un nuevo análisis SonarQube.

**Estado final propuesto:** RECOMENDACIÓN SIN CAMBIO. Las capturas DESPUÉS quedan pendientes del nuevo análisis real.
