package com.isanorte.constructora_api.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.Publicacion;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.IsadecorPublicacionRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.IIsadecorComunidadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IsadecorComunidadService implements IIsadecorComunidadService {

    static final int MAX_PUBLICACIONES = 24;

    private final ConfiguracionSitioRepository configuracionRepository;
    private final UnidadNegocioRepository unidadRepository;
    private final IsadecorPublicacionRepository publicacionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Publicacion> findPublicaciones(String siteKey, String unitSlug) {
        var site = configuracionRepository.findByClave(siteKey)
                .orElseThrow(() -> new ModelNotFoundException("Sitio público no encontrado con clave: " + siteKey));
        var unit = unidadRepository.findByEmpresaIdAndSlugAndActivoTrue(site.getEmpresa().getId(), unitSlug)
                .orElseThrow(() -> new ModelNotFoundException("Unidad pública activa no encontrada con slug: " + unitSlug));
        return publicacionRepository
                .findByUnidadNegocioIdAndActivoTrueOrderByOrdenAscFechaCreacionDescIdAsc(unit.getId()).stream()
                .limit(MAX_PUBLICACIONES)
                .map(p -> new Publicacion(p.getRed(), p.getUrl(), p.getTitulo(), p.getMiniaturaUrl()))
                .toList();
    }
}
