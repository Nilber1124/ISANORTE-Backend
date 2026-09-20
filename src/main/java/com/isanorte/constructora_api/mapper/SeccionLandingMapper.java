package com.isanorte.constructora_api.mapper;

import java.util.Comparator;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.SeccionLandingRequest;
import com.isanorte.constructora_api.dto.request.SeccionLandingUpdateRequest;
import com.isanorte.constructora_api.dto.response.SeccionLandingResponse;
import com.isanorte.constructora_api.dto.response.AccionLandingResponse;
import com.isanorte.constructora_api.dto.response.HeroSceneResponse;
import com.isanorte.constructora_api.model.AccionLanding;
import com.isanorte.constructora_api.model.HeroScene;
import com.isanorte.constructora_api.model.SeccionLanding;

@Mapper(componentModel = "spring", uses = DynamicContentMapper.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SeccionLandingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "escenas", ignore = true)
    @Mapping(target = "acciones", ignore = true)
    @Mapping(target = "configuracionSitio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    SeccionLanding toEntity(SeccionLandingRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "escenas", ignore = true)
    @Mapping(target = "acciones", ignore = true)
    @Mapping(target = "configuracionSitio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateEntity(SeccionLandingUpdateRequest request, @MappingTarget SeccionLanding seccion);

    @Mapping(target = "configuracionSitioId", source = "configuracionSitio.id")
    @Mapping(target = "escenas", expression = "java(sortedScenes(seccion.getEscenas()))")
    @Mapping(target = "acciones", expression = "java(sortedActions(seccion.getAcciones()))")
    SeccionLandingResponse toResponse(SeccionLanding seccion);

    default List<HeroSceneResponse> sortedScenes(List<HeroScene> values) {
        return values.stream().sorted(Comparator.comparing(HeroScene::getOrden)
                .thenComparing(value -> value.getId().toString()))
                .map(value -> new HeroSceneResponse(value.getId(), value.getImagenUrl(), value.getAlt(),
                        value.getOrden(), value.getActivo()))
                .toList();
    }

    default List<AccionLandingResponse> sortedActions(List<AccionLanding> values) {
        return values.stream().sorted(Comparator.comparing(AccionLanding::getOrden)
                .thenComparing(value -> value.getId().toString()))
                .map(value -> new AccionLandingResponse(value.getId(), value.getTexto(), value.getEnlace(),
                        value.getOrden(), value.getActivo()))
                .toList();
    }
}
