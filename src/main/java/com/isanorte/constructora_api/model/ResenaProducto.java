package com.isanorte.constructora_api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resenas_producto")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResenaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    /** Referencia que se convertirá en FK cuando exista el agregado Cliente. */
    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    /** Snapshot del nombre autenticado para conservar la autoría visible. */
    @NotBlank
    @Column(name = "nombre_cliente", nullable = false, length = 160)
    private String nombreCliente;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer calificacion;

    @Column(length = 160)
    private String titulo;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Builder.Default
    @Column(name = "compra_verificada", nullable = false)
    private Boolean compraVerificada = false;

    @Builder.Default
    @Column(name = "cantidad_util", nullable = false)
    private Integer cantidadUtil = 0;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (compraVerificada == null) {
            compraVerificada = false;
        }
        if (cantidadUtil == null) {
            cantidadUtil = 0;
        }
    }
}
