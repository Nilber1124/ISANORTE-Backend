package com.isanorte.constructora_api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Punto del bloque de servicios de la Home de ISADECOR. No tiene tabla propia: se guarda como lista
 * dentro de {@link IsadecorServicioEmpresa#getPuntos()} (columna {@code puntos}, en JSON).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsadecorServicioPunto {

    private UUID id;

    private String texto;

    @Builder.Default
    private Integer orden = 0;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;
}
