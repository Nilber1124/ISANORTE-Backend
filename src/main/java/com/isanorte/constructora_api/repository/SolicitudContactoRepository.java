package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.SolicitudContacto;

@Repository
public interface SolicitudContactoRepository extends IGenericRepository<SolicitudContacto, UUID> {
}
