# Reglas del Proyecto - constructora-api

## Generación Obligatoria de Pruebas Unitarias
Cada vez que se creen o modifiquen clases, métodos, endpoints, controladores, servicios o utilidades en el proyecto:
1. **Generar pruebas unitarias**: Para cada funcionalidad o comportamiento nuevo, seguir la skill `feature-unit-tests`. Para cambios de comportamiento existente y correcciones de bugs, seguir `generate-unit-tests`. Crear o actualizar los tests correspondientes en `src/test/java/`.
2. **Cobertura de escenarios**:
   - Camino feliz (happy path).
   - Casos borde/límite (nulls, vacíos, ceros, etc.).
   - Excepciones esperadas (`assertThrows`).
   - Verificaciones con Mockito (`verify`, `never`).
3. **Validación de ejecución**: Ejecutar `./mvnw test -Dtest=<TestName>` para verificar que todos los tests pasen exitosamente antes de dar por terminada la tarea.

## Mantenimiento Obligatorio del README.md
Cada vez que se agreguen, modifiquen o eliminen variables de entorno, propiedades de configuración (`application.properties`), dependencias clave en `pom.xml`, nuevos módulos estructurales o endpoints principales:
1. **Actualizar el README.md**: Sincronizar inmediatamente la documentación siguiendo las instrucciones y pautas de la skill `update-readme`.
2. **Verificar requisitos de arranque**: Asegurar que cualquier desarrollador nuevo cuente con las instrucciones completas (variables de entorno, claves, restricciones mínimas y comandos de ejecución) para levantar la aplicación sin errores.
