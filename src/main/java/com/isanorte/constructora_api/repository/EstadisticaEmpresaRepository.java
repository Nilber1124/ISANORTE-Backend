package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.EstadisticaEmpresa;

@Repository
public interface EstadisticaEmpresaRepository extends IGenericRepository<EstadisticaEmpresa, UUID> {
}
