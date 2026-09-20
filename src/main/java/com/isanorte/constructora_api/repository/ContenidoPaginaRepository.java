package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.ContenidoPagina;

@Repository
public interface ContenidoPaginaRepository extends IGenericRepository<ContenidoPagina, UUID> {
}
