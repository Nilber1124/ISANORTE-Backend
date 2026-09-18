# Changelog

## 2026-09-17

- Se agregaron endpoints administrativos específicos para crear, actualizar y eliminar variantes, imágenes, especificaciones y documentos de productos existentes.
- Se agregó el PUT idempotente de configuración de cálculo, con deshabilitación mediante `habilitada=false`.
- Se incorporaron DTO específicos, validación de pertenencia producto-hijo y control explícito del SKU único de variante.
- Se preservó la creación anidada de hijos y el comportamiento conservador del PUT principal de Producto.
- Se añadieron pruebas de integración JPA con H2 para CRUD, validaciones, pertenencia, `orphanRemoval`, vistas pública/administrativa y compatibilidad.
- Se actualizaron OpenAPI y las guías de API e integración frontend.
