package com.isanorte.constructora_api.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CarritoClienteRequest(
        @NotNull @Size(max = 50) List<@Valid CarritoItemDto> items) {
}
