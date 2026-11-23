package com.isanorte.constructora_api.util;

import static org.junit.jupiter.api.Assertions.*;

import com.isanorte.constructora_api.model.IsadecorServicioPunto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ConvertersTest {

    private final ListaTextoConverter texto = new ListaTextoConverter();
    private final ServicioPuntosConverter puntos = new ServicioPuntosConverter();

    @Test
    void listaTextoIdaYVuelta() {
        String columna = texto.convertToDatabaseColumn(List.of("Cajamarca", "Obra con garantía"));
        assertEquals("Cajamarca\nObra con garantía", columna);
        assertEquals(List.of("Cajamarca", "Obra con garantía"), texto.convertToEntityAttribute(columna));
    }

    @Test
    void listaTextoVaciaONulaSeGuardaComoNull() {
        assertNull(texto.convertToDatabaseColumn(null));
        assertNull(texto.convertToDatabaseColumn(List.of()));
        assertNull(texto.convertToDatabaseColumn(Arrays.asList(" ", null)));
        assertTrue(texto.convertToEntityAttribute(null).isEmpty());
        assertTrue(texto.convertToEntityAttribute("").isEmpty());
    }

    @Test
    void listaTextoReemplazaSaltosDeLineaDentroDeUnElemento() {
        String columna = texto.convertToDatabaseColumn(List.of("uno\r\ndos"));
        assertEquals(List.of("uno dos"), texto.convertToEntityAttribute(columna));
    }

    @Test
    void listaTextoLeidaEsModificable() {
        List<String> leida = texto.convertToEntityAttribute("a\nb");
        assertDoesNotThrow(() -> leida.add("c"));
    }

    @Test
    void puntosIdaYVueltaConservaCampos() {
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 23, 12, 30, 15, 123_456_000);
        IsadecorServicioPunto p = IsadecorServicioPunto.builder().id(UUID.randomUUID())
                .texto("Precio \"especial\" \\ obra").orden(2).fechaCreacion(fecha).fechaActualizacion(fecha).build();

        List<IsadecorServicioPunto> leidos = puntos.convertToEntityAttribute(puntos.convertToDatabaseColumn(List.of(p)));

        assertEquals(1, leidos.size());
        assertEquals(p.getId(), leidos.get(0).getId());
        assertEquals(p.getTexto(), leidos.get(0).getTexto());
        assertEquals(2, leidos.get(0).getOrden());
        assertEquals(fecha, leidos.get(0).getFechaCreacion());
    }

    @Test
    void puntosLeeElFormatoQueGeneraLaMigracionV14() {
        String json = "[{\"id\":\"e25b9790-4fea-53d9-bba1-c8a5460f10ce\",\"texto\":\"Entregas a obra\",\"orden\":1,"
                + "\"fechaCreacion\":\"2026-11-19T14:34:59.993469\",\"fechaActualizacion\":\"2026-11-19T14:34:59.993469\"}]";

        List<IsadecorServicioPunto> leidos = puntos.convertToEntityAttribute(json);

        assertEquals("Entregas a obra", leidos.get(0).getTexto());
        assertEquals(LocalDateTime.of(2026, 11, 19, 14, 34, 59, 993_469_000), leidos.get(0).getFechaCreacion());
    }

    @Test
    void puntosVaciosONulos() {
        assertNull(puntos.convertToDatabaseColumn(null));
        assertNull(puntos.convertToDatabaseColumn(new ArrayList<>()));
        assertTrue(puntos.convertToEntityAttribute(null).isEmpty());
        assertTrue(puntos.convertToEntityAttribute(" ").isEmpty());
    }
}
