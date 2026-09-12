package com.isanorte.constructora_api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.Permiso;

@Repository
public interface PermisoRepository extends IGenericRepository<Permiso, UUID> {

    Optional<Permiso> findByCodigo(String codigo);
}
