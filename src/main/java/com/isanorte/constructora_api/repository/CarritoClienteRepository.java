package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.CarritoCliente;

@Repository
public interface CarritoClienteRepository extends IGenericRepository<CarritoCliente, UUID> {
}
