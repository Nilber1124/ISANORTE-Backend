package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ProductoRequest;
import com.isanorte.constructora_api.dto.request.EstadoPublicacionRequest;
import com.isanorte.constructora_api.dto.request.ConfiguracionCalculoRequest;
import com.isanorte.constructora_api.dto.request.DocumentoProductoRequest;
import com.isanorte.constructora_api.dto.request.EspecificacionProductoRequest;
import com.isanorte.constructora_api.dto.request.ImagenProductoRequest;
import com.isanorte.constructora_api.dto.request.ProductoUpdateRequest;
import com.isanorte.constructora_api.dto.request.VarianteProductoRequest;
import com.isanorte.constructora_api.dto.response.ConfiguracionCalculoResponse;
import com.isanorte.constructora_api.dto.response.DocumentoProductoResponse;
import com.isanorte.constructora_api.dto.response.EspecificacionProductoResponse;
import com.isanorte.constructora_api.dto.response.ImagenProductoResponse;
import com.isanorte.constructora_api.dto.response.ProductoResponse;
import com.isanorte.constructora_api.dto.response.VarianteProductoResponse;
import com.isanorte.constructora_api.model.Producto;

public interface IProductoService extends IGenericService<Producto, UUID> {

    Producto findBySlug(String slug);

    Producto findBySku(String sku);

    Producto create(ProductoRequest request);

    List<ProductoResponse> findAllResponse();

    ProductoResponse findByIdResponse(UUID id);

    ProductoResponse findBySlugResponse(String slug);

    ProductoResponse findBySkuResponse(String sku);

    List<ProductoResponse> findPublishedResponses();

    ProductoResponse findPublishedBySlugResponse(String slug);

    ProductoResponse findPublishedBySkuResponse(String sku);

    ProductoResponse createResponse(ProductoRequest request);

    ProductoResponse update(UUID id, ProductoUpdateRequest request);

    ProductoResponse updateEstado(UUID id, EstadoPublicacionRequest request);

    VarianteProductoResponse createVariante(UUID productoId, VarianteProductoRequest request);

    VarianteProductoResponse updateVariante(UUID productoId, UUID varianteId, VarianteProductoRequest request);

    void deleteVariante(UUID productoId, UUID varianteId);

    ImagenProductoResponse createImagen(UUID productoId, ImagenProductoRequest request);

    ImagenProductoResponse updateImagen(UUID productoId, UUID imagenId, ImagenProductoRequest request);

    void deleteImagen(UUID productoId, UUID imagenId);

    EspecificacionProductoResponse createEspecificacion(UUID productoId, EspecificacionProductoRequest request);

    EspecificacionProductoResponse updateEspecificacion(
            UUID productoId, UUID especificacionId, EspecificacionProductoRequest request);

    void deleteEspecificacion(UUID productoId, UUID especificacionId);

    DocumentoProductoResponse createDocumento(UUID productoId, DocumentoProductoRequest request);

    DocumentoProductoResponse updateDocumento(UUID productoId, UUID documentoId, DocumentoProductoRequest request);

    void deleteDocumento(UUID productoId, UUID documentoId);

    ConfiguracionCalculoResponse upsertConfiguracionCalculo(UUID productoId, ConfiguracionCalculoRequest request);
}
