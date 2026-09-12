package com.isanorte.constructora_api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.Rol;

@Repository
public interface RolRepository extends IGenericRepository<Rol, UUID> {

    Optional<Rol> findByNombre(String nombre);
}
