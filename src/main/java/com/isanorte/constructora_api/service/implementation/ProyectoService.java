package com.isanorte.constructora_api.service.implementation;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Proyecto;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.ProyectoRepository;
import com.isanorte.constructora_api.service.IProyectoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProyectoService extends GenericService<Proyecto, UUID> implements IProyectoService {

    private final ProyectoRepository proyectoRepository;

    @Override
    protected IGenericRepository<Proyecto, UUID> getRepo() {
        return proyectoRepository;
    }

    @Override
    public Proyecto findBySlug(String slug) {
        return proyectoRepository.findBySlug(slug)
                .orElseThrow(() -> new ModelNotFoundException("Proyecto no encontrado con slug: " + slug));
    }
}
