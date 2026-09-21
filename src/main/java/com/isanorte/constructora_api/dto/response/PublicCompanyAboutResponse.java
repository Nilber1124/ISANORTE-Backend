package com.isanorte.constructora_api.dto.response;

import java.util.List;

/** Datos corporativos necesarios exclusivamente por la página pública Nosotros. */
public record PublicCompanyAboutResponse(
        String mision, String vision, String valores,
        List<PublicCompanyStatisticResponse> estadisticas) {
}
