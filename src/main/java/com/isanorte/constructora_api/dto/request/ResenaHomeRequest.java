package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Marca una reseña para la Home de la tienda y define su orden. */
public record ResenaHomeRequest(@NotNull Boolean mostrarEnHome, @NotNull @Min(0) Integer ordenHome) {
}
