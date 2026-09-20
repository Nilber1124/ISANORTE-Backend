package com.isanorte.constructora_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.ServicioRequest;
import com.isanorte.constructora_api.dto.request.ServicioUpdateRequest;
import com.isanorte.constructora_api.dto.response.ServicioResponse;
import com.isanorte.constructora_api.model.Servicio;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ServicioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "etiqueta", ignore = true)
    @Mapping(target = "imagenAlt", ignore = true)
    @Mapping(target = "beneficios", ignore = true)
    @Mapping(target = "proyectos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Servicio toEntity(ServicioRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "etiqueta", ignore = true)
    @Mapping(target = "imagenAlt", ignore = true)
    @Mapping(target = "beneficios", ignore = true)
    @Mapping(target = "proyectos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateEntity(ServicioUpdateRequest request, @MappingTarget Servicio servicio);

    ServicioResponse toResponse(Servicio servicio);
}
