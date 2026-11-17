package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.model.ClienteMarca;

public interface ClienteMarcaRepository extends IGenericRepository<ClienteMarca, UUID> {

    List<ClienteMarca> findAllByOrderByOrdenAscIdAsc();

    List<ClienteMarca> findByActivoTrueOrderByOrdenAscIdAsc();
}
