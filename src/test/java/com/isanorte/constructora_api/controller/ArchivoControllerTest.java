package com.isanorte.constructora_api.controller;

import com.isanorte.constructora_api.service.CloudinaryService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ArchivoControllerTest {

    @Test
    void subirImagenConContentTypeImagenDevuelveUrlSegura() throws Exception {
        var cloudinaryService = mock(CloudinaryService.class);
        when(cloudinaryService.uploadFile(any(), eq("isanorte/uploads")))
                .thenReturn(Map.of("secure_url", "https://res.cloudinary.com/demo/image/upload/v1/foto.jpg",
                        "public_id", "foto", "format", "jpg"));
        var mvc = MockMvcBuilders.standaloneSetup(new ArchivoController(cloudinaryService)).build();

        var file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "contenido".getBytes());
        mvc.perform(multipart("/api/archivos/subir-imagen").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://res.cloudinary.com/demo/image/upload/v1/foto.jpg"))
                .andExpect(jsonPath("$.bytes").doesNotExist());

        verify(cloudinaryService).uploadFile(any(), eq("isanorte/uploads"));
    }

    @Test
    void subirImagenConContentTypeInvalidoRechazaSinLlamarCloudinary() throws Exception {
        var cloudinaryService = mock(CloudinaryService.class);
        var mvc = MockMvcBuilders.standaloneSetup(new ArchivoController(cloudinaryService)).build();

        var file = new MockMultipartFile("file", "ficha.pdf", "application/pdf", "contenido".getBytes());
        mvc.perform(multipart("/api/archivos/subir-imagen").file(file))
                .andExpect(status().isBadRequest());

        verify(cloudinaryService, never()).uploadFile(any(), any());
    }

    @Test
    void subirDocumentoConContentTypePdfDevuelveUrlSegura() throws Exception {
        var cloudinaryService = mock(CloudinaryService.class);
        when(cloudinaryService.uploadFile(any(), eq("isanorte/documentos")))
                .thenReturn(Map.of("secure_url", "https://res.cloudinary.com/demo/image/upload/v1/ficha.pdf",
                        "public_id", "ficha", "format", "pdf", "bytes", 245760));
        var mvc = MockMvcBuilders.standaloneSetup(new ArchivoController(cloudinaryService)).build();

        var file = new MockMultipartFile("file", "ficha.pdf", "application/pdf", "contenido".getBytes());
        mvc.perform(multipart("/api/archivos/subir-documento").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://res.cloudinary.com/demo/image/upload/v1/ficha.pdf"))
                .andExpect(jsonPath("$.format").value("pdf"))
                .andExpect(jsonPath("$.bytes").value(245760));

        verify(cloudinaryService).uploadFile(any(), eq("isanorte/documentos"));
    }

    @Test
    void subirDocumentoConContentTypeNoPdfRechazaSinLlamarCloudinary() throws Exception {
        var cloudinaryService = mock(CloudinaryService.class);
        var mvc = MockMvcBuilders.standaloneSetup(new ArchivoController(cloudinaryService)).build();

        var file = new MockMultipartFile("file", "foto.png", "image/png", "contenido".getBytes());
        mvc.perform(multipart("/api/archivos/subir-documento").file(file))
                .andExpect(status().isBadRequest());

        verify(cloudinaryService, never()).uploadFile(any(), any());
    }

    @Test
    void subirDocumentoUsaCarpetaPersonalizadaCuandoSeIndica() throws Exception {
        var cloudinaryService = mock(CloudinaryService.class);
        when(cloudinaryService.uploadFile(any(), eq("isanorte/recursos")))
                .thenReturn(Map.of("secure_url", "https://res.cloudinary.com/demo/image/upload/v1/catalogo.pdf",
                        "public_id", "catalogo", "format", "pdf"));
        var mvc = MockMvcBuilders.standaloneSetup(new ArchivoController(cloudinaryService)).build();

        var file = new MockMultipartFile("file", "catalogo.pdf", "application/pdf", "contenido".getBytes());
        mvc.perform(multipart("/api/archivos/subir-documento").file(file).param("folder", "isanorte/recursos"))
                .andExpect(status().isOk());

        verify(cloudinaryService).uploadFile(any(), eq("isanorte/recursos"));
    }

    @Test
    void subirDocumentoCuandoCloudinaryFallaDevuelveError500() throws Exception {
        var cloudinaryService = mock(CloudinaryService.class);
        when(cloudinaryService.uploadFile(any(), any())).thenThrow(new java.io.IOException("fallo de red"));
        var mvc = MockMvcBuilders.standaloneSetup(new ArchivoController(cloudinaryService)).build();

        var file = new MockMultipartFile("file", "ficha.pdf", "application/pdf", "contenido".getBytes());
        mvc.perform(multipart("/api/archivos/subir-documento").file(file))
                .andExpect(status().isInternalServerError());
    }
}
