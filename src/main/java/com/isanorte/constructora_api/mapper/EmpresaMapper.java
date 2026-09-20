package com.isanorte.constructora_api.mapper;

import java.util.Comparator;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.EmpresaRequest;
import com.isanorte.constructora_api.dto.request.EmpresaUpdateRequest;
import com.isanorte.constructora_api.dto.response.EmpresaResponse;
import com.isanorte.constructora_api.dto.response.EstadisticaEmpresaResponse;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.EstadisticaEmpresa;

@Mapper(componentModel = "spring", uses = {RedSocialMapper.class, DynamicContentMapper.class}, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EmpresaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "configuracionSitio", ignore = true)
    @Mapping(target = "redesSociales", ignore = true)
    @Mapping(target = "estadisticas", ignore = true)
    @Mapping(target = "unidadesNegocio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Empresa toEntity(EmpresaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "configuracionSitio", ignore = true)
    @Mapping(target = "redesSociales", ignore = true)
    @Mapping(target = "estadisticas", ignore = true)
    @Mapping(target = "unidadesNegocio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateEntity(EmpresaUpdateRequest request, @MappingTarget Empresa empresa);

    @Mapping(target = "estadisticas", expression = "java(sortedStatistics(empresa.getEstadisticas()))")
    EmpresaResponse toResponse(Empresa empresa);

    default List<EstadisticaEmpresaResponse> sortedStatistics(List<EstadisticaEmpresa> values) {
        return values.stream().sorted(Comparator.comparing(EstadisticaEmpresa::getOrden)
                .thenComparing(value -> value.getId().toString()))
                .map(value -> new EstadisticaEmpresaResponse(value.getId(), value.getValor(), value.getPrefijo(),
                        value.getSufijo(), value.getEtiqueta(), value.getOrden(), value.getActivo()))
                .toList();
    }
}
