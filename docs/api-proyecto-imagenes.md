# Administración de imágenes de Proyecto

Estos endpoints administran imágenes de un proyecto ya existente. Tienen intención administrativa, pero temporalmente no están protegidos porque Spring Security/JWT todavía no está implementado.

## Modelo y persistencia

`Proyecto` contiene una relación `OneToMany(mappedBy = "proyecto", cascade = ALL, orphanRemoval = true)`. `ImagenProyecto` es el lado dueño de la FK obligatoria `proyecto_id` mediante `ManyToOne(optional = false)` y no puede existir sin proyecto. Los helpers `addImagen/removeImagen` mantienen ambos lados; al retirar una imagen de la colección, `orphanRemoval` elimina su fila.

No existe constraint ni regla de dominio que limite a una sola imagen principal. Para preservar el comportamiento actual, se permite más de una imagen con `esPrincipal=true`.

## Contrato

| Operación | Endpoint | Estado exitoso |
| --- | --- | --- |
| Crear | `POST /api/proyectos/{proyectoId}/imagenes` | 201 |
| Actualizar | `PUT /api/proyectos/{proyectoId}/imagenes/{imagenId}` | 200 |
| Eliminar | `DELETE /api/proyectos/{proyectoId}/imagenes/{imagenId}` | 204 |

POST y PUT reciben `url`, `titulo`, `descripcion`, `tipo`, `esPrincipal` y `orden`. `url`, `tipo`, `esPrincipal` y `orden` son obligatorios. `tipo` acepta exclusivamente `GENERAL`, `ANTES` o `DESPUES`. El body no acepta `id`, `proyectoId`, timestamps ni objetos Entity.

Un proyecto o una imagen inexistentes producen 404. Una imagen existente que pertenece a otro proyecto produce 400. El 409 queda reservado a conflictos reales de integridad. PUT conserva el ID y la FK, por lo que no mueve imágenes entre proyectos.

Las imágenes se administran exclusivamente mediante `url`; no existe upload físico, multipart, S3, Firebase, filesystem ni Cloudinary.

## Compatibilidad del agregado

- `POST /api/proyectos` mantiene la creación inicial con imágenes anidadas.
- `PUT /api/proyectos/{id}` actualiza los campos raíz y sincroniza servicios, pero no acepta imágenes y preserva la colección existente.
- No existe ni se agregó DELETE para el proyecto principal.

El contrato detallado está en [openapi.yaml](openapi.yaml).
