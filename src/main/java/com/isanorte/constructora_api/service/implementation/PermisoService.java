package com.isanorte.constructora_api.service.implementation;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Permiso;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.PermisoRepository;
import com.isanorte.constructora_api.service.IPermisoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermisoService extends GenericService<Permiso, UUID> implements IPermisoService {

    private final PermisoRepository permisoRepository;

    @Override
    protected IGenericRepository<Permiso, UUID> getRepo() {
        return permisoRepository;
    }

    @Override
    public Permiso findByCodigo(String codigo) {
        return permisoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ModelNotFoundException("Permiso no encontrado con código: " + codigo));
    }
}
