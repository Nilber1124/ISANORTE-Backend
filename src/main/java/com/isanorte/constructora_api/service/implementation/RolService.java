package com.isanorte.constructora_api.service.implementation;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Rol;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.RolRepository;
import com.isanorte.constructora_api.service.IRolService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolService extends GenericService<Rol, UUID> implements IRolService {

    private final RolRepository rolRepository;

    @Override
    protected IGenericRepository<Rol, UUID> getRepo() {
        return rolRepository;
    }

    @Override
    public Rol findByNombre(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseThrow(() -> new ModelNotFoundException("Rol no encontrado con nombre: " + nombre));
    }
}
