package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.isanorte.constructora_api.model.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {

    List<Notificacion> findTop50ByOrderByFechaCreacionDesc();

    long countByLeidaFalse();

    @Modifying
    @Query("update Notificacion n set n.leida = true where n.leida = false")
    int marcarTodasLeidas();
}
