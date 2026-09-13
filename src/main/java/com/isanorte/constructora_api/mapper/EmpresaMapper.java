package com.isanorte.constructora_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.EmpresaRequest;
import com.isanorte.constructora_api.dto.response.EmpresaResponse;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.RedSocial;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EmpresaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "configuracionSitio", ignore = true)
    @Mapping(target = "redesSociales", ignore = true)
    @Mapping(target = "unidadesNegocio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Empresa toEntity(EmpresaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    RedSocial toRedSocialEntity(EmpresaRequest.RedSocialRequest request);

    EmpresaResponse toResponse(Empresa empresa);

    EmpresaResponse.RedSocialResponse toRedSocialResponse(RedSocial redSocial);
}
