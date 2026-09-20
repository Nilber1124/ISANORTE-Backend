package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ConfiguracionSitioRequest;
import com.isanorte.constructora_api.dto.request.ConfiguracionSitioUpdateRequest;
import com.isanorte.constructora_api.dto.response.ConfiguracionSitioResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.ConfiguracionSitioMapper;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.service.IConfiguracionSitioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConfiguracionSitioService extends GenericService<ConfiguracionSitio, UUID> implements IConfiguracionSitioService {

    private final ConfiguracionSitioRepository configuracionSitioRepository;
    private final EmpresaRepository empresaRepository;
    private final ConfiguracionSitioMapper configuracionSitioMapper;

    @Override
    protected IGenericRepository<ConfiguracionSitio, UUID> getRepo() {
        return configuracionSitioRepository;
    }

    @Override
    @Transactional
    public ConfiguracionSitio create(ConfiguracionSitioRequest request) {
        Empresa empresa = empresaRepository.findById(request.empresaId())
                .orElseThrow(() -> new ModelNotFoundException("Empresa no encontrada con ID: " + request.empresaId()));
        if (empresa.getConfiguracionSitio() != null) {
            throw new IllegalStateException("La empresa ya tiene una configuración de sitio");
        }
        if (request.clave() != null && configuracionSitioRepository.existsByClave(request.clave())) {
            throw new IllegalStateException("Ya existe una configuración con clave: " + request.clave());
        }
        ConfiguracionSitio configuracion = configuracionSitioMapper.toEntity(request);
        if (request.secciones() != null) {
            request.secciones().stream()
                    .map(configuracionSitioMapper::toSeccionEntity)
                    .forEach(configuracion::addSeccion);
        }
        empresa.setConfiguracionSitio(configuracion);
        return configuracionSitioRepository.save(configuracion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfiguracionSitio> findAll() {
        List<ConfiguracionSitio> configuraciones = super.findAll();
        configuraciones.forEach(this::initializeForResponse);
        return configuraciones;
    }

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionSitio findById(UUID id) {
        ConfiguracionSitio configuracion = super.findById(id);
        initializeForResponse(configuracion);
        return configuracion;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfiguracionSitioResponse> findAllResponse() {
        return super.findAll().stream().map(configuracionSitioMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionSitioResponse findByIdResponse(UUID id) {
        return configuracionSitioMapper.toResponse(super.findById(id));
    }

    @Override
    @Transactional
    public ConfiguracionSitioResponse createResponse(ConfiguracionSitioRequest request) {
        return configuracionSitioMapper.toResponse(create(request));
    }

    @Override
    @Transactional
    public ConfiguracionSitioResponse update(UUID id, ConfiguracionSitioUpdateRequest request) {
        ConfiguracionSitio configuracion = super.findById(id);
        if (request.clave() != null && configuracionSitioRepository.existsByClaveAndIdNot(request.clave(), id)) {
            throw new IllegalStateException("Ya existe una configuración con clave: " + request.clave());
        }
        configuracionSitioMapper.updateEntity(request, configuracion);
        if (request.clave() != null) configuracion.setClave(request.clave());
        return configuracionSitioMapper.toResponse(configuracionSitioRepository.saveAndFlush(configuracion));
    }

    private void initializeForResponse(ConfiguracionSitio configuracion) {
        configuracion.getEmpresa().getNombreComercial();
        configuracion.getSecciones().size();
    }
}
