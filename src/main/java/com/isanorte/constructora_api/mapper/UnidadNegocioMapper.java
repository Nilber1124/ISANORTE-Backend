package com.isanorte.constructora_api.mapper;

import java.util.Comparator;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.UnidadNegocioRequest;
import com.isanorte.constructora_api.dto.request.UnidadNegocioUpdateRequest;
import com.isanorte.constructora_api.dto.response.UnidadNegocioResponse;
import com.isanorte.constructora_api.dto.response.RecursoUnidadNegocioResponse;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.RecursoUnidadNegocio;
import com.isanorte.constructora_api.model.UnidadNegocio;

@Mapper(componentModel = "spring", uses = DynamicContentMapper.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UnidadNegocioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recursos", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "productos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    UnidadNegocio toEntity(UnidadNegocioRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recursos", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "productos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateEntity(UnidadNegocioUpdateRequest request, @MappingTarget UnidadNegocio unidadNegocio);

    @Mapping(target = "recursos", expression = "java(sortedResources(unidadNegocio.getRecursos()))")
    UnidadNegocioResponse toResponse(UnidadNegocio unidadNegocio);

    default List<RecursoUnidadNegocioResponse> sortedResources(List<RecursoUnidadNegocio> values) {
        return values.stream().sorted(Comparator.comparing(RecursoUnidadNegocio::getOrden)
                .thenComparing(value -> value.getId().toString()))
                .map(value -> new RecursoUnidadNegocioResponse(value.getId(), value.getTipo(), value.getUrl(),
                        value.getAlt(), value.getEtiqueta(), value.getOrden(), value.getActivo()))
                .toList();
    }

    UnidadNegocioResponse.EmpresaResumen toEmpresaResumen(Empresa empresa);
}
