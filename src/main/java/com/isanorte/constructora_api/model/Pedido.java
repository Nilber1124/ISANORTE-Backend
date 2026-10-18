package com.isanorte.constructora_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.isanorte.constructora_api.enums.EstadoPedido;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pedidos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_pedido_codigo", columnNames = "codigo")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(length = 120)
    private String direccionAlias;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String direccionCalle;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String direccionDistrito;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String direccionCiudad;

    @Column(length = 255)
    private String direccionReferencia;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String direccionTelefonoContacto;

    @Column(length = 20)
    private String ruc;

    @Column(length = 200)
    private String razonSocial;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPedido estado;

    @PositiveOrZero
    @Column(precision = 12, scale = 2)
    private BigDecimal totalPedido;

    @Builder.Default
    @Column(nullable = false)
    private Boolean stockDescontado = false;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeguimientoPedido> seguimientos = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    /**
     * Agrega una línea de producto al pedido y sincroniza la referencia bidireccional correspondiente.
     *
     * @param detalle el detalle de pedido a asociar (no nulo)
     * @throws NullPointerException si {@code detalle} es {@code null}
     */
    public void addDetalle(DetallePedido detalle) {
        Objects.requireNonNull(detalle, "El detalle de pedido no puede ser null");
        if (detalle.getPedido() != null && detalle.getPedido() != this) {
            throw new IllegalStateException("El detalle ya pertenece a otro pedido");
        }
        this.detalles.add(detalle);
        detalle.setPedido(this);
    }

    /**
     * Remueve una línea de producto del pedido y desvincula la referencia bidireccional.
     *
     * @param detalle el detalle de pedido a remover (no nulo)
     * @throws NullPointerException si {@code detalle} es {@code null}
     */
    public void removeDetalle(DetallePedido detalle) {
        Objects.requireNonNull(detalle, "El detalle de pedido no puede ser null");
        if (this.detalles.remove(detalle)) {
            detalle.setPedido(null);
        }
    }

    /**
     * Registra un nuevo evento de seguimiento al pedido y sincroniza la referencia bidireccional.
     *
     * @param seguimiento el registro de seguimiento a asociar (no nulo)
     * @throws NullPointerException si {@code seguimiento} es {@code null}
     */
    public void addSeguimiento(SeguimientoPedido seguimiento) {
        Objects.requireNonNull(seguimiento, "El seguimiento de pedido no puede ser null");
        if (seguimiento.getPedido() != null && seguimiento.getPedido() != this) {
            throw new IllegalStateException("El seguimiento ya pertenece a otro pedido");
        }
        this.seguimientos.add(seguimiento);
        seguimiento.setPedido(this);
    }

    /**
     * Remueve un registro de seguimiento del pedido y desvincula la referencia bidireccional.
     *
     * @param seguimiento el registro de seguimiento a remover (no nulo)
     * @throws NullPointerException si {@code seguimiento} es {@code null}
     */
    public void removeSeguimiento(SeguimientoPedido seguimiento) {
        Objects.requireNonNull(seguimiento, "El seguimiento de pedido no puede ser null");
        if (this.seguimientos.remove(seguimiento)) {
            seguimiento.setPedido(null);
        }
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();

        if (estado == null) {
            estado = EstadoPedido.PENDIENTE_PAGO;
        }
        if (stockDescontado == null) {
            stockDescontado = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
