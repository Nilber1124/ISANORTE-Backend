package com.isanorte.constructora_api.service.implementation;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Administrador;
import com.isanorte.constructora_api.repository.AdministradorRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.service.IAdministradorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdministradorService extends GenericService<Administrador, UUID> implements IAdministradorService {

    private final AdministradorRepository administradorRepository;

    @Override
    protected IGenericRepository<Administrador, UUID> getRepo() {
        return administradorRepository;
    }

    @Override
    public Administrador findByEmail(String email) {
        return administradorRepository.findByEmail(email)
                .orElseThrow(() -> new ModelNotFoundException("Administrador no encontrado con email: " + email));
    }
}
