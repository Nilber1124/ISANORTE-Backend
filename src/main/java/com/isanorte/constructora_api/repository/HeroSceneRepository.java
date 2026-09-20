package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.HeroScene;

@Repository
public interface HeroSceneRepository extends IGenericRepository<HeroScene, UUID> {
}
