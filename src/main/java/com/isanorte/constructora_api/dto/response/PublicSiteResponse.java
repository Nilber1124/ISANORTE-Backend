package com.isanorte.constructora_api.dto.response;

import java.util.List;

public record PublicSiteResponse(
        String clave, String tituloSitio, String descripcionSitio, String logoUrl,
        String logoBlancoUrl, String faviconUrl, String textoPiePagina,
        EmpresaPublica empresa, List<RedPublica> redes, List<UnidadPublica> unidades) {

    public record EmpresaPublica(
            String nombreComercial, String direccion, String ciudad, String telefono,
            String telefonoSecundario, String email, String emailVentas, String whatsapp,
            String horarioAtencion, String resumenNosotros) {
    }

    public record RedPublica(String nombre, String url, String icono, Integer orden) {
    }

    public record UnidadPublica(String nombre, String slug, String descripcion, String icono,
            String imagenUrl, String imagenAlt, Integer orden) {
    }
}
