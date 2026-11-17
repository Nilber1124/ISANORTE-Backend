package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.model.Faq;

public interface FaqRepository extends IGenericRepository<Faq, UUID> {

    List<Faq> findAllByOrderByOrdenAscIdAsc();

    List<Faq> findByActivoTrueOrderByOrdenAscIdAsc();
}
