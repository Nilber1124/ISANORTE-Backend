# Fase 1A: Flyway y dominio de contenido dinámico

Esta fase incorpora el esquema y el modelo de persistencia. No publica nuevas capacidades REST, no agrega controllers ni DTO de API y no carga el contenido estático actual.

## Integración Flyway

Spring Boot 4.1.1 administra las versiones de `spring-boot-starter-flyway`, `flyway-core` y `flyway-database-postgresql` (Flyway 12.4.0). PostgreSQL requiere el módulo de base de datos separado; el starter aporta la autoconfiguración modular de Spring Boot 4.

Las migraciones están en `src/main/resources/db/migration`:

- `V1__baseline_schema_actual.sql`: definición completa del esquema anterior a esta fase. Una instalación nueva ejecuta V1 y puede construir el esquema original desde cero.
- `V2__contenido_dinamico_dominio.sql`: evolución aditiva y compatible con datos existentes: columnas, tablas, claves foráneas, checks e índices portables.
- `V3__constraints_parciales_postgresql.sql`: índices únicos parciales específicos de PostgreSQL para unidad destacada y SEO.

`spring.flyway.baseline-on-migrate=false` permanece desactivado de manera predeterminada y `clean` está deshabilitado. Esto evita que una aplicación apunte accidentalmente a un esquema poblado no administrado y lo adopte sin una revisión previa.

## Adopción segura de una base existente

Una base existente no debe ejecutar V1 encima de sus tablas. La adopción es una operación única y controlada:

1. Hacer backup y detener las escrituras de la aplicación.
2. Verificar que el esquema coincide con V1 y que no hay duplicados o valores negativos incompatibles con V2/V3.
3. Ejecutar una vez con `spring.flyway.baseline-on-migrate=true` y `spring.flyway.baseline-version=1`. Flyway registra V1 como baseline sin ejecutarla y aplica V2 y V3.
4. Confirmar en `flyway_schema_history` que el baseline 1 y las migraciones 2 y 3 quedaron exitosos.
5. Volver a iniciar sin el override; la configuración normal conserva `baseline-on-migrate=false`.

La habilitación permanente de `baseline-on-migrate` no es apropiada porque elimina una protección frente a una URL o esquema equivocados. Tampoco se usa un seed repetitivo ni `ddl-auto=update`.

## Hibernate y perfiles

- Configuración normal (desarrollo y futura producción): Flyway habilitado y `spring.jpa.hibernate.ddl-auto=validate`.
- Tests: H2 en modo PostgreSQL, Flyway habilitado y `ddl-auto=validate`. Se aplican V1 y V2; `spring.flyway.target=2` excluye únicamente V3 porque H2 no soporta los índices parciales de PostgreSQL.
- La V3 debe validarse contra PostgreSQL antes de desplegar.

Flyway siempre migra antes de que Hibernate valide. Hibernate detecta divergencias entre entidades y esquema, pero ya no crea ni altera tablas.

## Dominio incorporado

| Agregado | Nuevos datos o hijos |
| --- | --- |
| `ConfiguracionSitio` | `clave` única normalizada, `ContenidoPagina[]`, `SeoPagina[]` |
| `SeccionLanding` | `etiqueta`, `imagenAlt`, `HeroScene[]`, `AccionLanding[]`, tipo `UNIDAD_NEGOCIO` |
| `UnidadNegocio` | `destacado`, `imagenAlt`, `RecursoUnidadNegocio[]` |
| `Servicio` | `etiqueta`, `imagenAlt`, `BeneficioServicio[]` |
| `Proyecto` | `orden` |
| `ImagenProyecto` | `alt` |
| `Empresa` | `EstadisticaEmpresa[]` |
| Independiente | `SolicitudContacto` |

Los hijos pertenecen al padre mediante la FK en el hijo. Las colecciones nuevas de hijos de agregado usan `cascade=ALL` y `orphanRemoval=true`. `ContenidoPagina.tags` es una `List<String>` persistida en una tabla explícita con `@OrderColumn`.

## SEO

`SeoPagina` persiste `tipoPagina`, `title`, `description`, `ogImageUrl`, `robots` y la unidad opcional. `ogTitle` y `ogDescription` se derivarán de `title` y `description`; el canonical se derivará de la configuración y la ruta. No se almacenan copias sin una necesidad demostrada.

Los checks obligan a que `UNIDAD_NEGOCIO` tenga unidad y a que los tipos estáticos no la tengan. PostgreSQL garantiza una fila SEO por página estática y una por unidad mediante índices parciales.

## Constraints destacadas

- `configuraciones_sitio.clave` requerida y única.
- Una sección por `(configuracion_sitio_id, tipo)`; el orden no es único.
- Un contenido editorial por `(configuracion_sitio_id, pagina)`.
- Como máximo una unidad simultáneamente activa y destacada por empresa.
- Valores `orden >= 0` y `EstadisticaEmpresa.valor >= 0`.
- Enums persistidos como `VARCHAR` con checks; no se crean tipos enum nativos de PostgreSQL.
- `alt` permanece nullable donde su obligatoriedad depende del uso informativo de la imagen; esa regla corresponde a la futura capa de aplicación.

## Pendiente para fases siguientes

- DTO, mappers y endpoints públicos/administrativos para los nuevos datos.
- Validación de URLs de acciones y reglas condicionales de accesibilidad.
- Validación de servicio para la unidad destacada, además del constraint de PostgreSQL.
- Migración única del contenido visual hardcodeado.
- Admin Angular, consumo público, SSR, TransferState y SEO en Angular.

`docs/openapi.yaml` no cambia en esta fase porque no existen rutas ni contratos REST nuevos.
