package com.isanorte.constructora_api.service.implementation;

import com.isanorte.constructora_api.dto.request.IsadecorServicioPuntoRequest;
import com.isanorte.constructora_api.dto.response.IsadecorServicioPuntoResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorServicioEmpresa;
import com.isanorte.constructora_api.model.IsadecorServicioPunto;
import com.isanorte.constructora_api.repository.IsadecorServicioEmpresaRepository;
import com.isanorte.constructora_api.service.IIsadecorServicioPuntoService;
import com.isanorte.constructora_api.util.BusinessTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Puntos del bloque de servicios de la Home de ISADECOR. Se guardan como lista dentro de cada
 * {@link IsadecorServicioEmpresa}; la API sigue tratándolos como recursos con su propio id.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IsadecorServicioPuntoService implements IIsadecorServicioPuntoService {

    static final Comparator<IsadecorServicioPunto> ORDEN =
            Comparator.comparing(IsadecorServicioPunto::getOrden).thenComparing(IsadecorServicioPunto::getId);

    private final IsadecorServicioEmpresaRepository isadecorServicioEmpresaRepository;

    @Override
    public List<IsadecorServicioPuntoResponse> findAll() {
        List<IsadecorServicioPuntoResponse> respuesta = new ArrayList<>();
        for (IsadecorServicioEmpresa servicio : isadecorServicioEmpresaRepository.findAll()) {
            for (IsadecorServicioPunto punto : servicio.getPuntos()) {
                respuesta.add(toResponse(servicio, punto));
            }
        }
        respuesta.sort(Comparator.comparing(IsadecorServicioPuntoResponse::orden)
                .thenComparing(IsadecorServicioPuntoResponse::id));
        return respuesta;
    }

    @Override
    public IsadecorServicioPuntoResponse findById(UUID id) {
        IsadecorServicioEmpresa servicio = findOwner(id);
        return toResponse(servicio, findIn(servicio, id));
    }

    @Override
    @Transactional
    public IsadecorServicioPuntoResponse create(IsadecorServicioPuntoRequest request) {
        IsadecorServicioEmpresa servicio = findServicio(request.servicioId());
        LocalDateTime now = BusinessTime.now();
        IsadecorServicioPunto punto = IsadecorServicioPunto.builder()
                .id(UUID.randomUUID())
                .texto(request.texto().trim())
                .orden(request.orden())
                .fechaCreacion(now)
                .fechaActualizacion(now)
                .build();

        List<IsadecorServicioPunto> puntos = new ArrayList<>(servicio.getPuntos());
        puntos.add(punto);
        guardar(servicio, puntos);
        return toResponse(servicio, punto);
    }

    @Override
    @Transactional
    public IsadecorServicioPuntoResponse update(UUID id, IsadecorServicioPuntoRequest request) {
        IsadecorServicioEmpresa actual = findOwner(id);
        IsadecorServicioEmpresa destino = actual.getId().equals(request.servicioId())
                ? actual
                : findServicio(request.servicioId());

        IsadecorServicioPunto punto = findIn(actual, id);
        punto.setTexto(request.texto().trim());
        punto.setOrden(request.orden());
        punto.setFechaActualizacion(BusinessTime.now());

        if (destino != actual) {
            guardar(actual, sin(actual, id));
            List<IsadecorServicioPunto> puntosDestino = new ArrayList<>(destino.getPuntos());
            puntosDestino.add(punto);
            guardar(destino, puntosDestino);
        } else {
            guardar(actual, new ArrayList<>(actual.getPuntos()));
        }
        return toResponse(destino, punto);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        IsadecorServicioEmpresa servicio = findOwner(id);
        guardar(servicio, sin(servicio, id));
    }

    private IsadecorServicioEmpresa findServicio(UUID servicioId) {
        return isadecorServicioEmpresaRepository.findById(servicioId)
                .orElseThrow(() -> new ModelNotFoundException("IsadecorServicioEmpresa no encontrado con ID: " + servicioId));
    }

    private IsadecorServicioEmpresa findOwner(UUID puntoId) {
        return isadecorServicioEmpresaRepository.findAll().stream()
                .filter(s -> s.getPuntos().stream().anyMatch(p -> puntoId.equals(p.getId())))
                .findFirst()
                .orElseThrow(() -> new ModelNotFoundException("Punto del bloque de servicios no encontrado con ID: " + puntoId));
    }

    private static IsadecorServicioPunto findIn(IsadecorServicioEmpresa servicio, UUID puntoId) {
        return servicio.getPuntos().stream()
                .filter(p -> puntoId.equals(p.getId()))
                .findFirst()
                .orElseThrow(() -> new ModelNotFoundException("Punto del bloque de servicios no encontrado con ID: " + puntoId));
    }

    private static List<IsadecorServicioPunto> sin(IsadecorServicioEmpresa servicio, UUID puntoId) {
        List<IsadecorServicioPunto> puntos = new ArrayList<>(servicio.getPuntos());
        puntos.removeIf(p -> puntoId.equals(p.getId()));
        return puntos;
    }

    /** Asigna una lista nueva (ordenada) para que Hibernate detecte el cambio en la columna JSON. */
    private void guardar(IsadecorServicioEmpresa servicio, List<IsadecorServicioPunto> puntos) {
        puntos.sort(ORDEN);
        servicio.setPuntos(puntos);
        isadecorServicioEmpresaRepository.saveAndFlush(servicio);
    }

    private static IsadecorServicioPuntoResponse toResponse(IsadecorServicioEmpresa servicio, IsadecorServicioPunto p) {
        return new IsadecorServicioPuntoResponse(p.getId(), servicio.getId(), p.getTexto(), p.getOrden(),
                p.getFechaCreacion(), p.getFechaActualizacion());
    }
}
