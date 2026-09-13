package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.SeccionLandingRequest;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.SeccionLandingMapper;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.SeccionLanding;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.SeccionLandingRepository;
import com.isanorte.constructora_api.service.ISeccionLandingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeccionLandingService extends GenericService<SeccionLanding, UUID> implements ISeccionLandingService {

    private final SeccionLandingRepository seccionLandingRepository;
    private final ConfiguracionSitioRepository configuracionSitioRepository;
    private final SeccionLandingMapper seccionLandingMapper;

    @Override
    protected IGenericRepository<SeccionLanding, UUID> getRepo() {
        return seccionLandingRepository;
    }

    @Override
    public List<SeccionLanding> findByVisibleTrueOrderByOrdenAsc() {
        List<SeccionLanding> secciones = seccionLandingRepository.findByVisibleTrueOrderByOrdenAsc();
        secciones.forEach(this::initializeForResponse);
        return secciones;
    }

    @Override
    @Transactional
    public SeccionLanding create(SeccionLandingRequest request) {
        ConfiguracionSitio configuracion = configuracionSitioRepository.findById(request.configuracionSitioId())
                .orElseThrow(() -> new ModelNotFoundException(
                        "Configuración de sitio no encontrada con ID: " + request.configuracionSitioId()));
        SeccionLanding seccion = seccionLandingMapper.toEntity(request);
        configuracion.addSeccion(seccion);
        return seccionLandingRepository.save(seccion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionLanding> findAll() {
        List<SeccionLanding> secciones = super.findAll();
        secciones.forEach(this::initializeForResponse);
        return secciones;
    }

    @Override
    @Transactional(readOnly = true)
    public SeccionLanding findById(UUID id) {
        SeccionLanding seccion = super.findById(id);
        initializeForResponse(seccion);
        return seccion;
    }

    private void initializeForResponse(SeccionLanding seccion) {
        seccion.getConfiguracionSitio().getId();
    }
}
