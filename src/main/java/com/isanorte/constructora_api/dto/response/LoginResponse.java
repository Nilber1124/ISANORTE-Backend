package com.isanorte.constructora_api.dto.response;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record LoginResponse(String token, String tipo, Instant expiracion, UsuarioAutenticado usuario) {
    public record UsuarioAutenticado(UUID id, String nombre, String apellido, String email, Set<String> roles) {
    }
}
