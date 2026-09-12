package com.isanorte.constructora_api.exception;

public class ModelNotFoundException extends RuntimeException {

    public ModelNotFoundException(String message) {
        super(message);
    }

    public ModelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
