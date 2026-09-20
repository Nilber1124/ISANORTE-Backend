package com.isanorte.constructora_api.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.SolicitudContacto;

@Repository
public interface SolicitudContactoRepository extends IGenericRepository<SolicitudContacto, UUID> {
    List<SolicitudContacto> findAllByOrderByFechaCreacionDesc();

    List<SolicitudContacto> findByEstadoOrderByFechaCreacionDesc(
            com.isanorte.constructora_api.enums.EstadoSolicitudContacto estado);
}
