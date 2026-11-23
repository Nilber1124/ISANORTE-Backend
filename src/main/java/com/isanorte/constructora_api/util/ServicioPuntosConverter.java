package com.isanorte.constructora_api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.isanorte.constructora_api.model.IsadecorServicioPunto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.databind.json.JsonMapper;

/** Guarda los puntos del bloque de servicios de ISADECOR como un arreglo JSON en una sola columna. */
@Converter
public class ServicioPuntosConverter implements AttributeConverter<List<IsadecorServicioPunto>, String> {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    @Override
    public String convertToDatabaseColumn(List<IsadecorServicioPunto> puntos) {
        if (puntos == null || puntos.isEmpty()) {
            return null;
        }
        return JSON.writeValueAsString(puntos);
    }

    @Override
    public List<IsadecorServicioPunto> convertToEntityAttribute(String columna) {
        if (columna == null || columna.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(JSON.readValue(columna, IsadecorServicioPunto[].class)));
    }
}
