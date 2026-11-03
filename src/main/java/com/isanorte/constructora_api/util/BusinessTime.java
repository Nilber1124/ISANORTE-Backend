package com.isanorte.constructora_api.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/** Política temporal única para las fechas de negocio persistidas y expuestas por la API. */
public final class BusinessTime {
    public static final ZoneId ZONE = ZoneId.of("America/Lima");

    private BusinessTime() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZONE);
    }

    public static LocalDate today() {
        return LocalDate.now(ZONE);
    }

    public static OffsetDateTime nowOffset() {
        return OffsetDateTime.now(ZONE);
    }
}
