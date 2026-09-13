package com.isanorte.constructora_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.AdministradorUpdateRequest;
import com.isanorte.constructora_api.dto.response.AdministradorResponse;
import com.isanorte.constructora_api.model.Administrador;
import com.isanorte.constructora_api.model.Rol;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AdministradorMapper {

    AdministradorResponse toResponse(Administrador administrador);

    AdministradorResponse.RolResumen toRolResumen(Rol rol);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "ultimoAcceso", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateEntity(AdministradorUpdateRequest request, @MappingTarget Administrador administrador);
}
