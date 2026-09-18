package com.isanorte.constructora_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.ProyectoRequest;
import com.isanorte.constructora_api.dto.request.ProyectoUpdateRequest;
import com.isanorte.constructora_api.dto.request.ImagenProyectoRequest;
import com.isanorte.constructora_api.dto.response.ImagenProyectoResponse;
import com.isanorte.constructora_api.dto.response.ProyectoResponse;
import com.isanorte.constructora_api.model.ImagenProyecto;
import com.isanorte.constructora_api.model.Proyecto;
import com.isanorte.constructora_api.model.Servicio;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProyectoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "servicios", ignore = true)
    @Mapping(target = "imagenes", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Proyecto toEntity(ProyectoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "servicios", ignore = true)
    @Mapping(target = "imagenes", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateEntity(ProyectoUpdateRequest request, @MappingTarget Proyecto proyecto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proyecto", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    ImagenProyecto toImagenEntity(ProyectoRequest.ImagenRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proyecto", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    ImagenProyecto toImagenEntity(ImagenProyectoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proyecto", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    void updateImagenEntity(ImagenProyectoRequest request, @MappingTarget ImagenProyecto imagen);

    ProyectoResponse toResponse(Proyecto proyecto);

    ProyectoResponse.ServicioResumen toServicioResumen(Servicio servicio);

    ImagenProyectoResponse toImagenResponse(ImagenProyecto imagen);
}
