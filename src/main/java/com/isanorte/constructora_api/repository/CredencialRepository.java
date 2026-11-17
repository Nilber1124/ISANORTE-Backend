package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.model.Credencial;

public interface CredencialRepository extends IGenericRepository<Credencial, UUID> {

    List<Credencial> findAllByOrderByOrdenAscIdAsc();

    List<Credencial> findByActivoTrueOrderByOrdenAscIdAsc();
}
