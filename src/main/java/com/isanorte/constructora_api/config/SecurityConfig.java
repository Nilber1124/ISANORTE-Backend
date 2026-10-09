package com.isanorte.constructora_api.config;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.isanorte.constructora_api.security.JwtService;

@Configuration
@EnableConfigurationProperties({ AuthProperties.class, JwtProperties.class })
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    JwtEncoder jwtEncoder(JwtProperties properties) {
        return NimbusJwtEncoder.withSecretKey(signingKey(properties)).build();
    }

    @Bean
    JwtDecoder jwtDecoder(JwtProperties properties) {
        return NimbusJwtDecoder.withSecretKey(signingKey(properties)).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        var authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("ROLE_");
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthProperties auth,
            JwtAuthenticationConverter converter,
            org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource) throws Exception {
        String adminAuthority = "ROLE_" + auth.adminRole().toUpperCase(java.util.Locale.ROOT);
        String clienteAuthority = "ROLE_" + JwtService.ROL_CLIENTE;
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/login", "/actuator/health", "/error").permitAll()
                        // Cuentas de cliente: registro y login abiertos; el resto exige sesión de cliente.
                        .requestMatchers("/api/publico/cuenta/registro", "/api/publico/cuenta/login").permitAll()
                        .requestMatchers("/api/publico/cuenta/**").hasAuthority(clienteAuthority)
                        // Cotizar y comparar precios requieren cuenta; el catálogo sigue siendo público.
                        .requestMatchers(HttpMethod.POST, "/api/publico/sitios/*/unidades/*/cotizaciones",
                                "/api/publico/sitios/*/unidades/*/productos/*/comparar-precio").hasAuthority(clienteAuthority)
                        .requestMatchers(HttpMethod.GET,
                                "/api/publico/sitios/*/unidades/*/productos/*/comparacion-competidores").hasAuthority(clienteAuthority)
                        .requestMatchers(HttpMethod.POST,
                                "/api/publico/sitios/*/unidades/*/productos/*/resenas").hasAuthority(clienteAuthority)
                        .requestMatchers("/api/publico/**").permitAll()
                        .requestMatchers("/api/**").hasAuthority(adminAuthority)
                        .anyRequest().permitAll())
                .oauth2ResourceServer(resource -> resource.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .build();
    }

    private static SecretKey signingKey(JwtProperties properties) {
        String secret = properties.secret() == null ? "" : properties.secret().trim();
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET debe contener al menos 32 bytes.");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }
}
