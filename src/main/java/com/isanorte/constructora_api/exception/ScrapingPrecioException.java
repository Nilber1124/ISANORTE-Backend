package com.isanorte.constructora_api.exception;

import com.isanorte.constructora_api.enums.EstadoComparacionPrecio;

// Safe public message only: never include URL parameters, remote HTML or transport exceptions.
public class ScrapingPrecioException extends RuntimeException {
    private final EstadoComparacionPrecio estado;

    public ScrapingPrecioException(EstadoComparacionPrecio estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public EstadoComparacionPrecio getEstado() { return estado; }
}

