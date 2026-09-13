package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ConfiguracionSitioRequest;
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

    private void initializeForResponse(ConfiguracionSitio configuracion) {
        configuracion.getEmpresa().getNombreComercial();
        configuracion.getSecciones().size();
    }
}
