package com.isanorte.constructora_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.SeccionLandingRequest;
import com.isanorte.constructora_api.dto.request.SeccionLandingUpdateRequest;
import com.isanorte.constructora_api.dto.response.SeccionLandingResponse;
import com.isanorte.constructora_api.model.SeccionLanding;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SeccionLandingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "configuracionSitio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    SeccionLanding toEntity(SeccionLandingRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "configuracionSitio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateEntity(SeccionLandingUpdateRequest request, @MappingTarget SeccionLanding seccion);

    @Mapping(target = "configuracionSitioId", source = "configuracionSitio.id")
    SeccionLandingResponse toResponse(SeccionLanding seccion);
}
