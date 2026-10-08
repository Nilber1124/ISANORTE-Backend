package com.isanorte.constructora_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.enums.DestinoAnuncio;
import com.isanorte.constructora_api.model.Anuncio;

@Repository
public interface AnuncioRepository extends IGenericRepository<Anuncio, UUID> {

    List<Anuncio> findAllByOrderByOrdenAscFechaCreacionDescIdAsc();

    @Query("""
            select anuncio from Anuncio anuncio
            where anuncio.activo = true
              and (anuncio.destino = :destino
                   or anuncio.destino = com.isanorte.constructora_api.enums.DestinoAnuncio.AMBOS)
              and (anuncio.fechaInicio is null or anuncio.fechaInicio <= :ahora)
              and (anuncio.fechaFin is null or anuncio.fechaFin >= :ahora)
            order by anuncio.orden asc, anuncio.fechaCreacion desc, anuncio.id asc
            """)
    List<Anuncio> findPublicosVigentes(
            @Param("destino") DestinoAnuncio destino,
            @Param("ahora") LocalDateTime ahora);
}

