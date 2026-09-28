package com.isanorte.constructora_api.service.implementation;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.isanorte.constructora_api.config.AuthProperties;
import com.isanorte.constructora_api.dto.request.LoginRequest;
import com.isanorte.constructora_api.dto.response.LoginResponse;
import com.isanorte.constructora_api.dto.response.LoginResponse.UsuarioAutenticado;
import com.isanorte.constructora_api.exception.CredencialesInvalidasException;
import com.isanorte.constructora_api.repository.AdministradorRepository;
import com.isanorte.constructora_api.security.AdministradorUserDetailsService;
import com.isanorte.constructora_api.security.JwtService;
import com.isanorte.constructora_api.service.IAuthService;

@Service
public class AuthService implements IAuthService {
    private final AdministradorRepository repository;
    private final AdministradorUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthProperties properties;
    private final String dummyHash;

    public AuthService(AdministradorRepository repository, AdministradorUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder, JwtService jwtService, AuthProperties properties) {
        this.repository = repository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.properties = properties;
        this.dummyHash = passwordEncoder.encode(java.util.UUID.randomUUID().toString());
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        try {
            var user = userDetailsService.loadUserByUsername(email);
            if (!user.isEnabled() || !passwordEncoder.matches(request.password(), user.getPassword())
                    || user.getAuthorities().stream().noneMatch(a -> a.getAuthority()
                            .equals("ROLE_" + properties.adminRole().toUpperCase(Locale.ROOT)))) {
                throw new CredencialesInvalidasException();
            }
        } catch (UsernameNotFoundException exception) {
            passwordEncoder.matches(request.password(), dummyHash);
            throw new CredencialesInvalidasException();
        }

        var admin = repository.findByEmailIgnoreCase(email).orElseThrow(CredencialesInvalidasException::new);
        Set<String> roles = admin.getRoles().stream().filter(role -> Boolean.TRUE.equals(role.getActivo()))
                .map(role -> role.getNombre().toUpperCase(Locale.ROOT)).collect(Collectors.toUnmodifiableSet());
        admin.setUltimoAcceso(LocalDateTime.now());
        repository.save(admin);
        var issued = jwtService.emitir(admin, roles);
        return new LoginResponse(issued.valor(), "Bearer", issued.expiracion(),
                new UsuarioAutenticado(admin.getId(), admin.getNombre(), admin.getApellido(), admin.getEmail(), roles));
    }
}
