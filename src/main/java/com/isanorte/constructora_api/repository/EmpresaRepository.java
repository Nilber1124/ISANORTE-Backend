package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.Empresa;

@Repository
public interface EmpresaRepository extends IGenericRepository<Empresa, UUID> {

    boolean existsByRucAndIdNot(String ruc, UUID id);
}
