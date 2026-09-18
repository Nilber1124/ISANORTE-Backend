package com.isanorte.constructora_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.RedSocialRequest;
import com.isanorte.constructora_api.dto.response.RedSocialResponse;
import com.isanorte.constructora_api.model.RedSocial;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RedSocialMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    RedSocial toEntity(RedSocialRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateEntity(RedSocialRequest request, @MappingTarget RedSocial redSocial);

    RedSocialResponse toResponse(RedSocial redSocial);
}
