package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.EstadisticaEmpresa;

@Repository
public interface EstadisticaEmpresaRepository extends IGenericRepository<EstadisticaEmpresa, UUID> {

    List<EstadisticaEmpresa> findByEmpresaIdAndActivoTrueOrderByOrdenAscIdAsc(UUID empresaId);
}
