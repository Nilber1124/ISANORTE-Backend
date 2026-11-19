package com.isanorte.constructora_api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoMovimientoStock;
import com.isanorte.constructora_api.util.BusinessTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Historial de cambios de stock de un producto o de uno de sus acabados. */
@Entity
@Table(name = "movimientos_stock")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoStock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id")
    private VarianteProducto variante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private TipoMovimientoStock tipo;

    @Column(nullable = false)
    private Integer cantidad;

    private Integer stockResultante;

    @Column(length = 200)
    private String motivo;

    private UUID pedidoId;

    @Column(length = 120)
    private String usuario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = BusinessTime.now();
        }
    }
}
