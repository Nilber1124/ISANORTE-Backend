package com.isanorte.constructora_api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import com.isanorte.constructora_api.model.Administrador;

@Repository
public interface AdministradorRepository extends IGenericRepository<Administrador, UUID> {

    Optional<Administrador> findByEmail(String email);

    @EntityGraph(attributePaths = "roles")
    Optional<Administrador> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}
