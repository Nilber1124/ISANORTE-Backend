# Guía frontend: imágenes de Proyecto

Esta guía describe la futura integración del panel administrativo; no se modificó Angular en este cambio. Los endpoints aún no exigen token porque Security/JWT está pendiente.

El cliente debe conservar el `proyectoId` de la ruta y usar el `id` de la respuesta al crear. No debe enviar ninguno de esos IDs dentro del body ni intentar mover imágenes entre proyectos.

## Flujo recomendado

1. Obtener el agregado con `GET /api/proyectos/{proyectoId}`.
2. Crear con POST y añadir al estado local la respuesta 201.
3. Editar con PUT enviando el DTO completo y reemplazar el elemento por la respuesta 200.
4. Eliminar con DELETE y, tras 204, retirar el elemento del estado local.
5. Ante 400, mostrar la validación o pertenencia incorrecta; ante 404, recargar el proyecto; ante 409, informar un conflicto real de integridad.

```http
POST /api/proyectos/{proyectoId}/imagenes
Content-Type: application/json

{
  "url": "https://cdn.example.com/proyecto/fachada.jpg",
  "titulo": "Fachada terminada",
  "descripcion": "Vista principal del proyecto",
  "tipo": "DESPUES",
  "esPrincipal": true,
  "orden": 1
}
```

Los tipos válidos son `GENERAL`, `ANTES` y `DESPUES`. El backend no impone una única imagen principal. La URL debe corresponder a un recurso ya disponible: estos endpoints no suben archivos.

La creación principal (`POST /api/proyectos`) puede seguir incluyendo imágenes iniciales. La edición principal (`PUT /api/proyectos/{id}`) no recibe imágenes y las conserva; las altas, cambios y bajas posteriores usan exclusivamente los tres endpoints de esta guía.
