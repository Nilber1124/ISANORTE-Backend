package com.isanorte.constructora_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.CategoriaProductoRequest;
import com.isanorte.constructora_api.dto.response.CategoriaProductoResponse;
import com.isanorte.constructora_api.model.CategoriaProducto;
import com.isanorte.constructora_api.model.UnidadNegocio;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoriaProductoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "unidadNegocio", ignore = true)
    @Mapping(target = "productos", ignore = true)
    CategoriaProducto toEntity(CategoriaProductoRequest request);

    CategoriaProductoResponse toResponse(CategoriaProducto categoria);

    CategoriaProductoResponse.UnidadNegocioResumen toUnidadNegocioResumen(UnidadNegocio unidadNegocio);
}
