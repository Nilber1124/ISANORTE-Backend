package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.DireccionCliente;

@Repository
public interface DireccionClienteRepository extends IGenericRepository<DireccionCliente, UUID> {

    List<DireccionCliente> findByClienteIdOrderByPrincipalDescFechaCreacionDesc(UUID clienteId);
}
