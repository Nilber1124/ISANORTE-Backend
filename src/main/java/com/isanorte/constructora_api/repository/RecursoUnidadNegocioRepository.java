package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.RecursoUnidadNegocio;

@Repository
public interface RecursoUnidadNegocioRepository extends IGenericRepository<RecursoUnidadNegocio, UUID> {
}
