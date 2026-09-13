package com.isanorte.constructora_api.dto.request;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.enums.CanalCotizacion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CotizacionRequest(
    @NotBlank String nombreCliente,
    @NotBlank @Email String emailCliente,
    @NotBlank String telefonoCliente,
    String empresaCliente,
    String ciudad,
    String mensaje,
    @NotNull CanalCotizacion canal,
    @NotEmpty List<@Valid DetalleRequest> detalles) {

    public record DetalleRequest(
        @NotNull UUID productoId,
        UUID varianteId,
        @NotNull @Positive Integer cantidad,
        String notas) {
    }
}
