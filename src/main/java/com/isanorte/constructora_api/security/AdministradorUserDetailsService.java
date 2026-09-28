package com.isanorte.constructora_api.security;

import java.util.Locale;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.isanorte.constructora_api.repository.AdministradorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdministradorUserDetailsService implements UserDetailsService {
    private final AdministradorRepository repository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var admin = repository.findByEmailIgnoreCase(email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));
        String[] authorities = admin.getRoles().stream()
                .filter(role -> Boolean.TRUE.equals(role.getActivo()))
                .map(role -> "ROLE_" + role.getNombre().toUpperCase(Locale.ROOT))
                .toArray(String[]::new);
        return User.withUsername(admin.getEmail())
                .password(admin.getPasswordHash())
                .authorities(authorities)
                .disabled(!Boolean.TRUE.equals(admin.getActivo()))
                .build();
    }
}
