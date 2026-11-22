package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isanorte.constructora_api.model.TextoBloque;

public interface TextoBloqueRepository extends JpaRepository<TextoBloque, UUID> {

    Optional<TextoBloque> findByClave(String clave);

    List<TextoBloque> findAllByOrderByClaveAsc();
}
