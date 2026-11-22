package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.isanorte.constructora_api.enums.RedPublicacion;
import com.isanorte.constructora_api.model.IsadecorPublicacion;

class IsadecorHomeComunidadTest {

    private static IsadecorPublicacion video(RedPublicacion red, String url) {
        return IsadecorPublicacion.builder().red(red).url(url).build();
    }

    @Test
    void elHomeIncluyeElVideoMasRecienteDeCadaRedAunqueUnaRedOcupeLosPrimerosLugares() {
        var ig1 = video(RedPublicacion.INSTAGRAM, "ig1");
        var ig2 = video(RedPublicacion.INSTAGRAM, "ig2");
        var ig3 = video(RedPublicacion.INSTAGRAM, "ig3");
        var ig4 = video(RedPublicacion.INSTAGRAM, "ig4");
        var tt = video(RedPublicacion.TIKTOK, "tt");
        var fb = video(RedPublicacion.FACEBOOK, "fb");

        var result = IsadecorHomeService.seleccionComunidad(List.of(ig1, ig2, ig3, ig4, tt, fb));

        assertEquals(List.of(ig1, ig2, tt, fb), result);
    }

    @Test
    void sinMasRedesSeConservanLosPrimerosCuatroEnOrden() {
        var lista = List.of(video(RedPublicacion.INSTAGRAM, "a"), video(RedPublicacion.INSTAGRAM, "b"),
                video(RedPublicacion.INSTAGRAM, "c"), video(RedPublicacion.INSTAGRAM, "d"),
                video(RedPublicacion.INSTAGRAM, "e"));

        assertEquals(lista.subList(0, 4), IsadecorHomeService.seleccionComunidad(lista));
    }
}
