package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ConfiguracionSitioUpdateRequest(
    @Pattern(regexp = "[a-z0-9]++(?:-[a-z0-9]++)*+") @Size(max = 100) String clave,
    String tituloSitio,
    String descripcionSitio,
    String logoUrl,
    String logoBlancoUrl,
    String faviconUrl,
    @Size(max = 500) String videoProcesoUrl,
    @Size(max = 500) String videoProcesoMovilUrl,
    @Size(max = 500) String videoProcesoPosterUrl,
    String colorPrimario,
    String colorSecundario,
    String textoPiePagina) {
}
