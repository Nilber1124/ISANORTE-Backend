package com.isanorte.constructora_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.ConfiguracionSitioRequest;
import com.isanorte.constructora_api.dto.request.ConfiguracionSitioUpdateRequest;
import com.isanorte.constructora_api.dto.response.ConfiguracionSitioResponse;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.SeccionLanding;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ConfiguracionSitioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "scriptsHead", ignore = true)
    @Mapping(target = "scriptsBody", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "secciones", ignore = true)
    @Mapping(target = "contenidosPagina", ignore = true)
    @Mapping(target = "seoPaginas", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    ConfiguracionSitio toEntity(ConfiguracionSitioRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clave", ignore = true)
    @Mapping(target = "scriptsHead", ignore = true)
    @Mapping(target = "scriptsBody", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "secciones", ignore = true)
    @Mapping(target = "contenidosPagina", ignore = true)
    @Mapping(target = "seoPaginas", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateEntity(ConfiguracionSitioUpdateRequest request, @MappingTarget ConfiguracionSitio configuracion);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "escenas", ignore = true)
    @Mapping(target = "acciones", ignore = true)
    @Mapping(target = "configuracionSitio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    SeccionLanding toSeccionEntity(ConfiguracionSitioRequest.SeccionRequest request);

    ConfiguracionSitioResponse toResponse(ConfiguracionSitio configuracion);

    ConfiguracionSitioResponse.EmpresaResumen toEmpresaResumen(Empresa empresa);

    ConfiguracionSitioResponse.SeccionResumen toSeccionResumen(SeccionLanding seccion);
}
