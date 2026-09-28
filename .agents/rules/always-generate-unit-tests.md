# Regla: Generación Obligatoria de Pruebas Unitarias

## Mandato
**Siempre que se agregue o modifique código productivo** (nuevas clases, métodos, endpoints, lógica de negocio, servicios, componentes, utilidades o validadores) en este repositorio:

1. **Obligatoriedad de Pruebas**: Se DEBE generar o actualizar la suite de pruebas unitarias correspondiente en `src/test/java/`. No se considera completada ninguna tarea que agregue código nuevo sin sus pruebas unitarias.
2. **Aplicación de la Skill**: Seguir estrictamente las pautas, estructura y convenciones definidas en la skill `generate-unit-tests` (`.agents/skills/generate-unit-tests/SKILL.md`).
3. **Casos Mínimos Exigidos**:
   - Caso exitoso (Happy path).
   - Casos límite (Edge cases: `null`, vacíos, ceros, límites de `BigDecimal`).
   - Casos de error/excepción (`assertThrows` con excepciones de negocio).
   - Verificación de llamadas y dependencias (`verify`, `verifyNoInteractions`).
4. **Verificación de Ejecución**: Siempre compilar y ejecutar los tests correspondientes usando `./mvnw test -Dtest=<NombreTest>` para asegurar que el build esté en verde (`BUILD SUCCESS`) antes de reportar la tarea al usuario.
