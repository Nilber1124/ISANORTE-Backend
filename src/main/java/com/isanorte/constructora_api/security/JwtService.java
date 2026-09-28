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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtEncoder encoder;
    private final JwtProperties properties;

    public TokenEmitido emitir(Administrador admin, Set<String> roles) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(properties.expiration());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("isanorte-api")
                .subject(admin.getEmail())
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
