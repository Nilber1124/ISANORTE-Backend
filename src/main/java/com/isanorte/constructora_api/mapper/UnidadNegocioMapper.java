package com.isanorte.constructora_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.UnidadNegocioRequest;
import com.isanorte.constructora_api.dto.request.UnidadNegocioUpdateRequest;
import com.isanorte.constructora_api.dto.response.UnidadNegocioResponse;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.UnidadNegocio;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UnidadNegocioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "destacado", ignore = true)
    @Mapping(target = "imagenAlt", ignore = true)
    @Mapping(target = "recursos", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "productos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    UnidadNegocio toEntity(UnidadNegocioRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "destacado", ignore = true)
    @Mapping(target = "imagenAlt", ignore = true)
    @Mapping(target = "recursos", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "productos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateEntity(UnidadNegocioUpdateRequest request, @MappingTarget UnidadNegocio unidadNegocio);

    UnidadNegocioResponse toResponse(UnidadNegocio unidadNegocio);

    UnidadNegocioResponse.EmpresaResumen toEmpresaResumen(Empresa empresa);
}
