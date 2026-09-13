package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.SeccionLandingRequest;
import com.isanorte.constructora_api.model.SeccionLanding;

public interface ISeccionLandingService extends IGenericService<SeccionLanding, UUID> {

    List<SeccionLanding> findByVisibleTrueOrderByOrdenAsc();

    SeccionLanding create(SeccionLandingRequest request);
}
