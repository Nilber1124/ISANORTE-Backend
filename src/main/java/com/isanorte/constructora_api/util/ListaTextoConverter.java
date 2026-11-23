package com.isanorte.constructora_api.util;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Guarda una lista corta de textos en una sola columna, un elemento por línea. Sirve para listas
 * pequeñas sin identidad propia (por ejemplo las etiquetas de una página) y evita una tabla extra.
 */
@Converter
public class ListaTextoConverter implements AttributeConverter<List<String>, String> {

    private static final String SEPARADOR = "\n";

    @Override
    public String convertToDatabaseColumn(List<String> valores) {
        if (valores == null || valores.isEmpty()) {
            return null;
        }
        List<String> limpios = new ArrayList<>(valores.size());
        for (String valor : valores) {
            if (valor != null && !valor.isBlank()) {
                limpios.add(valor.replaceAll("[\\r\\n]+", " ").trim());
            }
        }
        return limpios.isEmpty() ? null : String.join(SEPARADOR, limpios);
    }

    @Override
    public List<String> convertToEntityAttribute(String columna) {
        List<String> valores = new ArrayList<>();
        if (columna == null || columna.isEmpty()) {
            return valores;
        }
        for (String valor : columna.split(SEPARADOR)) {
            if (!valor.isBlank()) {
                valores.add(valor);
            }
        }
        return valores;
    }
}
