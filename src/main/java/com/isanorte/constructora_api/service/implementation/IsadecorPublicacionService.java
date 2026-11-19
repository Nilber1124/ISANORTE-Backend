package com.isanorte.constructora_api.service.implementation;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorPublicacionRequest;
import com.isanorte.constructora_api.dto.response.IsadecorPublicacionResponse;
import com.isanorte.constructora_api.enums.RedPublicacion;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorPublicacion;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.IsadecorPublicacionRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.CloudinaryService;
import com.isanorte.constructora_api.service.IIsadecorPublicacionService;
import com.isanorte.constructora_api.service.IPublicacionMetadataProvider;

import lombok.RequiredArgsConstructor;

/** Administración de los videos de redes sociales de la tienda (carga por enlace). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IsadecorPublicacionService implements IIsadecorPublicacionService {

    private static final Logger log = LoggerFactory.getLogger(IsadecorPublicacionService.class);
    private static final String FOLDER = "isadecor/comunidad";
    private static final Pattern INSTAGRAM = Pattern.compile(
            "^https://(www\\.)?instagram\\.com/(p|reel|reels|tv)/[A-Za-z0-9_-]+/?(\\?.*)?$");
    private static final Pattern TIKTOK = Pattern.compile(
            "^https://(www\\.|vm\\.|vt\\.)?tiktok\\.com/.+$");

    private final IsadecorPublicacionRepository repository;
    private final UnidadNegocioRepository unidadRepository;
    private final IPublicacionMetadataProvider metadataProvider;
    private final CloudinaryService cloudinaryService;

    @Override
    public List<IsadecorPublicacionResponse> findAll() {
        return repository.findAllByOrderByOrdenAscFechaCreacionDescIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public IsadecorPublicacionResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public IsadecorPublicacionResponse create(IsadecorPublicacionRequest request) {
        UnidadNegocio unidad = findUnidad(request.unidadNegocioId());
        String url = normalizeUrl(request.red(), request.url());
        if (repository.existsByUnidadNegocioIdAndUrl(unidad.getId(), url)) {
            throw new IllegalStateException("Esta publicación ya está registrada");
        }
        IsadecorPublicacion entity = new IsadecorPublicacion();
        apply(request, entity, unidad, url, request.titulo(), request.miniaturaUrl());
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public IsadecorPublicacionResponse update(UUID id, IsadecorPublicacionRequest request) {
        IsadecorPublicacion entity = findEntity(id);
        UnidadNegocio unidad = findUnidad(request.unidadNegocioId());
        String url = normalizeUrl(request.red(), request.url());
        if (!url.equals(entity.getUrl()) && repository.existsByUnidadNegocioIdAndUrl(unidad.getId(), url)) {
            throw new IllegalStateException("Esta publicación ya está registrada");
        }
        // Si cambia el enlace, el título y la miniatura anteriores ya no corresponden y se vuelven a obtener.
        boolean urlChanged = !url.equals(entity.getUrl());
        String titulo = urlChanged && same(request.titulo(), entity.getTitulo()) ? null : request.titulo();
        String miniatura = urlChanged && same(request.miniaturaUrl(), entity.getMiniaturaUrl())
                ? null : request.miniaturaUrl();
        if (urlChanged) {
            entity.setTitulo(null);
            entity.setMiniaturaUrl(null);
        }
        apply(request, entity, unidad, url, titulo, miniatura);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public IsadecorPublicacionResponse updateActivo(UUID id, ActivoRequest request) {
        IsadecorPublicacion entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private IsadecorPublicacion findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Publicación no encontrada con ID: " + id));
    }

    private UnidadNegocio findUnidad(UUID id) {
        return unidadRepository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con ID: " + id));
    }

    /** Valida que el enlace sea de la red indicada y lo deja sin parámetros de seguimiento. */
    String normalizeUrl(RedPublicacion red, String raw) {
        String url = raw.trim();
        boolean valid = red == RedPublicacion.INSTAGRAM ? INSTAGRAM.matcher(url).matches() : TIKTOK.matcher(url).matches();
        if (!valid) {
            throw new IllegalArgumentException(red == RedPublicacion.INSTAGRAM
                    ? "El enlace debe ser de una publicación o reel de Instagram (instagram.com/p/… o /reel/…)"
                    : "El enlace debe ser de un video de TikTok");
        }
        if (red == RedPublicacion.INSTAGRAM) {
            int query = url.indexOf('?');
            url = query >= 0 ? url.substring(0, query) : url;
            url = url.endsWith("/") ? url : url + "/";
        }
        return url;
    }

    private void apply(IsadecorPublicacionRequest request, IsadecorPublicacion entity, UnidadNegocio unidad, String url,
            String titulo, String miniatura) {
        entity.setUnidadNegocio(unidad);
        entity.setRed(request.red());
        entity.setUrl(url);
        entity.setTitulo(blank(titulo) ? entity.getTitulo() : titulo.trim());
        entity.setMiniaturaUrl(blank(miniatura) ? entity.getMiniaturaUrl() : miniatura.trim());
        entity.setOrden(request.orden());
        entity.setActivo(request.activo());
        if (blank(entity.getMiniaturaUrl()) || blank(entity.getTitulo())) {
            completeFromNetwork(entity);
        }
    }

    /** Completa lo que falte con el oEmbed de la red; si no responde, la publicación se guarda igual. */
    private void completeFromNetwork(IsadecorPublicacion entity) {
        metadataProvider.fetch(entity.getRed(), entity.getUrl()).ifPresent(metadata -> {
            if (blank(entity.getTitulo()) && !blank(metadata.titulo())) {
                String title = metadata.titulo();
                entity.setTitulo(title.length() > 200 ? title.substring(0, 200) : title);
            }
            if (blank(entity.getMiniaturaUrl()) && !blank(metadata.miniaturaUrl())) {
                entity.setMiniaturaUrl(rehost(metadata.miniaturaUrl()));
            }
        });
    }

    /** Las miniaturas de las redes caducan: se copian a Cloudinary para que no se rompan. */
    private String rehost(String sourceUrl) {
        try {
            Object secure = cloudinaryService.uploadFromUrl(sourceUrl, FOLDER).get("secure_url");
            return secure == null ? null : secure.toString();
        } catch (IOException | RuntimeException ex) {
            log.warn("No se pudo copiar la miniatura a Cloudinary: {}", ex.getMessage());
            return null;
        }
    }

    private boolean same(String requested, String current) {
        return blank(requested) || requested.trim().equals(current);
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private IsadecorPublicacionResponse toResponse(IsadecorPublicacion e) {
        return new IsadecorPublicacionResponse(e.getId(), e.getUnidadNegocio().getId(), e.getRed(), e.getUrl(),
                e.getTitulo(), e.getMiniaturaUrl(), e.getOrigen(), e.getOrden(), e.getActivo(),
                e.getFechaCreacion(), e.getFechaActualizacion());
    }
}
