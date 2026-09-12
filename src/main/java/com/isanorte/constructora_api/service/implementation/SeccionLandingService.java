package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.isanorte.constructora_api.model.SeccionLanding;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.SeccionLandingRepository;
import com.isanorte.constructora_api.service.ISeccionLandingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeccionLandingService extends GenericService<SeccionLanding, UUID> implements ISeccionLandingService {

    private final SeccionLandingRepository seccionLandingRepository;

    @Override
    protected IGenericRepository<SeccionLanding, UUID> getRepo() {
        return seccionLandingRepository;
    }

    @Override
    public List<SeccionLanding> findByVisibleTrueOrderByOrdenAsc() {
        return seccionLandingRepository.findByVisibleTrueOrderByOrdenAsc();
    }
}
