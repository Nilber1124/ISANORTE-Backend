package com.isanorte.constructora_api.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import com.isanorte.constructora_api.enums.DestinoAnuncio;
import com.isanorte.constructora_api.enums.UbicacionAnuncio;
import com.isanorte.constructora_api.model.Anuncio;
import com.isanorte.constructora_api.repository.AnuncioRepository;

@ExtendWith(MockitoExtension.class)
class DemoHomeIsadecorSeederTest {
    @Mock private AnuncioRepository repository;

    @Test
    void createsCompleteDemoContentWhenItDoesNotExist() throws Exception {
        when(repository.existsByEtiquetaAndDestinoAndUbicacion(
                anyString(), any(DestinoAnuncio.class), any(UbicacionAnuncio.class)))
                .thenReturn(false);

        new DemoHomeIsadecorSeeder(repository).run(mock(ApplicationArguments.class));

        ArgumentCaptor<Anuncio> captor = ArgumentCaptor.forClass(Anuncio.class);
        verify(repository, times(11)).save(captor.capture());
        assertThat(captor.getAllValues()).allMatch(Anuncio::getActivo);
        assertThat(captor.getAllValues()).allMatch(item -> item.getDestino() == DestinoAnuncio.ISADECOR);
        assertThat(captor.getAllValues()).extracting(Anuncio::getEtiqueta).doesNotHaveDuplicates();
        assertThat(captor.getAllValues())
                .filteredOn(item -> item.getUbicacion() == UbicacionAnuncio.CATEGORIA_SUPERIOR)
                .hasSize(4)
                .allMatch(item -> item.getImagenHoverUrl() != null);
    }

    @Test
    void remainsIdempotentWhenDemoContentAlreadyExists() throws Exception {
        when(repository.existsByEtiquetaAndDestinoAndUbicacion(
                anyString(), any(DestinoAnuncio.class), any(UbicacionAnuncio.class)))
                .thenReturn(true);

        new DemoHomeIsadecorSeeder(repository).run(mock(ApplicationArguments.class));

        verify(repository, never()).save(any());
    }
}
