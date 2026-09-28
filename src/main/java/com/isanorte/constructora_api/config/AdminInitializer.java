package com.isanorte.constructora_api.config;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.isanorte.constructora_api.model.Administrador;
import com.isanorte.constructora_api.model.Rol;
import com.isanorte.constructora_api.repository.AdministradorRepository;
import com.isanorte.constructora_api.repository.RolRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);
    private final AdministradorRepository administradorRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthProperties properties;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String email = value(properties.initialEmail()).toLowerCase(Locale.ROOT);
        String password = value(properties.initialPassword());
        if (email.isBlank() && password.isBlank()) {
            log.info("Administrador inicial no configurado; se omite su creación.");
            return;
        }
        if (email.isBlank() || password.isBlank()) {
            throw new IllegalStateException("ADMIN_EMAIL y ADMIN_PASSWORD deben configurarse juntos.");
        }
        if (administradorRepository.findByEmailIgnoreCase(email).isPresent()) {
            log.info("El administrador inicial ya existe; no se modificó su contraseña ni sus roles.");
            return;
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") || password.length() < 12
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException(
                    "ADMIN_EMAIL debe ser válido y ADMIN_PASSWORD debe tener entre 12 y 72 bytes.");
        }
        String roleName = value(properties.adminRole()).toUpperCase(Locale.ROOT);
        if (roleName.isBlank()) throw new IllegalStateException("app.auth.admin-role es obligatorio.");
        Rol role = rolRepository.findByNombreIgnoreCase(roleName).orElseGet(() -> rolRepository.save(
                Rol.builder().nombre(roleName).descripcion("Acceso al panel administrativo").activo(true).build()));
        if (!Boolean.TRUE.equals(role.getActivo())) {
            throw new IllegalStateException("El rol administrativo configurado está inactivo.");
        }
        Administrador admin = Administrador.builder()
                .nombre("Administrador")
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .activo(true)
                .build();
        admin.addRol(role);
        administradorRepository.save(admin);
        log.info("Administrador inicial creado correctamente.");
    }

    private static String value(String value) {
        return value == null ? "" : value.trim();
    }
}
