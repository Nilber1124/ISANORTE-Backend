---
name: generate-unit-tests
description: >-
  Genera y ejecuta pruebas unitarias exhaustivas cada vez que se agrega o modifica
  código en el proyecto (servicios, controladores, utilidades, validadores, mapeadores, etc.).
---

# Generador Automático de Pruebas Unitarias

Esta skill guía la creación, actualización y ejecución de pruebas unitarias robustas en el proyecto **constructora-api** (Java 21, Spring Boot, JUnit 5, Mockito).

---

## 1. Cuándo Usar Esta Skill

Debe activarse **siempre** que:
- Se cree una nueva clase de negocio (Service, Controller, Component, Utility, Policy, Validator, etc.).
- Se agregue un nuevo método o endpoint a una clase existente.
- Se modifique la lógica de negocio o el flujo de datos existente.
- Se implemente una corrección de bugs (para asegurar regresiones cubiertas).

---

## 2. Tipos de Pruebas según la Capa del Código

### A. Capa de Servicio (`service/`, `service/implementation/`)
* **Enfoque**: Pruebas unitarias puras y aisladas con **Mockito**.
* **Objetivo**: Probar la lógica de negocio sin levantar el contexto de Spring (máxima velocidad).
* **Convención**:
  - Instanciar mocks con `mock(Interface.class)` o `@ExtendWith(MockitoExtension.class)`.
  - Configurar comportamientos con `when(...).thenReturn(...)` o `when(...).thenThrow(...)`.
  - Verificar llamadas clave con `verify(...)` y `verifyNoInteractions(...)`.
* **Ejemplo de referencia en el proyecto**: [ComparacionPrecioServiceTest.java](file:///home/nilber/Documentos/Proyectos/ISANORTE/constructora-api/src/test/java/com/isanorte/constructora_api/service/implementation/ComparacionPrecioServiceTest.java)

### B. Capa de Controladores (`controller/`)
* **Enfoque**: Pruebas de endpoints REST usando `MockMvc` en modo standalone.
* **Objetivo**: Validar contratos HTTP (status code, headers, JSON body, validación de requests y captura de excepciones globales).
* **Convención**:
  - Configurar con `MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build()`.
  - Ejecutar peticiones simuladas con `MockMvcRequestBuilders` (`post(...)`, `get(...)`, `put(...)`, `delete(...)`).
  - Validar con `andExpect(status().isOk())`, `andExpect(jsonPath("$.campo").value(...))`.
* **Ejemplo de referencia en el proyecto**: [ComparacionPrecioControllerTest.java](file:///home/nilber/Documentos/Proyectos/ISANORTE/constructora-api/src/test/java/com/isanorte/constructora_api/controller/ComparacionPrecioControllerTest.java)

### C. Utilidades, Políticas, Algoritmos y Validadores (`service/competitor/`, `util/`, `policy/`)
* **Enfoque**: Pruebas unitarias puras JUnit 5 sin dependencias externas.
* **Objetivo**: Evaluar funciones deterministas, cálculos (`BigDecimal`), expresiones regulares, transformaciones de texto.
* **Convención**:
  - Usar `@ParameterizedTest` con `@CsvSource` o `@ValueSource` para probar múltiples combinaciones y casos límite.
* **Ejemplo de referencia en el proyecto**: [SimilitudProductoServiceTest.java](file:///home/nilber/Documentos/Proyectos/ISANORTE/constructora-api/src/test/java/com/isanorte/constructora_api/service/competitor/SimilitudProductoServiceTest.java)

### D. Capa de Persistencia (`repository/`)
* **Enfoque**: Slices `@DataJpaTest` con base de datos H2 en memoria (solo cuando hay queries personalizadas `@Query` o especificaciones JPA).

---

## 3. Cobertura Obligatoria por Cada Nueva Funcionalidad (Las 4 Reglas)

Para cada método o funcionalidad nueva, generar pruebas para:

1. **Camino Feliz (Happy Path)**:
   - Flujo exitoso con datos válidos esperados.
   - Comprobación de que la salida y estado retornado coinciden exactamente con la especificación.

2. **Casos Límite y Borde (Edge Cases)**:
   - Valores nulos (`null`), cadenas vacías (`""` o espacios en blanco), colecciones vacías.
   - Valores numéricos límite (0, valores negativos, desbordes, precisión de `BigDecimal`).
   - Casos especiales de formatos (URLs malformadas, correos con caracteres especiales, etc.).

3. **Manejo de Excepciones y Errores (Error Paths)**:
   - Verificación de excepciones esperadas con `assertThrows(MiExcepcion.class, () -> ...)`.
   - Comprobar que los mensajes de error o códigos de estado sean los correctos.

4. **Verificación de Efectos Secundarios (Interactions)**:
   - Usar `verify(dependency, times(1)).metodo(...)` en caminos de éxito.
   - Usar `verify(dependency, never()).metodo(...)` cuando una validación previa falle.

---

## 4. Convenciones de Nomenclatura y Ubicación

- **Ubicación**: En el directorio espejo `src/test/java/` bajo el mismo paquete que la clase testeada.
- **Nombre de clase**: `<NombreClaseOriginal>Test.java` (ej. `ProductoService` -> `ProductoServiceTest`).
- **Nombres de métodos de prueba**: Expresivos y descriptivos, indicando la condición y el resultado esperado:
  - `debeCrearProductoCuandoDatosSonValidos()`
  - `lanzaExcepcionCuandoProductoNoExiste()`
  - `retornaVacioCuandoFiltroNoTieneCoincidencias()`
  - O en estilo funcional del proyecto: `reconoceNombreCategoriaYCaracteristicasSimilares()`, `noComparaMonedasDiferentes()`.

---

## 5. Procedimiento Paso a Paso

1. **Identificar el cambio**:
   - Revisar qué clases o métodos se crearon o modificaron en `src/main/java/`.
2. **Localizar o crear el archivo de test**:
   - Si no existe `src/test/java/.../<Clase>Test.java`, crearlo en el paquete correspondiente.
3. **Redactar los casos de prueba**:
   - Incluir happy path, edge cases y error paths según las 4 reglas.
4. **Ejecutar la prueba**:
   ```bash
   ./mvnw test -Dtest=NombreDeLaClaseTest
   ```
5. **Verificar y corregir**:
   - Si la prueba falla o hay error de compilación, analizar la traza, ajustar el código o el test y volver a ejecutar hasta que pase con éxito (`BUILD SUCCESS`).
