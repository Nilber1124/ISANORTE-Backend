package com.isanorte.constructora_api.service.implementation;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Servicio;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.ServicioRepository;
import com.isanorte.constructora_api.service.IServicioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicioService extends GenericService<Servicio, UUID> implements IServicioService {

    private final ServicioRepository servicioRepository;

    @Override
    protected IGenericRepository<Servicio, UUID> getRepo() {
        return servicioRepository;
    }

    @Override
    public Servicio findBySlug(String slug) {
        return servicioRepository.findBySlug(slug)
                .orElseThrow(() -> new ModelNotFoundException("Servicio no encontrado con slug: " + slug));
    }
}
