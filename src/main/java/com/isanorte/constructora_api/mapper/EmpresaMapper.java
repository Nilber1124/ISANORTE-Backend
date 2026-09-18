package com.isanorte.constructora_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.EmpresaRequest;
import com.isanorte.constructora_api.dto.request.EmpresaUpdateRequest;
import com.isanorte.constructora_api.dto.response.EmpresaResponse;
import com.isanorte.constructora_api.model.Empresa;

@Mapper(componentModel = "spring", uses = RedSocialMapper.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EmpresaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "configuracionSitio", ignore = true)
    @Mapping(target = "redesSociales", ignore = true)
    @Mapping(target = "unidadesNegocio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Empresa toEntity(EmpresaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "configuracionSitio", ignore = true)
    @Mapping(target = "redesSociales", ignore = true)
    @Mapping(target = "unidadesNegocio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateEntity(EmpresaUpdateRequest request, @MappingTarget Empresa empresa);

    EmpresaResponse toResponse(Empresa empresa);
}
