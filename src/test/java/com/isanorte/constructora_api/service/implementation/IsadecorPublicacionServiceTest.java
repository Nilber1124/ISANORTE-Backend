package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.isanorte.constructora_api.dto.request.IsadecorPublicacionRequest;
import com.isanorte.constructora_api.enums.RedPublicacion;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorPublicacion;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.IsadecorPublicacionRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.CloudinaryService;
import com.isanorte.constructora_api.service.IPublicacionMetadataProvider;
import com.isanorte.constructora_api.service.IPublicacionMetadataProvider.Metadata;

@ExtendWith(MockitoExtension.class)
class IsadecorPublicacionServiceTest {

    @Mock private IsadecorPublicacionRepository repository;
    @Mock private UnidadNegocioRepository unidadRepository;
    @Mock private IPublicacionMetadataProvider metadataProvider;
    @Mock private CloudinaryService cloudinaryService;
    @InjectMocks private IsadecorPublicacionService service;

    private final UUID unitId = UUID.randomUUID();
    private final UnidadNegocio unit = UnidadNegocio.builder().id(unitId).build();

    @BeforeEach
    void setUp() {
        org.mockito.Mockito.lenient().when(unidadRepository.findById(unitId)).thenReturn(Optional.of(unit));
        org.mockito.Mockito.lenient().when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        org.mockito.Mockito.lenient().when(repository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
    }

    private IsadecorPublicacionRequest request(RedPublicacion red, String url, String titulo, String miniatura) {
        return new IsadecorPublicacionRequest(unitId, red, url, titulo, miniatura, 0, true);
    }

    @Test
    void createCompletaTituloYMiniaturaDesdeLaRedYLaCopiaACloudinary() throws IOException {
        String url = "https://www.instagram.com/reel/Abc123/";
        when(metadataProvider.fetch(RedPublicacion.INSTAGRAM, url))
                .thenReturn(Optional.of(new Metadata("Instalación de panel", "https://cdn.instagram/x.jpg")));
        when(cloudinaryService.uploadFromUrl("https://cdn.instagram/x.jpg", "isadecor/comunidad"))
                .thenReturn(Map.of("secure_url", "https://res.cloudinary.com/x.webp"));

        var response = service.create(request(RedPublicacion.INSTAGRAM, url + "?igsh=tracking", null, null));

        assertEquals(url, response.url());
        assertEquals("Instalación de panel", response.titulo());
        assertEquals("https://res.cloudinary.com/x.webp", response.miniaturaUrl());
    }

    @Test
    void createGuardaLaPublicacionAunqueLaRedNoResponda() {
        when(metadataProvider.fetch(any(), any())).thenReturn(Optional.empty());

        var response = service.create(request(RedPublicacion.INSTAGRAM, "https://www.instagram.com/p/Abc123/", null, null));

        assertNull(response.miniaturaUrl());
        assertNull(response.titulo());
    }

    @Test
    void createRespetaLoQueEscribeElAdministradorSinConsultarLaRed() {
        var response = service.create(request(RedPublicacion.INSTAGRAM, "https://www.instagram.com/p/Abc123/",
                "Mi título", "https://res.cloudinary.com/mia.webp"));

        assertEquals("Mi título", response.titulo());
        assertEquals("https://res.cloudinary.com/mia.webp", response.miniaturaUrl());
        verify(metadataProvider, never()).fetch(any(), any());
    }

    @Test
    void createRechazaEnlacesQueNoSonDeLaRed() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(request(RedPublicacion.INSTAGRAM, "https://evil.com/p/Abc123/", null, null)));
        assertThrows(IllegalArgumentException.class,
                () -> service.create(request(RedPublicacion.INSTAGRAM, "https://www.instagram.com/isadecor/", null, null)));
        assertThrows(IllegalArgumentException.class,
                () -> service.create(request(RedPublicacion.TIKTOK, "https://www.instagram.com/p/Abc123/", null, null)));
    }

    @Test
    void createRechazaUnaPublicacionRepetida() {
        when(repository.existsByUnidadNegocioIdAndUrl(unitId, "https://www.instagram.com/p/Abc123/")).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> service.create(request(RedPublicacion.INSTAGRAM, "https://www.instagram.com/p/Abc123", null, null)));
    }

    @Test
    void createAceptaEnlacesDeTikTok() {
        when(metadataProvider.fetch(any(), any())).thenReturn(Optional.empty());

        var response = service.create(request(RedPublicacion.TIKTOK, "https://www.tiktok.com/@isadecor/video/123", null, null));

        assertEquals("https://www.tiktok.com/@isadecor/video/123", response.url());
    }

    @Test
    void updateConNuevoEnlaceBorraLaMiniaturaAnteriorYLaVuelveAObtener() throws IOException {
        var entity = IsadecorPublicacion.builder().id(UUID.randomUUID()).unidadNegocio(unit).red(RedPublicacion.INSTAGRAM)
                .url("https://www.instagram.com/p/Vieja/").titulo("Vieja").miniaturaUrl("https://res.cloudinary.com/vieja.webp").build();
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        String nueva = "https://www.instagram.com/p/Nueva/";
        when(metadataProvider.fetch(RedPublicacion.INSTAGRAM, nueva))
                .thenReturn(Optional.of(new Metadata("Nueva", "https://cdn/n.jpg")));
        when(cloudinaryService.uploadFromUrl("https://cdn/n.jpg", "isadecor/comunidad"))
                .thenReturn(Map.of("secure_url", "https://res.cloudinary.com/nueva.webp"));

        var response = service.update(entity.getId(),
                request(RedPublicacion.INSTAGRAM, nueva, "Vieja", "https://res.cloudinary.com/vieja.webp"));

        assertEquals("Nueva", response.titulo());
        assertEquals("https://res.cloudinary.com/nueva.webp", response.miniaturaUrl());
    }

    @Test
    void updateSinCambiarElEnlaceConservaLaMiniatura() {
        var entity = IsadecorPublicacion.builder().id(UUID.randomUUID()).unidadNegocio(unit).red(RedPublicacion.INSTAGRAM)
                .url("https://www.instagram.com/p/Abc123/").titulo("T").miniaturaUrl("https://res.cloudinary.com/a.webp").build();
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

        var response = service.update(entity.getId(),
                request(RedPublicacion.INSTAGRAM, "https://www.instagram.com/p/Abc123/", "T", null));

        assertEquals("https://res.cloudinary.com/a.webp", response.miniaturaUrl());
        verify(metadataProvider, never()).fetch(any(), any());
    }

    @Test
    void findByIdInexistenteLanzaNoEncontrado() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> service.findById(id));
    }

    @Test
    void unidadInexistenteLanzaNoEncontrado() {
        UUID otra = UUID.randomUUID();
        when(unidadRepository.findById(otra)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> service.create(
                new IsadecorPublicacionRequest(otra, RedPublicacion.INSTAGRAM, "https://www.instagram.com/p/A1/", null, null, 0, true)));
    }
}
