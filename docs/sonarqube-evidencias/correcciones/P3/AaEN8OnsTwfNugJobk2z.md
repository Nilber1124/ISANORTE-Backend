# AaEN8OnsTwfNugJobk2z

- **Regla / severidad:** `java:S1172` / MAJOR.
- **Archivo y línea original:** `src/main/java/com/isanorte/constructora_api/service/competitor/AbstractHtmlCompetidorProvider.java:31`.
- **Problema o recomendación:** Remove this unused method parameter "referencia".
- **Evidencia:** [captura real ANTES](../../antes/AaEN8OnsTwfNugJobk2z.png) y [metadatos](../../antes/AaEN8OnsTwfNugJobk2z.json).

## ANTES

```java
protected List<String> urlsDirectas(ProductoReferencia referencia) { return List.of(); }
```

## DESPUÉS

```java
protected List<String> urlsDirectas(ProductoReferencia referencia) { return List.of(); }
```

## Sustento técnico

El parámetro forma parte de un hook extensible o helper de prueba y mantiene una firma uniforme con consumidores; retirarlo obliga a cambiar el contrato sin beneficio funcional inmediato.

**Estado final propuesto:** RECOMENDACIÓN SIN CAMBIO.

**Limitación:** compilación y 27 pruebas focalizadas exitosas; sin integraciones completas ni nuevo análisis SonarQube. La captura DESPUÉS queda pendiente.
