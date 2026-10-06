---
name: feature-unit-tests
description: >-
  Diseña, implementa y ejecuta pruebas unitarias para cada funcionalidad o comportamiento nuevo
  del proyecto constructora-api. Usar siempre que se agregue una capacidad, endpoint o regla de negocio.
---

# Pruebas para Funcionalidades Nuevas

Esta skill establece las pruebas como parte de la implementación de cualquier funcionalidad nueva en **constructora-api** (Java 21, Spring Boot, JUnit 5, Mockito).

## Activación Obligatoria

Usar esta skill cada vez que se agregue una funcionalidad o un comportamiento nuevo, incluyendo endpoints, reglas de negocio, validaciones, transformaciones, consultas o cambios de persistencia que habiliten una capacidad nueva. No considerar terminada la funcionalidad hasta que sus pruebas enfocadas se hayan ejecutado.

Para modificaciones de comportamiento existente y correcciones de bugs, complementar con la skill `generate-unit-tests` para cubrir regresiones.

## Flujo de Trabajo

1. **Aterrizar el comportamiento**: identificar los criterios de aceptación, las entradas y salidas observables, las validaciones y los efectos secundarios de la funcionalidad. Revisar las pruebas vecinas para respetar las convenciones del proyecto.
2. **Localizar la capa responsable**: mapear cada clase de producción modificada a su paquete espejo en `src/test/java/`. Reutilizar el test existente si ya cubre esa clase; de lo contrario, crear `<NombreClase>Test.java`.
3. **Diseñar pruebas por comportamiento**: cubrir los escenarios aplicables indicados abajo. Probar resultados y contratos observables, no detalles internos de implementación.
4. **Implementar los tests junto con la funcionalidad**: mantener las pruebas enfocadas en la lógica añadida, aislando colaboradores externos cuando corresponda.
5. **Ejecutar la prueba enfocada**: correr el test de la funcionalidad y corregir los fallos antes de darla por terminada. Si el cambio cruza capas o afecta contratos compartidos, ejecutar además la suite completa.
6. **Informar el resultado**: indicar qué pruebas se ejecutaron y si pasaron. Si una limitación del entorno impide ejecutarlas, señalarlo claramente y no afirmar que la validación pasó.

## Escenarios a Cubrir

Incluir, según aplique al comportamiento:

- **Camino exitoso**: entradas válidas y resultado, estado HTTP o persistencia esperados.
- **Límites y validaciones**: nulos, vacíos, valores fuera de rango, duplicados u otros límites definidos por el contrato. No inventar reglas que el producto no exige.
- **Errores esperados**: excepciones, respuestas de error o rechazos definidos para entradas inválidas y dependencias fallidas.
- **Efectos secundarios**: verificar interacciones relevantes con Mockito; comprobar que no ocurran cuando la operación se rechaza o falla antes de ejecutarlos.
- **Regresión del contrato**: cuando la funcionalidad modifica una ruta o contrato existente, verificar que su comportamiento previo relevante se conserve.

No es obligatorio forzar todos los tipos de escenario si no son significativos para la funcionalidad. Cada caso debe proteger un requisito o una decisión observable.

## Estrategia según la Capa

- **Servicios**: pruebas unitarias sin contexto Spring, con Mockito para repositorios y colaboradores. Verificar el resultado y las interacciones significativas.
- **Controladores REST**: usar `MockMvc` standalone para validar método/ruta, status, JSON, validación y manejo de errores; simular el servicio.
- **Utilidades y lógica determinista**: JUnit 5 puro; usar pruebas parametrizadas cuando haya combinaciones de entradas equivalentes.
- **Repositorios**: reservar `@DataJpaTest` para consultas personalizadas o comportamiento de persistencia que no se pueda verificar de forma unitaria.
- **DTOs, mappers y validadores**: probar conversiones y restricciones públicas con entradas representativas y límites relevantes.
- **Migraciones o cambios que requieran integración entre capas**: añadir o ejecutar la prueba de integración apropiada; no presentar una prueba unitaria aislada como validación suficiente del flujo completo.

## Ejecución

Ejecutar solo el test de la clase tocada durante el ciclo de desarrollo:

```powershell
.\mvnw.cmd test "-Dtest=NombreDeLaClaseTest"
```

En sistemas Unix:

```bash
./mvnw test -Dtest=NombreDeLaClaseTest
```

Si la funcionalidad añade o modifica varias clases de prueba, pasar sus nombres a Surefire separados por comas, o ejecutar `test` para la suite completa cuando sea más claro. Confirmar `BUILD SUCCESS`; ante un fallo, distinguir errores del cambio de fallos preexistentes y comunicar cualquier bloqueo sin ocultarlo.