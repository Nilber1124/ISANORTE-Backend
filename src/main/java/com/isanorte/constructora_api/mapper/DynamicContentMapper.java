package com.isanorte.constructora_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.AccionLandingRequest;
import com.isanorte.constructora_api.dto.request.BeneficioServicioRequest;
import com.isanorte.constructora_api.dto.request.ContenidoPaginaRequest;
import com.isanorte.constructora_api.dto.request.ContenidoPaginaUpdateRequest;
import com.isanorte.constructora_api.dto.request.EstadisticaEmpresaRequest;
import com.isanorte.constructora_api.dto.request.HeroSceneRequest;
import com.isanorte.constructora_api.dto.request.RecursoUnidadNegocioRequest;
import com.isanorte.constructora_api.dto.request.SeoPaginaRequest;
import com.isanorte.constructora_api.dto.request.SeoPaginaUpdateRequest;
import com.isanorte.constructora_api.dto.request.SolicitudContactoCreateRequest;
import com.isanorte.constructora_api.dto.response.AccionLandingResponse;
import com.isanorte.constructora_api.dto.response.BeneficioServicioResponse;
import com.isanorte.constructora_api.dto.response.ContenidoPaginaResponse;
import com.isanorte.constructora_api.dto.response.EstadisticaEmpresaResponse;
import com.isanorte.constructora_api.dto.response.HeroSceneResponse;
import com.isanorte.constructora_api.dto.response.RecursoUnidadNegocioResponse;
import com.isanorte.constructora_api.dto.response.SeoPaginaResponse;
import com.isanorte.constructora_api.dto.response.SolicitudContactoPublicResponse;
import com.isanorte.constructora_api.dto.response.SolicitudContactoResponse;
import com.isanorte.constructora_api.model.AccionLanding;
import com.isanorte.constructora_api.model.BeneficioServicio;
import com.isanorte.constructora_api.model.ContenidoPagina;
import com.isanorte.constructora_api.model.EstadisticaEmpresa;
import com.isanorte.constructora_api.model.HeroScene;
import com.isanorte.constructora_api.model.RecursoUnidadNegocio;
import com.isanorte.constructora_api.model.SeoPagina;
import com.isanorte.constructora_api.model.SolicitudContacto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DynamicContentMapper {

    @Mapping(target = "id", ignore = true) @Mapping(target = "seccionLanding", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true) @Mapping(target = "fechaActualizacion", ignore = true)
    HeroScene toEntity(HeroSceneRequest request);
    @Mapping(target = "id", ignore = true) @Mapping(target = "seccionLanding", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true) @Mapping(target = "fechaActualizacion", ignore = true)
    void update(HeroSceneRequest request, @MappingTarget HeroScene entity);
    HeroSceneResponse toResponse(HeroScene entity);

    @Mapping(target = "id", ignore = true) @Mapping(target = "seccionLanding", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true) @Mapping(target = "fechaActualizacion", ignore = true)
    AccionLanding toEntity(AccionLandingRequest request);
    @Mapping(target = "id", ignore = true) @Mapping(target = "seccionLanding", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true) @Mapping(target = "fechaActualizacion", ignore = true)
    void update(AccionLandingRequest request, @MappingTarget AccionLanding entity);
    AccionLandingResponse toResponse(AccionLanding entity);

    @Mapping(target = "id", ignore = true) @Mapping(target = "servicio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true) @Mapping(target = "fechaActualizacion", ignore = true)
    BeneficioServicio toEntity(BeneficioServicioRequest request);
    @Mapping(target = "id", ignore = true) @Mapping(target = "servicio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true) @Mapping(target = "fechaActualizacion", ignore = true)
    void update(BeneficioServicioRequest request, @MappingTarget BeneficioServicio entity);
    BeneficioServicioResponse toResponse(BeneficioServicio entity);

    @Mapping(target = "id", ignore = true) @Mapping(target = "unidadNegocio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true) @Mapping(target = "fechaActualizacion", ignore = true)
    RecursoUnidadNegocio toEntity(RecursoUnidadNegocioRequest request);
    @Mapping(target = "id", ignore = true) @Mapping(target = "unidadNegocio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true) @Mapping(target = "fechaActualizacion", ignore = true)
    void update(RecursoUnidadNegocioRequest request, @MappingTarget RecursoUnidadNegocio entity);
    RecursoUnidadNegocioResponse toResponse(RecursoUnidadNegocio entity);

    @Mapping(target = "id", ignore = true) @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true) @Mapping(target = "fechaActualizacion", ignore = true)
    EstadisticaEmpresa toEntity(EstadisticaEmpresaRequest request);
    @Mapping(target = "id", ignore = true) @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true) @Mapping(target = "fechaActualizacion", ignore = true)
    void update(EstadisticaEmpresaRequest request, @MappingTarget EstadisticaEmpresa entity);
    EstadisticaEmpresaResponse toResponse(EstadisticaEmpresa entity);

    @Mapping(target = "id", ignore = true) @Mapping(target = "configuracionSitio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true) @Mapping(target = "fechaActualizacion", ignore = true)
    ContenidoPagina toEntity(ContenidoPaginaRequest request);
    @Mapping(target = "id", ignore = true) @Mapping(target = "pagina", ignore = true)
    @Mapping(target = "configuracionSitio", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true) @Mapping(target = "fechaActualizacion", ignore = true)
    void update(ContenidoPaginaUpdateRequest request, @MappingTarget ContenidoPagina entity);
    @Mapping(target = "configuracionSitioId", source = "configuracionSitio.id")
    ContenidoPaginaResponse toResponse(ContenidoPagina entity);

    @Mapping(target = "id", ignore = true) @Mapping(target = "configuracionSitio", ignore = true)
    @Mapping(target = "unidadNegocio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true) @Mapping(target = "fechaActualizacion", ignore = true)
    SeoPagina toEntity(SeoPaginaRequest request);
    @Mapping(target = "id", ignore = true) @Mapping(target = "tipoPagina", ignore = true)
    @Mapping(target = "configuracionSitio", ignore = true) @Mapping(target = "unidadNegocio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true) @Mapping(target = "fechaActualizacion", ignore = true)
    void update(SeoPaginaUpdateRequest request, @MappingTarget SeoPagina entity);
    @Mapping(target = "configuracionSitioId", source = "configuracionSitio.id")
    @Mapping(target = "unidadNegocioId", source = "unidadNegocio.id")
    SeoPaginaResponse toResponse(SeoPagina entity);

    @Mapping(target = "id", ignore = true) @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true) @Mapping(target = "fechaActualizacion", ignore = true)
    SolicitudContacto toEntity(SolicitudContactoCreateRequest request);
    SolicitudContactoResponse toResponse(SolicitudContacto entity);
    SolicitudContactoPublicResponse toPublicResponse(SolicitudContacto entity);
}
