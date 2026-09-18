package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.RedSocial;

@Repository
public interface RedSocialRepository extends IGenericRepository<RedSocial, UUID> {
}
