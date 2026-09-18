# Guía de integración frontend: hijos de Producto

Esta guía es para el futuro panel administrativo. El frontend debe conservar el `productoId` de la ruta actual y usar siempre el ID del hijo retornado por POST. No debe enviar IDs dentro del body ni intentar mover hijos entre productos.

## Flujo recomendado

1. Obtener el agregado con `GET /api/productos/{productoId}`.
2. Crear el hijo con su endpoint POST y añadir la respuesta 201 al estado local.
3. Editar mediante PUT con el body completo y reemplazar en el estado local la respuesta 200.
4. Eliminar mediante DELETE; tras 204, retirar el hijo del estado local.
5. Ante 400, mostrar el mensaje de validación; ante 404, recargar el producto; ante 409, informar el duplicado o conflicto.

Ejemplo para una especificación:

```http
POST /api/productos/{productoId}/especificaciones
Content-Type: application/json

{
  "clave": "Espesor",
  "valor": "9 mm",
  "grupo": "Dimensiones",
  "orden": 1
}
```

Ejemplo para una imagen URL:

```http
POST /api/productos/{productoId}/imagenes
Content-Type: application/json

{
  "url": "https://cdn.example.com/wall-panel-roble.jpg",
  "altText": "Wall Panel Roble",
  "esPrincipal": true,
  "orden": 1
}
```

Ejemplo para establecer o deshabilitar la calculadora:

```http
PUT /api/productos/{productoId}/configuracion-calculo
Content-Type: application/json

{
  "habilitada": false,
  "etiquetaEntrada": "Área a cubrir",
  "unidadEntrada": "m2",
  "coberturaPorUnidad": 2.4,
  "unidadVenta": "caja",
  "textoAyuda": "El resultado se redondea hacia arriba"
}
```

Aunque `habilitada` sea `false`, se debe enviar una cobertura positiva. No hay upload de imágenes o documentos: el frontend envía una URL ya disponible. Estos endpoints aún no exigen token porque Security/JWT está pendiente; el cliente no debe interpretar esa ausencia como una garantía de acceso público futura.
