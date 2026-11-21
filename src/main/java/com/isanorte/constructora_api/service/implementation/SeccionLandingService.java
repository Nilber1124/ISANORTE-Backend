package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.SeccionLandingRequest;
import com.isanorte.constructora_api.dto.request.SeccionLandingUpdateRequest;
import com.isanorte.constructora_api.dto.request.VisibleRequest;
import com.isanorte.constructora_api.dto.response.SeccionLandingResponse;
import com.isanorte.constructora_api.dto.request.HeroSceneRequest;
import com.isanorte.constructora_api.dto.response.HeroSceneResponse;
import com.isanorte.constructora_api.enums.TipoSeccionLanding;
import com.isanorte.constructora_api.mapper.DynamicContentMapper;
import com.isanorte.constructora_api.model.HeroScene;
import com.isanorte.constructora_api.repository.HeroSceneRepository;
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
    private final DynamicContentMapper dynamicContentMapper;
    private final HeroSceneRepository heroSceneRepository;

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
        return createInternal(request);
    }

    private SeccionLanding createInternal(SeccionLandingRequest request) {
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

    @Override
    @Transactional(readOnly = true)
    public List<SeccionLandingResponse> findAllResponse() {
        return super.findAll().stream().map(seccionLandingMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SeccionLandingResponse findByIdResponse(UUID id) {
        return seccionLandingMapper.toResponse(super.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionLandingResponse> findVisibleResponses() {
        return seccionLandingRepository.findByVisibleTrueOrderByOrdenAsc().stream()
                .map(seccionLandingMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public SeccionLandingResponse createResponse(SeccionLandingRequest request) {
        return seccionLandingMapper.toResponse(createInternal(request));
    }

    @Override
    @Transactional
    public SeccionLandingResponse update(UUID id, SeccionLandingUpdateRequest request) {
        SeccionLanding seccion = super.findById(id);
        ConfiguracionSitio configuracion = configuracionSitioRepository.findById(request.configuracionSitioId())
                .orElseThrow(() -> new ModelNotFoundException(
                        "Configuración de sitio no encontrada con ID: " + request.configuracionSitioId()));
        if (!seccion.getConfiguracionSitio().getId().equals(configuracion.getId())) {
            throw new IllegalStateException("No se permite cambiar la configuración padre de una sección existente");
        }
        seccionLandingMapper.updateEntity(request, seccion);
        return seccionLandingMapper.toResponse(seccionLandingRepository.saveAndFlush(seccion));
    }

    @Override
    @Transactional
    public SeccionLandingResponse updateVisible(UUID id, VisibleRequest request) {
        SeccionLanding seccion = super.findById(id);
        seccion.setVisible(request.visible());
        return seccionLandingMapper.toResponse(seccionLandingRepository.saveAndFlush(seccion));
    }

    @Override
    @Transactional
    public HeroSceneResponse createEscena(UUID seccionId, HeroSceneRequest request) {
        SeccionLanding seccion = findSectionForAdmin(seccionId);
        requireHero(seccion);
        HeroScene escena = dynamicContentMapper.toEntity(request);
        seccion.addEscena(escena);
        return dynamicContentMapper.toResponse(heroSceneRepository.saveAndFlush(escena));
    }

    @Override
    @Transactional
    public HeroSceneResponse updateEscena(UUID seccionId, UUID escenaId, HeroSceneRequest request) {
        SeccionLanding seccion = findSectionForAdmin(seccionId);
        requireHero(seccion);
        HeroScene escena = heroSceneRepository.findById(escenaId)
                .orElseThrow(() -> new ModelNotFoundException("Escena Hero no encontrada con ID: " + escenaId));
        requireOwnership(seccion, escena.getSeccionLanding(), "escena", escenaId);
        dynamicContentMapper.update(request, escena);
        return dynamicContentMapper.toResponse(heroSceneRepository.saveAndFlush(escena));
    }

    @Override
    @Transactional
    public void deleteEscena(UUID seccionId, UUID escenaId) {
        SeccionLanding seccion = findSectionForAdmin(seccionId);
        HeroScene escena = heroSceneRepository.findById(escenaId)
                .orElseThrow(() -> new ModelNotFoundException("Escena Hero no encontrada con ID: " + escenaId));
        requireOwnership(seccion, escena.getSeccionLanding(), "escena", escenaId);
        seccion.removeEscena(escena);
        seccionLandingRepository.flush();
    }

    private SeccionLanding findSectionForAdmin(UUID id) {
        return seccionLandingRepository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Sección landing no encontrada con ID: " + id));
    }

    private void requireHero(SeccionLanding seccion) {
        if (seccion.getTipo() != TipoSeccionLanding.HERO) {
            throw new IllegalArgumentException("Las escenas solo pueden pertenecer a una sección HERO");
        }
    }

    private void requireOwnership(SeccionLanding expected, SeccionLanding actual, String resource, UUID id) {
        if (actual == null || !Objects.equals(expected.getId(), actual.getId())) {
            throw new IllegalArgumentException("La " + resource + " " + id + " no pertenece a la sección " + expected.getId());
        }
    }

    private void initializeForResponse(SeccionLanding seccion) {
        seccion.getConfiguracionSitio().getId();
        seccion.getEscenas().size();
        seccion.getAcciones().size();
    }
}
