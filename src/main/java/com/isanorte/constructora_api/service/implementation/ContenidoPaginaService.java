package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ContenidoPaginaRequest;
import com.isanorte.constructora_api.dto.request.ContenidoPaginaUpdateRequest;
import com.isanorte.constructora_api.dto.response.ContenidoPaginaResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.DynamicContentMapper;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.ContenidoPagina;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.ContenidoPaginaRepository;
import com.isanorte.constructora_api.service.IContenidoPaginaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContenidoPaginaService implements IContenidoPaginaService {
    private final ContenidoPaginaRepository repository;
    private final ConfiguracionSitioRepository configuracionRepository;
    private final DynamicContentMapper mapper;

    @Override @Transactional(readOnly = true)
    public List<ContenidoPaginaResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public ContenidoPaginaResponse findById(UUID id) {
        return mapper.toResponse(find(id));
    }

    @Override @Transactional
    public ContenidoPaginaResponse create(ContenidoPaginaRequest request) {
        ConfiguracionSitio site = configuracionRepository.findById(request.configuracionSitioId())
                .orElseThrow(() -> new ModelNotFoundException("Configuración no encontrada con ID: " + request.configuracionSitioId()));
        if (repository.existsByConfiguracionSitioIdAndPagina(site.getId(), request.pagina())) {
            throw new IllegalStateException("Ya existe contenido para la página indicada en este sitio");
        }
        ContenidoPagina entity = mapper.toEntity(request);
        entity.setConfiguracionSitio(site);
        return mapper.toResponse(repository.saveAndFlush(entity));
    }

    @Override @Transactional
    public ContenidoPaginaResponse update(UUID id, ContenidoPaginaUpdateRequest request) {
        ContenidoPagina entity = find(id);
        mapper.update(request, entity);
        entity.getTags().clear();
        if (request.tags() != null) {
            entity.getTags().addAll(request.tags());
        }
        return mapper.toResponse(repository.saveAndFlush(entity));
    }

    private ContenidoPagina find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Contenido de página no encontrado con ID: " + id));
    }
}
