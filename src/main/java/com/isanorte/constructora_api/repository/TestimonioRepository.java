package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.model.Testimonio;

public interface TestimonioRepository extends IGenericRepository<Testimonio, UUID> {

    List<Testimonio> findAllByOrderByOrdenAscIdAsc();

    List<Testimonio> findByActivoTrueOrderByOrdenAscIdAsc();
}
