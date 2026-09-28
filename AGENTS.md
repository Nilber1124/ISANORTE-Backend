# Reglas del Proyecto - constructora-api

## Generación Obligatoria de Pruebas Unitarias
Cada vez que se creen o modifiquen clases, métodos, endpoints, controladores, servicios o utilidades en el proyecto:
1. **Generar pruebas unitarias**: Crear o actualizar los tests correspondientes en `src/test/java/` siguiendo la skill `generate-unit-tests`.
2. **Cobertura de escenarios**:
   - Camino feliz (happy path).
   - Casos borde/límite (nulls, vacíos, ceros, etc.).
   - Excepciones esperadas (`assertThrows`).
   - Verificaciones con Mockito (`verify`, `never`).
3. **Validación de ejecución**: Ejecutar `./mvnw test -Dtest=<TestName>` para verificar que todos los tests pasen exitosamente antes de dar por terminada la tarea.
