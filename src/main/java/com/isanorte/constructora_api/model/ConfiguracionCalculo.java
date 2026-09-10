package com.isanorte.constructora_api.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "configuraciones_calculo")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionCalculo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Builder.Default
    @Column(nullable = false)
    private Boolean habilitada = true;

    @Column(length = 100)
    private String etiquetaEntrada;

    @Column(length = 50)
    private String unidadEntrada;

    @Column(precision = 12, scale = 4)
    private BigDecimal coberturaPorUnidad;

    @Column(length = 50)
    private String unidadVenta;

    @Column(length = 255)
    private String textoAyuda;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false, unique = true)
    private Producto producto;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    /**
     * Calcula la cantidad requerida del producto en base a una medida de entrada
     * y a la cobertura útil configurada por unidad.
     * <p>
     * Regla de cálculo: {@code cantidad = ceil(medida / coberturaPorUnidad)}.
     * Siempre redondea hacia arriba al entero superior para asegurar la cobertura.
     * </p>
     *
     * @param medida dimensión o área ingresada por el usuario (debe ser mayor a
     *               cero)
     * @return {@link ResultadoCalculo} con el desglose de la estimación de unidades
     *         necesarias
     * @throws IllegalArgumentException si {@code medida} es nula o menor o igual a
     *                                  cero
     * @throws IllegalStateException    si {@code coberturaPorUnidad} no está
     *                                  configurada o es inválida
     */
    public ResultadoCalculo calcular(BigDecimal medida) {
        if (medida == null || medida.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La medida ingresada debe ser mayor a cero.");
        }

        if (coberturaPorUnidad == null || coberturaPorUnidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("La cobertura por unidad no está configurada o es inválida.");
        }

        BigDecimal division = medida.divide(coberturaPorUnidad, 6, RoundingMode.HALF_UP);
        int cantidadCalculada = (int) Math.ceil(division.doubleValue());

        return ResultadoCalculo.builder()
                .medidaIngresada(medida)
                .unidadEntrada(this.unidadEntrada)
                .cantidad(cantidadCalculada)
                .unidadVenta(this.unidadVenta)
                .build();
    }

    @PrePersist
    protected void onCreate() {
        fechaActualizacion = LocalDateTime.now();
        if (habilitada == null) {
            habilitada = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
