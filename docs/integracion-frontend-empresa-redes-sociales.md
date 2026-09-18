# Guía de integración frontend: redes sociales de Empresa

Esta guía está preparada para el futuro panel Admin; este cambio no modifica Angular. Los endpoints son administrativos por intención, pero aún no requieren token porque Security/JWT está pendiente.

1. Cargar la empresa con `GET /api/empresa/{empresaId}`.
2. Crear una red con `POST /api/empresa/{empresaId}/redes-sociales` y guardar el `id` recibido.
3. Editar con `PUT /api/empresa/{empresaId}/redes-sociales/{redSocialId}` usando el DTO completo.
4. Tras un DELETE 204, retirar la red del estado local.

```http
POST /api/empresa/{empresaId}/redes-sociales
Content-Type: application/json

{
  "nombre": "Instagram",
  "url": "https://instagram.com/isanorte",
  "icono": "instagram",
  "orden": 0,
  "activo": false
}
```

El cliente no envía `id`, `empresaId` ni timestamps en el body y no intenta mover una red a otra empresa. Un 400 indica validación o pertenencia incorrecta; 404 indica empresa o red inexistente; 409 solo un conflicto real. `activo=false` y `orden=0` son valores válidos y deben conservarse en el estado de UI.

La alta inicial también puede incluir `redesSociales` en `POST /api/empresa`. La edición raíz `PUT /api/empresa/{id}` no recibe esa colección y la preserva junto con configuración y unidades; las modificaciones posteriores de redes usan exclusivamente estos endpoints.
