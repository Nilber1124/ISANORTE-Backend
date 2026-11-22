package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.TextoBloqueRequest;
import com.isanorte.constructora_api.dto.response.TextoBloqueResponse;
import com.isanorte.constructora_api.model.TextoBloque;
import com.isanorte.constructora_api.repository.TextoBloqueRepository;

import lombok.RequiredArgsConstructor;

/** Textos de cabecera de los bloques de la Home, editables desde el admin. */
@Service
@RequiredArgsConstructor
public class TextoBloqueService {

    /** Bloques de la Home cuyo encabezado se puede editar. */
    public static final Set<String> CLAVES = Set.of("garantias", "testimonios", "preguntas", "proceso");

    private final TextoBloqueRepository repository;

    @Transactional(readOnly = true)
    public List<TextoBloqueResponse> findAll() {
        return repository.findAllByOrderByClaveAsc().stream().map(TextoBloqueService::toResponse).toList();
    }

    /** Crea o actualiza el texto del bloque. Un campo en blanco se guarda como nulo (vuelve al texto por defecto). */
    @Transactional
    public TextoBloqueResponse upsert(String clave, TextoBloqueRequest request) {
        if (!CLAVES.contains(clave)) {
            throw new IllegalArgumentException("Bloque desconocido: " + clave);
        }
        TextoBloque entity = repository.findByClave(clave)
                .orElseGet(() -> TextoBloque.builder().clave(clave).build());
        entity.setEtiqueta(blankToNull(request.etiqueta()));
        entity.setTitulo(blankToNull(request.titulo()));
        entity.setSubtitulo(blankToNull(request.subtitulo()));
        return toResponse(repository.saveAndFlush(entity));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }

    private static TextoBloqueResponse toResponse(TextoBloque entity) {
        return new TextoBloqueResponse(entity.getId(), entity.getClave(), entity.getEtiqueta(),
                entity.getTitulo(), entity.getSubtitulo());
    }
}
