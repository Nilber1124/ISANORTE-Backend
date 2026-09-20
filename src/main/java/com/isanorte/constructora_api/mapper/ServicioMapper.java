package com.isanorte.constructora_api.mapper;

import java.util.Comparator;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.ServicioRequest;
import com.isanorte.constructora_api.dto.request.ServicioUpdateRequest;
import com.isanorte.constructora_api.dto.response.ServicioResponse;
import com.isanorte.constructora_api.dto.response.BeneficioServicioResponse;
import com.isanorte.constructora_api.model.BeneficioServicio;
import com.isanorte.constructora_api.model.Servicio;

@Mapper(componentModel = "spring", uses = DynamicContentMapper.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ServicioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "beneficios", ignore = true)
    @Mapping(target = "proyectos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Servicio toEntity(ServicioRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "beneficios", ignore = true)
    @Mapping(target = "proyectos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateEntity(ServicioUpdateRequest request, @MappingTarget Servicio servicio);

    @Mapping(target = "beneficios", expression = "java(sortedBenefits(servicio.getBeneficios()))")
    ServicioResponse toResponse(Servicio servicio);

    default List<BeneficioServicioResponse> sortedBenefits(List<BeneficioServicio> values) {
        return values.stream().sorted(Comparator.comparing(BeneficioServicio::getOrden)
                .thenComparing(value -> value.getId().toString()))
                .map(value -> new BeneficioServicioResponse(value.getId(), value.getTexto(), value.getOrden(),
                        value.getActivo()))
                .toList();
    }
}
