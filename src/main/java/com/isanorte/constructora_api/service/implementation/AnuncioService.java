package com.isanorte.constructora_api.service.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.AnuncioRequest;
import com.isanorte.constructora_api.dto.response.AnuncioResponse;
import com.isanorte.constructora_api.enums.DestinoAnuncio;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Anuncio;
import com.isanorte.constructora_api.repository.AnuncioRepository;
import com.isanorte.constructora_api.service.IAnuncioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnuncioService implements IAnuncioService {

    private final AnuncioRepository anuncioRepository;

    @Override
    public List<AnuncioResponse> findAll() {
        return anuncioRepository.findAllByOrderByOrdenAscFechaCreacionDescIdAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AnuncioResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public AnuncioResponse create(AnuncioRequest request) {
        Anuncio anuncio = new Anuncio();
        apply(request, anuncio);
        return toResponse(anuncioRepository.save(anuncio));
    }

    @Override
    @Transactional
    public AnuncioResponse update(UUID id, AnuncioRequest request) {
        Anuncio anuncio = findEntity(id);
        apply(request, anuncio);
        return toResponse(anuncioRepository.saveAndFlush(anuncio));
    }

    @Override
    @Transactional
    public AnuncioResponse updateActivo(UUID id, ActivoRequest request) {
        Anuncio anuncio = findEntity(id);
        anuncio.setActivo(request.activo());
        return toResponse(anuncioRepository.saveAndFlush(anuncio));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        anuncioRepository.delete(findEntity(id));
    }

    @Override
    public List<AnuncioResponse> findPublicos(DestinoAnuncio destino) {
        if (destino == DestinoAnuncio.AMBOS) {
            throw new IllegalArgumentException("El destino público debe ser ISANORTE o ISADECOR");
        }
        return anuncioRepository.findPublicosVigentes(destino, LocalDateTime.now()).stream()
                .map(this::toResponse)
                .toList();
    }

    private Anuncio findEntity(UUID id) {
        return anuncioRepository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Anuncio no encontrado con ID: " + id));
    }

    private void apply(AnuncioRequest request, Anuncio anuncio) {
        anuncio.setTitulo(request.titulo().trim());
        anuncio.setDescripcionResumida(request.descripcionResumida().trim());
        anuncio.setContenidoDetallado(request.contenidoDetallado().trim());
        anuncio.setCondiciones(trimToNull(request.condiciones()));
        anuncio.setImagenUrl(request.imagenUrl().trim());
        anuncio.setEtiqueta(request.etiqueta().trim());
        anuncio.setDestino(request.destino());
        anuncio.setTipoAccion(request.tipoAccion());
        anuncio.setDestinoAccion(request.destinoAccion().trim());
        anuncio.setTextoBoton(request.textoBoton().trim());
        anuncio.setFechaInicio(request.fechaInicio());
        anuncio.setFechaFin(request.fechaFin());
        anuncio.setActivo(request.activo());
        anuncio.setOrden(request.orden());
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private AnuncioResponse toResponse(Anuncio anuncio) {
        return new AnuncioResponse(
                anuncio.getId(), anuncio.getTitulo(), anuncio.getDescripcionResumida(),
                anuncio.getContenidoDetallado(), anuncio.getCondiciones(), anuncio.getImagenUrl(),
                anuncio.getEtiqueta(), anuncio.getDestino(), anuncio.getTipoAccion(),
                anuncio.getDestinoAccion(), anuncio.getTextoBoton(), anuncio.getFechaInicio(),
                anuncio.getFechaFin(), anuncio.getActivo(), anuncio.getOrden(),
                anuncio.getFechaCreacion(), anuncio.getFechaActualizacion());
    }
}

