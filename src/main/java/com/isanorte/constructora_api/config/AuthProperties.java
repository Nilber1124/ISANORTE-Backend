package com.isanorte.constructora_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auth")
public record AuthProperties(String adminRole, String initialEmail, String initialPassword) {
}
