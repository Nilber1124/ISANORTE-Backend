package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.AccionLanding;

@Repository
public interface AccionLandingRepository extends IGenericRepository<AccionLanding, UUID> {
}
