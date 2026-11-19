package com.isanorte.constructora_api.service.implementation;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ResenaHomeRequest;
import com.isanorte.constructora_api.dto.response.ResenaAdminResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.ResenaProducto;
import com.isanorte.constructora_api.repository.ResenaProductoRepository;
import com.isanorte.constructora_api.service.IResenaAdminService;

import lombok.RequiredArgsConstructor;

/** Administración de las reseñas que se muestran en la Home de la tienda. */
@Service
@RequiredArgsConstructor
public class ResenaAdminService implements IResenaAdminService {

    private final ResenaProductoRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ResenaAdminResponse> findAll() {
        return repository.findAll().stream()
                .sorted(Comparator
                        .comparing((ResenaProducto r) -> !Boolean.TRUE.equals(r.getMostrarEnHome()))
                        .thenComparing(ResenaProducto::getOrdenHome, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(ResenaProducto::getFechaCreacion, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ResenaAdminResponse updateHome(UUID id, ResenaHomeRequest request) {
        ResenaProducto review = repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Reseña no encontrada con ID: " + id));
        review.setMostrarEnHome(request.mostrarEnHome());
        review.setOrdenHome(request.ordenHome());
        return toResponse(repository.save(review));
    }

    private ResenaAdminResponse toResponse(ResenaProducto r) {
        return new ResenaAdminResponse(r.getId(), r.getNombreCliente(), r.getCalificacion(), r.getComentario(),
                r.getCompraVerificada(), r.getProducto().getId(), r.getProducto().getNombre(), r.getFechaCreacion(),
                r.getMostrarEnHome(), r.getOrdenHome());
    }
}
