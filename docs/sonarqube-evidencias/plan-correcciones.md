# Plan de corrección de SonarQube

Referencia inmutable del análisis original: `73615cec38bbd6051dd9aad7a2ef9de06a5e3f93`.

Este documento no autoriza cambios de código. Cada bloque requiere aprobación y debe conservar las evidencias ANTES.

## Orden propuesto

1. **P0 — Seguridad:** secretos, CSRF, CORS y límites de carga. Verificar primero los 4 secretos ya cerrados y documentar rotación; no reabrir ni cambiar estados automáticamente.
2. **P1 — Fiabilidad:** límites transaccionales, igualdad de arrays y expresiones regulares con riesgo de error/ReDoS.
3. **P2 — Cambios estructurales:** zona horaria, complejidad cognitiva, firmas extensas y regex complejas. Crear pruebas de caracterización antes de refactorizar.
4. **P3 — Mantenibilidad y tests:** literales Java, imports, lambdas assertThrows, estilo y contratos menores.
5. **P4 — Perfil SQL:** no modificar migraciones aplicadas. Resolver 221 alertas de dialecto/duplicación mediante configuración, falso positivo o riesgo aceptado con revisión humana.

## Dependencias y controles

- Definir primero la política temporal (`Clock`/`ZoneId`) antes de resolver las 88 incidencias S8688.
- Corregir fronteras `@Transactional` antes de refactorizar complejidad en los mismos servicios.
- Crear pruebas de caracterización antes de tocar scraping, regex o métodos con complejidad alta.
- No editar migraciones Flyway ya aplicadas: cualquier cambio funcional SQL debe ir en una migración nueva.
- Ejecutar la skill `generate-unit-tests` por cada clase Java modificada y las pruebas focalizadas correspondientes.
- Reanalizar la rama corregida y generar evidencias DESPUÉS enlazadas por el mismo ID; las incidencias cerradas/removidas se documentan por estado, no con capturas inventadas.

## Grupos por regla y solución

| Prioridad | Regla | Cantidad | Grupo | Disposición | Riesgo | Corrección recomendada | Pruebas |
|---|---|---:|---|---|---|---|---|
| P0 | `java:S5693` | 2 | Límites multipart administrados | FALSO_POSITIVO_VERIFICADO | MEDIO | Conservar límites si los endpoints siguen restringidos a administradores; añadir rate limiting/proxy limits si cambia la exposición. | Carga autorizada bajo el límite, rechazo sobre el límite y acceso anónimo denegado. |
| P0 | `java:S6437` | 2 | Secretos fuera del repositorio | CORREGIR_Y_ROTAR | ALTO | Revocar/rotar la credencial, retirarla del historial utilizable y cargarla exclusivamente desde variables o un gestor de secretos. | Secret scan, arranque sin secreto por defecto, prueba de configuración con variable y verificación operativa de rotación. |
| P0 | `secrets:S6703` | 2 | Secretos fuera del repositorio | CORREGIR_Y_ROTAR | ALTO | Rotar la contraseña y exigir SPRING_DATASOURCE_PASSWORD o gestor de secretos sin valor sensible predeterminado. | Secret scan, conexión con secreto inyectado y fallo seguro cuando falta la variable. |
| P0 | `java:S4502` | 1 | Modelo CSRF para API JWT | REVISAR_RIESGO_ACEPTABLE | ALTO | Documentar y probar que la API es stateless, usa Bearer JWT y no autentica con cookies; si aparece autenticación por cookies, habilitar CSRF para esas rutas. | Pruebas MockMvc de sesión STATELESS, ausencia de cookies de autenticación y autorización de rutas mutables. |
| P0 | `java:S5122` | 1 | CORS centralizado | CORREGIR_O_JUSTIFICAR | ALTO | Centralizar CorsConfigurationSource y limitar orígenes, métodos y cabeceras mediante configuración validada. | MockMvc con Origin permitido/no permitido y preflight OPTIONS. |
| P1 | `java:S6809` | 11 | Evitar self-invocation transaccional | CORREGIR | ALTO | Extraer el método transaccional a un colaborador inyectado o mover la frontera transaccional al método público invocado externamente. | Integración con base de datos que verifique commit/rollback y propagación ante excepciones. |
| P1 | `java:S8786` | 4 | Regex lineal | CORREGIR | ALTO | Reescribir la expresión para complejidad lineal, acotar entrada y precompilar Pattern. | Benchmark/timeout con entradas largas adversarias y casos funcionales existentes. |
| P1 | `java:S6218` | 2 | Igualdad profunda de arrays | CORREGIR | ALTO | Usar Arrays.equals/deepEquals, Arrays.hashCode/deepHashCode y representación defensiva; considerar una clase en vez de record si hay arrays mutables. | Objetos con arrays distintos pero mismo contenido, hash collections, null y copia defensiva. |
| P1 | `java:S5998` | 2 | Regex sin recursión peligrosa | CORREGIR | ALTO | Simplificar la regex, usar cuantificadores posesivos/grupos atómicos cuando corresponda o validación imperativa con límite de longitud. | Casos válidos/inválidos, entrada máxima y prueba de tiempo/stack con cadenas adversarias. |
| P1 | `java:S8989` | 1 | Rollback explícito | CORREGIR | ALTO | Definir rollbackFor/noRollbackFor según el contrato o convertir la excepción a una excepción de dominio no comprobada. | Integración que fuerce la excepción y compruebe rollback completo. |
| P1 | `java:S5866` | 1 | Regex Unicode explícita | CORREGIR | MEDIO | Activar la bandera Unicode compatible y validar caracteres españoles, símbolos y separadores. | Precios/textos ASCII y Unicode, tildes, ñ y espacios no separables. |
| P1 | `java:S5850` | 1 | Precedencia regex explícita | CORREGIR | MEDIO | Agrupar alternativas con grupos no capturantes y anclar el conjunto completo. | Tabla parametrizada de valores aceptados/rechazados y entradas parciales. |
| P2 | `java:S8688` | 88 | Clock/ZoneId explícito | CORREGIR | MEDIO | Definir una zona de negocio o inyectar Clock; usar el mismo criterio en entidades, servicios y pruebas. | Clock fijo, cambio de zona/DST, persistencia y comparación de fechas. |
| P2 | `java:S3776` | 10 | Reducir complejidad cognitiva | CORREGIR | ALTO | Extraer validaciones y operaciones con nombres de dominio, usar retornos tempranos y separar responsabilidades sin alterar el flujo. | Caracterización antes de refactorizar, ramas felices, bordes, excepciones y verify/never. |
| P2 | `java:S107` | 2 | Objeto de parámetros | CORREGIR_GRADUALMENTE | ALTO | Introducir request/criteria/value object validado, manteniendo compatibilidad temporal si es API pública. | Contrato, validación, mapeo y consumidores del método. |
| P2 | `java:S3358` | 2 | Eliminar ternarios anidados | CORREGIR | MEDIO | Extraer la decisión a if/else o método con nombre y pruebas por rama. | Prueba de cada combinación que alimenta el ternario. |
| P2 | `java:S1141` | 1 | Extraer try anidado | CORREGIR | MEDIO | Extraer la operación interna y su política de error a un método colaborador. | Éxito y excepción en ambos niveles, causa preservada y efectos laterales. |
| P2 | `java:S5843` | 1 | Regex dividida y documentada | CORREGIR | MEDIO | Dividir en patrones con nombre o validación por etapas y precompilar. | Suite parametrizada de coincidencias y no coincidencias reales. |
| P3 | `java:S5778` | 22 | Lambda assertThrows de una invocación | CORREGIR | BAJO | Preparar datos fuera de la lambda y dejar una sola invocación bajo assertThrows. | Ejecutar la propia prueba y confirmar tipo/mensaje e interacciones. |
| P3 | `java:S1192` | 10 | Constantes Java compartidas | CORREGIR | BAJO | Extraer constantes privadas con nombre; no compartir constantes entre conceptos coincidentes por accidente. | Compilación y pruebas existentes del componente. |
| P3 | `java:S1659` | 3 | Una declaración por línea | CORREGIR | BAJO | Separar declaraciones sin alterar inicialización. | Compilación y pruebas existentes. |
| P3 | `java:S9391` | 3 | Loop versus Stream | REVISAR_RIESGO_ACEPTABLE | MEDIO | Convertir solo si mejora claridad y preserva orden/efectos; de lo contrario aceptar justificadamente el loop. | Orden, duplicados, colección vacía y efectos laterales. |
| P3 | `java:S119` | 3 | Genéricos con nombre convencional | CORREGIR | BAJO | Renombrar el parámetro genérico de forma consistente en interfaz e implementación. | Compilación completa. |
| P3 | `java:S8924` | 2 | Static imports Mockito | CORREGIR | BAJO | Usar imports estáticos coherentes con el resto de tests. | Compilación y ejecución del test afectado. |
| P3 | `java:S1128` | 2 | Imports | CORREGIR | BAJO | Eliminar únicamente el import señalado y ordenar imports. | Compilación del módulo. |
| P3 | `java:S1172` | 2 | Parámetros no usados | REVISAR_CONTRATO | MEDIO | Eliminar el parámetro y actualizar llamadas, o documentar/usar el parámetro si forma parte de un contrato requerido. | Compilación, consumidores y pruebas del método. |
| P3 | `java:S1135` | 1 | Resolver TODO | CORREGIR | BAJO | Implementar la verificación pendiente o convertirla en tarea rastreable y retirar el comentario. | Ejecutar la prueba de integración afectada. |
| P3 | `java:S5853` | 1 | Cadena AssertJ única | CORREGIR | BAJO | Unificarlas en una cadena AssertJ legible sin perder mensajes. | Ejecutar el test afectado. |
| P3 | `java:S112` | 1 | Excepción específica | CORREGIR | MEDIO | Usar excepción de biblioteca o dominio con causa preservada y manejo global coherente. | Tipo, mensaje, causa y respuesta HTTP si atraviesa controlador. |
| P3 | `java:S1130` | 1 | Throws innecesario | CORREGIR | BAJO | Eliminar throws y adaptar consumidores si fuera necesario. | Compilación y carga de contexto. |
| P3 | `java:S5673` | 1 | Estereotipo Service | CORREGIR_O_JUSTIFICAR | BAJO | Cambiar a @Service si representa lógica de negocio; conservar @Component si no lo es y documentar. | Carga de contexto e inyección del bean. |
| P3 | `java:S135` | 1 | Control de loop | CORREGIR | MEDIO | Extraer predicados o método de procesamiento y reducir saltos sin cambiar orden. | Entradas con cada ruta de salto, límites y colección vacía. |
| P3 | `java:S4144` | 1 | Callbacks duplicados | CORREGIR | MEDIO | Extraer método común o delegar ambos callbacks; verificar si las reglas temporales deberían diferir. | Callbacks create/update con timestamps y estados nulos. |
| P4 | `plsql:VarcharUsageCheck` | 161 | Falso positivo PostgreSQL VARCHAR | FALSO_POSITIVO_PROBABLE | ALTO | No cambiar VARCHAR por VARCHAR2. Configurar el análisis para reconocer PostgreSQL o excluir estas migraciones de reglas PL/SQL incompatibles. | Validación Flyway sobre PostgreSQL real y arranque de contexto; no modificar migraciones aplicadas. |
| P4 | `plsql:S1192` | 53 | Literales repetidos en migraciones inmutables | RIESGO_ACEPTABLE_PROBABLE | ALTO | Preferir una migración nueva con CTE/VALUES reutilizable solo si mejora claridad; para migraciones aplicadas, aceptar el riesgo o excluir duplicación. | Validación de checksum Flyway, migración desde base vacía y comprobación de datos sembrados. |
| P4 | `plsql:NullComparison` | 7 | Falso positivo PostgreSQL cadena vacía | FALSO_POSITIVO_PROBABLE | ALTO | Conservar las comparaciones <> '' y ajustar el perfil/dialecto de análisis; no sustituirlas por IS NOT NULL porque cambiaría la validación. | Migración V11 en PostgreSQL y pruebas de constraints con NULL, cadena vacía y texto válido. |

## Agrupación por archivo

| Archivo | Incidencias | Reglas | Prioridades |
|---|---:|---|---|
| `src/main/resources/db/migration/V1__baseline_schema_actual.sql` | 98 | `plsql:VarcharUsageCheck` | P4 |
| `src/main/resources/db/migration/V4__contenido_inicial_isanorte.sql` | 49 | `plsql:S1192` | P4 |
| `src/main/resources/db/migration/V2__contenido_dinamico_dominio.sql` | 33 | `plsql:S1192`, `plsql:VarcharUsageCheck` | P4 |
| `src/main/resources/db/migration/V14__create_pedidos_direcciones.sql` | 21 | `plsql:VarcharUsageCheck` | P4 |
| `src/main/resources/db/migration/V11__create_anuncios.sql` | 15 | `plsql:VarcharUsageCheck`, `plsql:NullComparison` | P4 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/scraping/PrecioHtmlExtractor.java` | 11 | `java:S5866`, `java:S5843`, `java:S3776`, `java:S1192`, `java:S135`, `java:S8688`, `java:S8786` | P1, P2, P3 |
| `src/main/resources/application.properties` | 6 | `java:S5693`, `java:S6437`, `secrets:S6703` | P0 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/CotizacionService.java` | 5 | `java:S3776`, `java:S6809`, `java:S8688` | P1, P2 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/scraping/UrlExternaPolicy.java` | 5 | `java:S6218`, `java:S3776`, `java:S1659` | P1, P2, P3 |
| `src/main/java/com/isanorte/constructora_api/model/ConfiguracionSitio.java` | 5 | `java:S5850`, `java:S8786`, `java:S8688`, `java:S4144` | P1, P2, P3 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/ProyectoService.java` | 4 | `java:S8688`, `java:S6809`, `java:S9391` | P1, P2, P3 |
| `src/main/java/com/isanorte/constructora_api/config/DemoHomeIsadecorSeeder.java` | 4 | `java:S1192`, `java:S107` | P2, P3 |
| `src/main/java/com/isanorte/constructora_api/config/SecurityConfig.java` | 4 | `java:S1192`, `java:S112`, `java:S1130`, `java:S4502` | P0, P3 |
| `src/test/java/com/isanorte/constructora_api/service/ProductoHijosIntegrationTests.java` | 4 | `java:S5778` | P3 |
| `src/main/java/com/isanorte/constructora_api/model/UnidadNegocio.java` | 4 | `java:S1192`, `java:S8688` | P2, P3 |
| `src/main/java/com/isanorte/constructora_api/model/DireccionCliente.java` | 3 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/Pedido.java` | 3 | `java:S8688` | P2 |
| `src/test/java/com/isanorte/constructora_api/service/implementation/DireccionClienteServiceTest.java` | 3 | `java:S5778`, `java:S8924` | P3 |
| `src/main/resources/db/migration/V13__create_catalogos.sql` | 3 | `plsql:VarcharUsageCheck` | P4 |
| `src/main/java/com/isanorte/constructora_api/service/competitor/SimilitudProductoService.java` | 3 | `java:S5673`, `java:S1659`, `java:S9391` | P3 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/ComparacionPrecioService.java` | 3 | `java:S3358`, `java:S8688` | P2 |
| `src/test/java/com/isanorte/constructora_api/service/implementation/scraping/PrecioHtmlExtractorTest.java` | 3 | `java:S5778` | P3 |
| `src/test/java/com/isanorte/constructora_api/ProductCategoryUnitIntegrityIntegrationTest.java` | 3 | `java:S5778` | P3 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/SeccionLandingService.java` | 3 | `java:S3776`, `java:S3358`, `java:S6809` | P1, P2 |
| `src/test/java/com/isanorte/constructora_api/DynamicContentPersistenceIntegrationTest.java` | 3 | `java:S5778` | P3 |
| `src/main/java/com/isanorte/constructora_api/model/Administrador.java` | 3 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/CategoriaProducto.java` | 3 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/Cotizacion.java` | 3 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/Empresa.java` | 3 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/Producto.java` | 3 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/Proyecto.java` | 3 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/RedSocial.java` | 3 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/Rol.java` | 3 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/SeccionLanding.java` | 3 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/Servicio.java` | 3 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/VarianteProducto.java` | 3 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/PedidoService.java` | 2 | `java:S3776`, `java:S8688` | P2 |
| `src/test/java/com/isanorte/constructora_api/service/implementation/PedidoServiceTest.java` | 2 | `java:S5778` | P3 |
| `src/main/java/com/isanorte/constructora_api/model/Catalogo.java` | 2 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/repository/specification/ProductoCatalogSpecifications.java` | 2 | `java:S1192` | P3 |
| `src/main/java/com/isanorte/constructora_api/model/Anuncio.java` | 2 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/Cliente.java` | 2 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/ClienteAuthService.java` | 2 | `java:S8688` | P2 |
| `src/main/resources/db/migration/V10__create_resenas_producto.sql` | 2 | `plsql:VarcharUsageCheck` | P4 |
| `src/test/java/com/isanorte/constructora_api/config/DemoProductSeederTest.java` | 2 | `java:S8924`, `java:S5853` | P3 |
| `src/main/java/com/isanorte/constructora_api/service/competitor/AbstractHtmlCompetidorProvider.java` | 2 | `java:S1172`, `java:S8786` | P1, P3 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/ComparacionCompetidoresService.java` | 2 | `java:S1128`, `java:S8688` | P2, P3 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/ScrapingPrecioService.java` | 2 | `java:S3776`, `java:S1141` | P2 |
| `src/test/java/com/isanorte/constructora_api/service/implementation/scraping/PaginaExternaClientTest.java` | 2 | `java:S5778` | P3 |
| `src/main/java/com/isanorte/constructora_api/model/AccionLanding.java` | 2 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/BeneficioServicio.java` | 2 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/ContenidoPagina.java` | 2 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/EstadisticaEmpresa.java` | 2 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/HeroScene.java` | 2 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/RecursoUnidadNegocio.java` | 2 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/SeoPagina.java` | 2 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/SolicitudContacto.java` | 2 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/ProductoService.java` | 2 | `java:S6809`, `java:S9391` | P1, P3 |
| `src/main/java/com/isanorte/constructora_api/model/ConfiguracionCalculo.java` | 2 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/SeguimientoPedido.java` | 1 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/service/IPublicContentService.java` | 1 | `java:S107` | P2 |
| `src/test/java/com/isanorte/constructora_api/PublicProductApiIntegrationTest.java` | 1 | `java:S1135` | P3 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/AnuncioService.java` | 1 | `java:S8688` | P2 |
| `src/test/java/com/isanorte/constructora_api/service/implementation/ProductoServiceTest.java` | 1 | `java:S5778` | P3 |
| `src/test/java/com/isanorte/constructora_api/service/implementation/ResenaProductoServiceTest.java` | 1 | `java:S5778` | P3 |
| `src/main/java/com/isanorte/constructora_api/controller/PublicContentController.java` | 1 | `java:S1128` | P3 |
| `src/main/java/com/isanorte/constructora_api/model/CarritoCliente.java` | 1 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/ResenaProducto.java` | 1 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/config/DemoProductSeeder.java` | 1 | `java:S8989` | P1 |
| `src/main/java/com/isanorte/constructora_api/config/AdminInitializer.java` | 1 | `java:S8786` | P1 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/AuthService.java` | 1 | `java:S8688` | P2 |
| `src/test/java/com/isanorte/constructora_api/service/implementation/AuthServiceTest.java` | 1 | `java:S5778` | P3 |
| `src/main/java/com/isanorte/constructora_api/controller/ArchivoController.java` | 1 | `java:S5122` | P0 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/scraping/PaginaExternaClient.java` | 1 | `java:S6218` | P1 |
| `src/test/java/com/isanorte/constructora_api/DynamicContentApiIntegrationTest.java` | 1 | `java:S1172` | P3 |
| `src/main/java/com/isanorte/constructora_api/dto/request/ConfiguracionSitioRequest.java` | 1 | `java:S5998` | P1 |
| `src/main/java/com/isanorte/constructora_api/dto/request/ConfiguracionSitioUpdateRequest.java` | 1 | `java:S5998` | P1 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/CategoriaProductoService.java` | 1 | `java:S6809` | P1 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/ConfiguracionSitioService.java` | 1 | `java:S6809` | P1 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/EmpresaService.java` | 1 | `java:S6809` | P1 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/ServicioService.java` | 1 | `java:S6809` | P1 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/UnidadNegocioService.java` | 1 | `java:S6809` | P1 |
| `src/main/java/com/isanorte/constructora_api/exception/GlobalExceptionHandler.java` | 1 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/DocumentoProducto.java` | 1 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/ImagenProducto.java` | 1 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/ImagenProyecto.java` | 1 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/Permiso.java` | 1 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/model/SeguimientoCotizacion.java` | 1 | `java:S8688` | P2 |
| `src/main/java/com/isanorte/constructora_api/repository/IGenericRepository.java` | 1 | `java:S119` | P3 |
| `src/main/java/com/isanorte/constructora_api/service/IGenericService.java` | 1 | `java:S119` | P3 |
| `src/main/java/com/isanorte/constructora_api/service/implementation/GenericService.java` | 1 | `java:S119` | P3 |

## Criterio de finalización por lote

- Tests focalizados y regresión pasan.
- No se altera contrato sin documentarlo.
- `git diff --check` limpio.
- Nuevo análisis SonarQube ejecutado sobre la rama corregida.
- Evidencia DESPUÉS y resultado de pruebas añadidos al ID correspondiente.

## Artefactos de auditoría

- `clasificacion-incidencias.json`: detalle completo de las 409 incidencias, incluido contexto del commit original.
- `clasificacion-incidencias.md`: índice humano por ID.
- `pendientes-estado-actual.md`: estado oficial de las 17 incidencias sin PNG válido.

