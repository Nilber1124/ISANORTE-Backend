package com.isanorte.constructora_api.dto.request;

import java.util.List;

import com.isanorte.constructora_api.enums.CanalCotizacion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PublicCotizacionRequest(
    @NotBlank String nombreCliente,
    @NotBlank @Email String emailCliente,
    @NotBlank String telefonoCliente,
    String empresaCliente,
    String ciudad,
    String mensaje,
    CanalCotizacion canal,
    String productoSlug,
    String varianteSku,
    @Positive Integer cantidad,
    String notas,
    List<@Valid DetallePublicoRequest> detalles) {

    public record DetallePublicoRequest(
        @NotBlank String productoSlug,
        String varianteSku,
        @Positive Integer cantidad,
        String notas) {
    }
}
