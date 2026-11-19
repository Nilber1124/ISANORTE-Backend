package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.isanorte.constructora_api.enums.RedPublicacion;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.IsadecorPublicacion;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.IsadecorPublicacionRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

@ExtendWith(MockitoExtension.class)
class IsadecorComunidadServiceTest {

    @Mock private ConfiguracionSitioRepository configuracionRepository;
    @Mock private UnidadNegocioRepository unidadRepository;
    @Mock private IsadecorPublicacionRepository publicacionRepository;
    @InjectMocks private IsadecorComunidadService service;

    @Test
    void devuelveLasPublicacionesActivasDeLaUnidad() {
        var empresa = Empresa.builder().id(UUID.randomUUID()).build();
        var unit = UnidadNegocio.builder().id(UUID.randomUUID()).build();
        when(configuracionRepository.findByClave("isanorte"))
                .thenReturn(Optional.of(ConfiguracionSitio.builder().empresa(empresa).build()));
        when(unidadRepository.findByEmpresaIdAndSlugAndActivoTrue(empresa.getId(), "isadecor")).thenReturn(Optional.of(unit));
        when(publicacionRepository.findByUnidadNegocioIdAndActivoTrueOrderByOrdenAscFechaCreacionDescIdAsc(unit.getId()))
                .thenReturn(List.of(IsadecorPublicacion.builder().red(RedPublicacion.INSTAGRAM)
                        .url("https://www.instagram.com/p/A1/").titulo("T").miniaturaUrl("https://x/y.webp").build()));

        var result = service.findPublicaciones("isanorte", "isadecor");

        assertEquals(1, result.size());
        assertEquals("https://www.instagram.com/p/A1/", result.get(0).url());
    }

    @Test
    void sitioInexistenteLanzaNoEncontrado() {
        when(configuracionRepository.findByClave("x")).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> service.findPublicaciones("x", "isadecor"));
    }
}
