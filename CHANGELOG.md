# Changelog

## 2026-09-18

- Se agregaron endpoints administrativos para crear, actualizar y eliminar imágenes URL de proyectos existentes.
- Se incorporaron DTO dedicados, validación de pertenencia proyecto-imagen y eliminación mediante `orphanRemoval`.
- Se preservaron la creación anidada de imágenes en POST Proyecto y las imágenes existentes en PUT Proyecto.
- Se añadieron pruebas de integración JPA/MockMvc y se actualizaron OpenAPI y las guías de API/frontend.

## 2026-09-17

- Se agregaron endpoints administrativos específicos para crear, actualizar y eliminar variantes, imágenes, especificaciones y documentos de productos existentes.
- Se agregó el PUT idempotente de configuración de cálculo, con deshabilitación mediante `habilitada=false`.
- Se incorporaron DTO específicos, validación de pertenencia producto-hijo y control explícito del SKU único de variante.
- Se preservó la creación anidada de hijos y el comportamiento conservador del PUT principal de Producto.
- Se añadieron pruebas de integración JPA con H2 para CRUD, validaciones, pertenencia, `orphanRemoval`, vistas pública/administrativa y compatibilidad.
- Se actualizaron OpenAPI y las guías de API e integración frontend.
