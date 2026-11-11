package com.isanorte.constructora_api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class BusinessTimeTest {
    @Test
    void usesExplicitLimaBusinessZone() {
        assertEquals("America/Lima", BusinessTime.ZONE.getId());
        assertEquals(BusinessTime.ZONE.getRules().getOffset(BusinessTime.nowOffset().toInstant()),
                BusinessTime.nowOffset().getOffset());
        assertFalse(BusinessTime.today().toString().isBlank());
        assertFalse(BusinessTime.now().toString().isBlank());
    }
}
