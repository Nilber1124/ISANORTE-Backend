package com.isanorte.constructora_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.ProductoRequest;
import com.isanorte.constructora_api.dto.request.ProductoUpdateRequest;
import com.isanorte.constructora_api.dto.request.ConfiguracionCalculoRequest;
import com.isanorte.constructora_api.dto.request.DocumentoProductoRequest;
import com.isanorte.constructora_api.dto.request.EspecificacionProductoRequest;
import com.isanorte.constructora_api.dto.request.ImagenProductoRequest;
import com.isanorte.constructora_api.dto.request.VarianteProductoRequest;
import com.isanorte.constructora_api.dto.response.ConfiguracionCalculoResponse;
import com.isanorte.constructora_api.dto.response.DocumentoProductoResponse;
import com.isanorte.constructora_api.dto.response.EspecificacionProductoResponse;
import com.isanorte.constructora_api.dto.response.ImagenProductoResponse;
import com.isanorte.constructora_api.dto.response.ProductoResponse;
import com.isanorte.constructora_api.dto.response.VarianteProductoResponse;
import com.isanorte.constructora_api.model.CategoriaProducto;
import com.isanorte.constructora_api.model.ConfiguracionCalculo;
import com.isanorte.constructora_api.model.DocumentoProducto;
import com.isanorte.constructora_api.model.EspecificacionProducto;
import com.isanorte.constructora_api.model.ImagenProducto;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.model.VarianteProducto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProductoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "unidadNegocio", ignore = true)
    @Mapping(target = "categorias", ignore = true)
    @Mapping(target = "variantes", ignore = true)
    @Mapping(target = "imagenes", ignore = true)
    @Mapping(target = "especificaciones", ignore = true)
    @Mapping(target = "documentos", ignore = true)
    @Mapping(target = "configuracionCalculo", ignore = true)
    Producto toEntity(ProductoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "unidadNegocio", ignore = true)
    @Mapping(target = "categorias", ignore = true)
    @Mapping(target = "variantes", ignore = true)
    @Mapping(target = "imagenes", ignore = true)
    @Mapping(target = "especificaciones", ignore = true)
    @Mapping(target = "documentos", ignore = true)
    @Mapping(target = "configuracionCalculo", ignore = true)
    void updateEntity(ProductoUpdateRequest request, @MappingTarget Producto producto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    VarianteProducto toVarianteEntity(ProductoRequest.VarianteRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    ImagenProducto toImagenEntity(ProductoRequest.ImagenRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    EspecificacionProducto toEspecificacionEntity(ProductoRequest.EspecificacionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    DocumentoProducto toDocumentoEntity(ProductoRequest.DocumentoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    ConfiguracionCalculo toConfiguracionCalculoEntity(ProductoRequest.ConfiguracionCalculoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    VarianteProducto toVarianteEntity(VarianteProductoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateVarianteEntity(VarianteProductoRequest request, @MappingTarget VarianteProducto variante);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    ImagenProducto toImagenEntity(ImagenProductoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    void updateImagenEntity(ImagenProductoRequest request, @MappingTarget ImagenProducto imagen);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    EspecificacionProducto toEspecificacionEntity(EspecificacionProductoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    void updateEspecificacionEntity(
            EspecificacionProductoRequest request, @MappingTarget EspecificacionProducto especificacion);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    DocumentoProducto toDocumentoEntity(DocumentoProductoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    void updateDocumentoEntity(DocumentoProductoRequest request, @MappingTarget DocumentoProducto documento);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    ConfiguracionCalculo toConfiguracionCalculoEntity(ConfiguracionCalculoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateConfiguracionCalculoEntity(
            ConfiguracionCalculoRequest request, @MappingTarget ConfiguracionCalculo configuracion);

    ProductoResponse toResponse(Producto producto);

    ProductoResponse.UnidadNegocioResumen toUnidadNegocioResumen(UnidadNegocio unidadNegocio);

    ProductoResponse.CategoriaResumen toCategoriaResumen(CategoriaProducto categoria);

    VarianteProductoResponse toVarianteResponse(VarianteProducto variante);

    ImagenProductoResponse toImagenResponse(ImagenProducto imagen);

    EspecificacionProductoResponse toEspecificacionResponse(EspecificacionProducto especificacion);

    DocumentoProductoResponse toDocumentoResponse(DocumentoProducto documento);

    ConfiguracionCalculoResponse toConfiguracionCalculoResponse(ConfiguracionCalculo configuracion);
}
