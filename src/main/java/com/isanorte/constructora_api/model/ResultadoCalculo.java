package com.isanorte.constructora_api.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoCalculo {

    private BigDecimal medidaIngresada;
    private String unidadEntrada;
    private Integer cantidad;
    private String unidadVenta;
}
