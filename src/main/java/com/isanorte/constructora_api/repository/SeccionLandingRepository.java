package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.SeccionLanding;

@Repository
public interface SeccionLandingRepository extends IGenericRepository<SeccionLanding, UUID> {

    List<SeccionLanding> findByVisibleTrueOrderByOrdenAsc();

    List<SeccionLanding> findByConfiguracionSitioIdAndVisibleTrueOrderByOrdenAscIdAsc(UUID configuracionSitioId);
}
