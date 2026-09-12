package com.isanorte.constructora_api.service.implementation;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.service.IConfiguracionSitioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConfiguracionSitioService extends GenericService<ConfiguracionSitio, UUID> implements IConfiguracionSitioService {

    private final ConfiguracionSitioRepository configuracionSitioRepository;

    @Override
    protected IGenericRepository<ConfiguracionSitio, UUID> getRepo() {
        return configuracionSitioRepository;
    }
}
