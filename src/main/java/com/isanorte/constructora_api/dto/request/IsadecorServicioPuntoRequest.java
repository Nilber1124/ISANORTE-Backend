package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record IsadecorServicioPuntoRequest(
        @NotNull UUID servicioId,
        @NotBlank @Size(max = 160) String texto,
        @NotNull @Min(0) Integer orden) {
}
