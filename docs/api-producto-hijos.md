# Administración de hijos de Producto

Los endpoints de este documento administran hijos de un producto ya existente. Son operaciones de intención administrativa, pero actualmente no tienen Spring Security ni JWT.

## Modelo y persistencia

`Producto` es el agregado propietario en memoria. Variantes, imágenes, especificaciones y documentos son relaciones `OneToMany(mappedBy = "producto", cascade = ALL, orphanRemoval = true)`. La configuración de cálculo es `OneToOne(mappedBy = "producto", cascade = ALL, orphanRemoval = true)` y su tabla tiene un `producto_id` único.

Los métodos `add/remove` de `Producto` mantienen ambos lados de cada relación. Al eliminar un hijo se lo retira de la colección y `orphanRemoval` elimina la fila. La configuración se crea o actualiza con PUT y se retira funcionalmente usando `habilitada=false`; no existe DELETE para ella.

## Endpoints

| Recurso | Crear | Actualizar | Eliminar |
| --- | --- | --- | --- |
| Variante | `POST /api/productos/{productoId}/variantes` | `PUT /api/productos/{productoId}/variantes/{varianteId}` | `DELETE /api/productos/{productoId}/variantes/{varianteId}` |
| Imagen | `POST /api/productos/{productoId}/imagenes` | `PUT /api/productos/{productoId}/imagenes/{imagenId}` | `DELETE /api/productos/{productoId}/imagenes/{imagenId}` |
| Especificación | `POST /api/productos/{productoId}/especificaciones` | `PUT /api/productos/{productoId}/especificaciones/{especificacionId}` | `DELETE /api/productos/{productoId}/especificaciones/{especificacionId}` |
| Documento | `POST /api/productos/{productoId}/documentos` | `PUT /api/productos/{productoId}/documentos/{documentoId}` | `DELETE /api/productos/{productoId}/documentos/{documentoId}` |
| Configuración | — | `PUT /api/productos/{productoId}/configuracion-calculo` | — |

POST responde 201, PUT responde 200 y DELETE responde 204 sin cuerpo. Un producto o hijo inexistente produce 404. Si el hijo existe pero pertenece a otro producto, la operación produce 400. Los duplicados y conflictos de integridad producen 409.

## Reglas

- El SKU de variante es obligatorio y globalmente único, de acuerdo con `uk_variante_sku`.
- Los PUT de hijos son reemplazos completos: deben incluir los campos obligatorios del DTO.
- Las imágenes continúan usando URL; no se realiza upload físico.
- El modelo no define una restricción de imagen principal única. Por compatibilidad, se permite más de una imagen con `esPrincipal=true`.
- Los documentos continúan usando URL y el enum `CATALOGO`, `FICHA_TECNICA`, `MANUAL` u `OTRO`.
- `coberturaPorUnidad` es obligatoria y mayor que cero, incluso al deshabilitar la configuración, porque esa es la restricción vigente de la entidad.
- `POST /api/productos` conserva la creación anidada existente.
- `PUT /api/productos/{id}` no acepta colecciones y preserva todos los hijos existentes.

El contrato detallado de request y response está en [openapi.yaml](openapi.yaml).
