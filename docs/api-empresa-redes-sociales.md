# Administración de redes sociales de Empresa

Estos endpoints administran enlaces configurables de una empresa ya existente. Su intención es administrativa, pero siguen temporalmente sin protección: Spring Security/JWT aún no está implementado.

## Modelo y persistencia

`Empresa.redesSociales` es `OneToMany(mappedBy = "empresa", cascade = ALL, orphanRemoval = true)`. `RedSocial` es el lado dueño: tiene una FK obligatoria `empresa_id` mediante `ManyToOne(optional = false)`. Los helpers `addRedSocial/removeRedSocial` sincronizan ambos lados. Al retirar el hijo, `orphanRemoval` elimina su fila.

## Endpoints

| Operación | Endpoint | Éxito |
| --- | --- | --- |
| Crear | `POST /api/empresa/{empresaId}/redes-sociales` | 201 |
| Editar | `PUT /api/empresa/{empresaId}/redes-sociales/{redSocialId}` | 200 |
| Eliminar | `DELETE /api/empresa/{empresaId}/redes-sociales/{redSocialId}` | 204 |

POST y PUT reciben `nombre`, `url`, `icono`, `orden` y `activo`. Solo `nombre` y `url` son obligatorios por el contrato actual; `orden` y `activo` pueden ser nulos al crear y el modelo los normaliza a `0` y `true`. El body nunca acepta `id`, `empresaId`, timestamps ni entidades.

Una empresa inexistente o red inexistente responde 404. Si la red existe pero corresponde a otra empresa, PUT y DELETE responden 400 y nunca permiten reparenting. Los 409 quedan reservados a conflictos de integridad reales.

`activo=false` se persiste y se devuelve explícitamente. `orden=0` es válido y no existe una regla actual de unicidad ni reordenamiento automático. `icono` y `url` se almacenan como valores del dominio; no hay librerías, upload ni integración con APIs externas.

## Compatibilidad

- `POST /api/empresa` conserva la creación inicial anidada con `redesSociales`.
- `PUT /api/empresa/{id}` no recibe redes y conserva redes, configuración del sitio y unidades de negocio.
- No existe DELETE de Empresa.

El contrato detallado está en [openapi.yaml](openapi.yaml).
