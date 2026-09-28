package com.isanorte.constructora_api.exception;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("Correo o contraseña incorrectos. Revisa los datos e inténtalo de nuevo.");
    }
}
