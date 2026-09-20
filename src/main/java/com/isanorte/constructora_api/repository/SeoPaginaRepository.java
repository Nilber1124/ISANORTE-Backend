package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.SeoPagina;

@Repository
public interface SeoPaginaRepository extends IGenericRepository<SeoPagina, UUID> {
}
