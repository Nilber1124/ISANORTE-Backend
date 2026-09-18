package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.ImagenProyecto;

@Repository
public interface ImagenProyectoRepository extends IGenericRepository<ImagenProyecto, UUID> {
}
