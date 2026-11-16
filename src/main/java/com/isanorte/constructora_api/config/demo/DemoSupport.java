package com.isanorte.constructora_api.config.demo;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.isanorte.constructora_api.util.BusinessTime;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

/**
 * Utilidades compartidas por los seeders de demostración.
 *
 * <p>Los identificadores de las filas sembradas son deterministas ({@link #id(String, String)}):
 * así la carga es idempotente y la limpieza puede recalcular exactamente qué filas son suyas.
 */
@Component
@RequiredArgsConstructor
public class DemoSupport {
    /** Dominio de los correos de las cuentas de demostración. */
    public static final String EMAIL_DOMAIN = "@demo.isanorte.ec";

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    @Value("${app.seed.demo-client-password:}")
    private String clientPassword;

    private DemoManifest manifest;

    public JdbcTemplate jdbc() {
        return jdbc;
    }

    public synchronized DemoManifest manifest() {
        if (manifest == null) {
            manifest = DemoManifest.load(objectMapper);
        }
        return manifest;
    }

    public String url(String key) {
        return manifest().url(key);
    }

    /** Contraseña de las cuentas de cliente de demostración; vacía si no se configuró. */
    public String clientPassword() {
        return clientPassword == null ? "" : clientPassword;
    }

    /** UUID determinista para una fila de demostración. */
    public static UUID id(String table, String key) {
        return UUID.nameUUIDFromBytes(("demo:" + table + ":" + key).getBytes(StandardCharsets.UTF_8));
    }

    public static Timestamp ts(LocalDateTime value) {
        return Timestamp.valueOf(value);
    }

    public static LocalDateTime now() {
        return BusinessTime.now();
    }

    public boolean exists(String sql, Object... args) {
        return !jdbc.queryForList(sql, args).isEmpty();
    }

    public UUID findId(String sql, Object... args) {
        List<UUID> ids = jdbc.query(sql, (rs, i) -> rs.getObject(1, UUID.class), args);
        return ids.isEmpty() ? null : ids.get(0);
    }

    public UUID requireId(String sql, Object... args) {
        UUID id = findId(sql, args);
        if (id == null) {
            throw new IllegalStateException("No se encontró la fila requerida: " + sql + " " + List.of(args));
        }
        return id;
    }

    /** Inserta si no existe una fila con ese id determinista; devuelve true si insertó. */
    public boolean insertIfAbsent(String table, UUID id, String insertSql, Object... args) {
        if (exists("select 1 from " + table + " where id = ?", id)) {
            return false;
        }
        jdbc.update(insertSql, args);
        return true;
    }
}
