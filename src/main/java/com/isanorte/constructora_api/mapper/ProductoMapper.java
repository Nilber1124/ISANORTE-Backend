package com.isanorte.constructora_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.ProductoRequest;
import com.isanorte.constructora_api.dto.response.ProductoResponse;
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

    ProductoResponse toResponse(Producto producto);

    ProductoResponse.UnidadNegocioResumen toUnidadNegocioResumen(UnidadNegocio unidadNegocio);

    ProductoResponse.CategoriaResumen toCategoriaResumen(CategoriaProducto categoria);

    ProductoResponse.VarianteResponse toVarianteResponse(VarianteProducto variante);

    ProductoResponse.ImagenResponse toImagenResponse(ImagenProducto imagen);

    ProductoResponse.EspecificacionResponse toEspecificacionResponse(EspecificacionProducto especificacion);

    ProductoResponse.DocumentoResponse toDocumentoResponse(DocumentoProducto documento);

    ProductoResponse.ConfiguracionCalculoResponse toConfiguracionCalculoResponse(ConfiguracionCalculo configuracion);
}
