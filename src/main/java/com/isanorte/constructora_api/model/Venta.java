package com.isanorte.constructora_api.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.enums.CanalVenta;
import com.isanorte.constructora_api.enums.MetodoPagoVenta;
import com.isanorte.constructora_api.enums.TipoDocumentoCliente;
import com.isanorte.constructora_api.util.BusinessTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Venta registrada a mano desde el admin (por WhatsApp, en tienda u otro canal). No necesita una cuenta de cliente.
 * Al registrarla se descuenta el stock de los productos del catálogo; al anularla se devuelve.
 */
@Entity
@Table(name = "ventas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 40, unique = true)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CanalVenta canal;

    @Column(nullable = false)
    private LocalDate fechaVenta;

    @Column(length = 150)
    private String clienteNombre;

    @Column(length = 30)
    private String clienteTelefono;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private TipoDocumentoCliente tipoDocumento;

    @Column(length = 20)
    private String documento;

    @Column(length = 200)
    private String razonSocial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPagoVenta metodoPago;

    @Column(nullable = false)
    private Boolean pagado;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(length = 500)
    private String notas;

    @Column(nullable = false)
    @Builder.Default
    private Boolean anulada = Boolean.FALSE;

    /** El stock de sus líneas ya salió del inventario (se devuelve al anular). */
    @Column(nullable = false)
    @Builder.Default
    private Boolean stockDescontado = Boolean.FALSE;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleVenta> detalles = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    public void addDetalle(DetalleVenta detalle) {
        detalle.setVenta(this);
        detalles.add(detalle);
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = BusinessTime.now();
        fechaCreacion = now;
        fechaActualizacion = now;
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = BusinessTime.now();
    }
}
