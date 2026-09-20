package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.SeoPaginaRequest;
import com.isanorte.constructora_api.dto.request.SeoPaginaUpdateRequest;
import com.isanorte.constructora_api.dto.response.SeoPaginaResponse;
import com.isanorte.constructora_api.enums.TipoPaginaSeo;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.DynamicContentMapper;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.SeoPagina;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.SeoPaginaRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.ISeoPaginaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeoPaginaService implements ISeoPaginaService {
    private final SeoPaginaRepository repository;
    private final ConfiguracionSitioRepository configuracionRepository;
    private final UnidadNegocioRepository unidadRepository;
    private final DynamicContentMapper mapper;

    @Override @Transactional(readOnly = true)
    public List<SeoPaginaResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public SeoPaginaResponse findById(UUID id) {
        return mapper.toResponse(find(id));
    }

    @Override @Transactional
    public SeoPaginaResponse create(SeoPaginaRequest request) {
        ConfiguracionSitio site = configuracionRepository.findById(request.configuracionSitioId())
                .orElseThrow(() -> new ModelNotFoundException("Configuración no encontrada con ID: " + request.configuracionSitioId()));
        UnidadNegocio unit = validateIdentity(site, request.tipoPagina(), request.unidadNegocioId());
        if (unit == null && repository.existsByConfiguracionSitioIdAndTipoPaginaAndUnidadNegocioIsNull(
                site.getId(), request.tipoPagina())) {
            throw new IllegalStateException("Ya existe SEO para la página estática indicada");
        }
        if (unit != null && repository.existsByUnidadNegocioId(unit.getId())) {
            throw new IllegalStateException("La unidad ya tiene configuración SEO");
        }
        SeoPagina entity = mapper.toEntity(request);
        entity.setConfiguracionSitio(site);
        entity.setUnidadNegocio(unit);
        return mapper.toResponse(repository.saveAndFlush(entity));
    }

    @Override @Transactional
    public SeoPaginaResponse update(UUID id, SeoPaginaUpdateRequest request) {
        SeoPagina entity = find(id);
        mapper.update(request, entity);
        return mapper.toResponse(repository.saveAndFlush(entity));
    }

    private UnidadNegocio validateIdentity(ConfiguracionSitio site, TipoPaginaSeo type, UUID unitId) {
        if (type == TipoPaginaSeo.UNIDAD_NEGOCIO && unitId == null) {
            throw new IllegalArgumentException("unidadNegocioId es obligatorio para SEO de unidad de negocio");
        }
        if (type != TipoPaginaSeo.UNIDAD_NEGOCIO && unitId != null) {
            throw new IllegalArgumentException("Las páginas estáticas no admiten unidadNegocioId");
        }
        if (unitId == null) return null;
        UnidadNegocio unit = unidadRepository.findById(unitId)
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con ID: " + unitId));
        if (!unit.getEmpresa().getId().equals(site.getEmpresa().getId())) {
            throw new IllegalArgumentException("La unidad de negocio no pertenece a la empresa del sitio");
        }
        return unit;
    }

    private SeoPagina find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("SEO de página no encontrado con ID: " + id));
    }
}
