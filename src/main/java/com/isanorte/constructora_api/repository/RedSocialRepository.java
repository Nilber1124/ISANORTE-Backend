package com.isanorte.constructora_api.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.RedSocial;

@Repository
public interface RedSocialRepository extends IGenericRepository<RedSocial, UUID> {
    List<RedSocial> findByEmpresaIdAndActivoTrueOrderByOrdenAscIdAsc(UUID empresaId);
}
