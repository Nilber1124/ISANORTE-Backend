package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.ConfiguracionSitio;

@Repository
public interface ConfiguracionSitioRepository extends IGenericRepository<ConfiguracionSitio, UUID> {

}
