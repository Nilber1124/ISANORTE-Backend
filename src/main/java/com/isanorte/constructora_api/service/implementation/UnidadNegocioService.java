package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.IUnidadNegocioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnidadNegocioService extends GenericService<UnidadNegocio, UUID> implements IUnidadNegocioService {

    private final UnidadNegocioRepository unidadNegocioRepository;

    @Override
    protected IGenericRepository<UnidadNegocio, UUID> getRepo() {
        return unidadNegocioRepository;
    }

    @Override
    public UnidadNegocio findBySlug(String slug) {
        return unidadNegocioRepository.findBySlug(slug)
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con slug: " + slug));
    }

    @Override
    public List<UnidadNegocio> findByActivoTrueOrderByOrdenAsc() {
        return unidadNegocioRepository.findByActivoTrueOrderByOrdenAsc();
    }
}
