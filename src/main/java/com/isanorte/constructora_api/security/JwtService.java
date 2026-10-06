package com.isanorte.constructora_api.security;

import java.time.Instant;
import java.util.Set;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import com.isanorte.constructora_api.config.JwtProperties;
import com.isanorte.constructora_api.model.Administrador;
import com.isanorte.constructora_api.model.Cliente;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
    public static final String ROL_CLIENTE = "CLIENTE";

    private final JwtEncoder encoder;
    private final JwtProperties properties;

    public TokenEmitido emitir(Administrador admin, Set<String> roles) {
        return emitir(admin.getEmail(), roles);
    }

    /**
     * El sujeto del token de cliente es su UUID; el rol {@code CLIENTE} solo habilita rutas de cuenta pública.
     */
    public TokenEmitido emitirCliente(Cliente cliente) {
        return emitir(cliente.getId().toString(), Set.of(ROL_CLIENTE));
    }

    private TokenEmitido emitir(String subject, Set<String> roles) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(properties.expiration());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("isanorte-api")
                .subject(subject)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("roles", roles)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenEmitido(token, expiresAt);
    }

    public record TokenEmitido(String valor, Instant expiracion) {
    }
}
