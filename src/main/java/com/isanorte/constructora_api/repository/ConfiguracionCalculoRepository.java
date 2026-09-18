package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.ConfiguracionCalculo;

@Repository
public interface ConfiguracionCalculoRepository extends IGenericRepository<ConfiguracionCalculo, UUID> {
}
