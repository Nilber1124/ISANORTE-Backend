package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.BeneficioServicio;

@Repository
public interface BeneficioServicioRepository extends IGenericRepository<BeneficioServicio, UUID> {
}
