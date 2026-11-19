package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;
import com.isanorte.constructora_api.model.IsadecorServicioPunto;

public interface IsadecorServicioPuntoRepository extends IGenericRepository<IsadecorServicioPunto, UUID> {

    List<IsadecorServicioPunto> findAllByOrderByOrdenAscIdAsc();
    List<IsadecorServicioPunto> findByServicioIdOrderByOrdenAscIdAsc(UUID servicioId);
}
