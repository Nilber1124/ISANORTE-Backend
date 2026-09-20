# Fase 1B: API de contenido dinámico

Esta fase publica los contratos REST administrativos y públicos sobre el dominio creado en Fase 1A. No modifica Angular, no migra contenido hardcodeado y no incorpora Spring Security, JWT, roles ni guards. `x-access-intent` en OpenAPI clasifica la intención futura, pero hoy no aplica autorización.

El contrato OpenAPI vigente contiene **83 paths y 116 operaciones HTTP**. De ellas, **31 operaciones** corresponden directamente a los recursos dinámicos, contacto y API pública incorporados en esta fase.

## Sitio canónico y política de `clave`

`ConfiguracionSitio.clave` se expone en create, update y response administrativos. Debe cumplir `[a-z0-9]+(?:-[a-z0-9]+)*`, tiene máximo 100 caracteres y es única.

La política adoptada es:

- la clave es editable únicamente mediante `PUT /api/configuracion-sitio/{id}`;
- no existe todavía un estado de publicación, por lo que no se inventa una inmutabilidad posterior a publicar;
- omitir `clave` en un update conserva la actual;
- por compatibilidad temporal con el Admin Angular existente, create todavía admite omitirla y genera `site-{empresaId}`;
- ese fallback es de transición, no la clave editorial definitiva: el Admin futuro debe reemplazarlo por una clave canónica elegida antes de publicar enlaces;
- ningún endpoint público cambia la clave.

`GET /api/publico/sitios/{clave}` devuelve únicamente configuración pública, datos públicos de empresa, redes activas y unidades activas. No expone RUC, scripts, IDs internos ni campos administrativos. Redes y unidades salen por `orden`, con ID como desempate interno estable.

## Home pública agregada

`GET /api/publico/sitios/{clave}/home` entrega una sola representación de contenido, sin HTML, CSS ni nombres de componentes Angular:

- secciones de la configuración solicitada con `visible=true`, ordenadas;
- escenas y acciones activas, ordenadas;
- servicios globales `activo=true AND destacado=true`, ordenados;
- proyectos globales `activo=true AND destacado=true`, ordenados;
- la única unidad de la empresa del sitio con `activo=true AND destacado=true`, o `null`;
- recursos activos y ordenados de esa unidad.

Servicios y proyectos son catálogos globales en el modelo de Fase 1A: no poseen relación con Empresa o ConfiguracionSitio. Por ello su filtro es global por actividad/destacado; no se simula un aislamiento que el dominio no representa. Secciones, unidad destacada, redes y unidades sí quedan aisladas por sitio/empresa.

El endpoint legado `GET /api/secciones-landing/visibles` se conserva por compatibilidad, pero es global y no es adecuado para multi-sitio. El frontend futuro debe consumir la Home canónica.

## CRUD de hijos

Los IDs padre siempre viajan en el path y nunca se aceptan entidades JPA en el body.

| Recurso | Alta | Cambio | Baja |
| --- | --- | --- | --- |
| Escena Hero | `POST /api/secciones-landing/{seccionId}/escenas` | `PUT /api/secciones-landing/{seccionId}/escenas/{escenaId}` | `DELETE` en la misma ruta |
| Acción Landing | `POST /api/secciones-landing/{seccionId}/acciones` | `PUT /api/secciones-landing/{seccionId}/acciones/{accionId}` | `DELETE` en la misma ruta |
| Beneficio | `POST /api/servicios/{servicioId}/beneficios` | `PUT /api/servicios/{servicioId}/beneficios/{beneficioId}` | `DELETE` en la misma ruta |
| Recurso de unidad | `POST /api/unidades-negocio/{unidadId}/recursos` | `PUT /api/unidades-negocio/{unidadId}/recursos/{recursoId}` | `DELETE` en la misma ruta |
| Estadística | `POST /api/empresa/{empresaId}/estadisticas` | `PUT /api/empresa/{empresaId}/estadisticas/{estadisticaId}` | `DELETE` en la misma ruta |

Todos validan existencia del padre y ownership; un hijo existente bajo otro padre produce 400 y ningún PUT permite reparenting. Las respuestas administrativas raíz anidan todas las escenas, acciones, beneficios, recursos y estadísticas, ordenadas determinísticamente.

Las escenas solo se admiten en secciones `HERO`. Las acciones se admiten en `HERO`, `SERVICIOS`, `PROYECTOS`, `CTA` y `UNIDAD_NEGOCIO`; `HERO` permite hasta dos activas y `CTA` hasta una activa. Los enlaces aceptan rutas internas con `/` (no protocol-relative `//`), fragmentos `#`, HTTPS bien formada, `mailto:` y `tel:`; se rechazan otros esquemas y caracteres de control.

`IMAGEN_EDITORIAL` exige `alt` no vacío. `IMAGEN_FONDO` y `CATALOGO` pueden omitirlo. No se implementa upload: `url` referencia un recurso existente.

## Contenido editorial y SEO

Contenido administrativo:

- `GET /api/contenidos-pagina`
- `GET /api/contenidos-pagina/{id}`
- `POST /api/contenidos-pagina`
- `PUT /api/contenidos-pagina/{id}`

La identidad `(configuracionSitioId, pagina)` es única y no cambia en update. Los tags conservan el orden enviado; `null` en update se interpreta como colección vacía. No se publica DELETE raíz.

SEO administrativo:

- `GET /api/seo-paginas`
- `GET /api/seo-paginas/{id}`
- `POST /api/seo-paginas`
- `PUT /api/seo-paginas/{id}`

`UNIDAD_NEGOCIO` exige `unidadNegocioId`; los tipos estáticos lo prohíben. La unidad debe pertenecer a la empresa de la configuración. Sitio, tipo y unidad constituyen identidad lógica inmutable en update. Los duplicados responden 409 y no se publica DELETE raíz.

`GET /api/publico/sitios/{clave}/paginas/{pagina}` acepta `NOSOTROS`, `SERVICIOS`, `PROYECTOS` y `CONTACTO`. Se eligió el enfoque editorial + SEO: no agrega catálogos de dominio. Un contenido inexistente o inactivo responde 404; SEO puede ser `null`.

`GET /api/publico/sitios/{clave}/unidades/{slug}` exige unidad activa de la empresa del sitio, devuelve solo recursos activos y agrega su SEO `UNIDAD_NEGOCIO` cuando existe. Una unidad inactiva o de otra empresa responde 404.

## Contacto

`POST /api/publico/contacto` valida nombre, email, teléfono y mensaje, crea siempre estado `NUEVA` y responde solo `id`, `estado` y `fechaCreacion`. No envía correo, no usa CAPTCHA y no crea cotizaciones.

La bandeja administrativa dispone de:

- `GET /api/solicitudes-contacto`, con filtro opcional `estado`;
- `GET /api/solicitudes-contacto/{id}`;
- `PATCH /api/solicitudes-contacto/{id}/estado`.

## Errores y compatibilidad

El `GlobalExceptionHandler` entrega `ApiErrorResponse`: 400 para validación/reglas y ownership, 404 para inexistencia, y 409 para duplicados o constraints. Las excepciones SQL no se exponen.

Los endpoints previos continúan disponibles. Los responses existentes incorporan `SeccionLanding.etiqueta/imagenAlt`, `UnidadNegocio.destacado/imagenAlt`, `Servicio.etiqueta/imagenAlt`, `Proyecto.orden`, `ImagenProyecto.alt` y `ConfiguracionSitio.clave` sin retirar campos anteriores.

## Integración futura

Angular deberá resolver primero la clave canónica, consumir `/api/publico/sitios/{clave}/home` para Home, el endpoint editorial para páginas estáticas y `/unidades/{slug}` para la ruta de unidad. La actualización del Admin deberá hacer `clave` explícita en create y permitir su edición controlada. Security debe proteger en una fase posterior todas las operaciones marcadas `ADMINISTRATIVO`; la clasificación actual no es una medida de seguridad.
