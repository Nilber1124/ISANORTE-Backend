package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.isanorte.constructora_api.enums.EstadoCotizacion;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Cotizacion;
import com.isanorte.constructora_api.repository.CotizacionRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.service.ICotizacionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CotizacionService extends GenericService<Cotizacion, UUID> implements ICotizacionService {

    private final CotizacionRepository cotizacionRepository;

    @Override
    protected IGenericRepository<Cotizacion, UUID> getRepo() {
        return cotizacionRepository;
    }

    @Override
    public Cotizacion findByCodigo(String codigo) {
        return cotizacionRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ModelNotFoundException("Cotización no encontrada con código: " + codigo));
    }

    @Override
    public List<Cotizacion> findByEstado(EstadoCotizacion estado) {
        return cotizacionRepository.findByEstado(estado);
    }
}
