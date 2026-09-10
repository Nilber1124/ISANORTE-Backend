package com.isanorte.constructora_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.isanorte.constructora_api.enums.CanalCotizacion;
import com.isanorte.constructora_api.enums.EstadoCotizacion;

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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cotizaciones", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cotizacion_codigo", columnNames = "codigo")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String codigo;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String nombreCliente;

    @NotBlank
    @Email
    @Column(nullable = false, length = 120)
    private String emailCliente;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String telefonoCliente;

    @Column(length = 150)
    private String empresaCliente;

    @Column(length = 100)
    private String ciudad;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CanalCotizacion canal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoCotizacion estado;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalEstimado;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCotizacion> detalles = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeguimientoCotizacion> seguimientos = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    /**
     * Agrega un ítem o detalle a la cotización y sincroniza la referencia bidireccional correspondiente.
     *
     * @param detalle el detalle de cotización a asociar (no nulo)
     * @throws NullPointerException si {@code detalle} es {@code null}
     */
    public void addDetalle(DetalleCotizacion detalle) {
        Objects.requireNonNull(detalle, "El detalle de cotización no puede ser null");
        this.detalles.add(detalle);
        detalle.setCotizacion(this);
    }

    /**
     * Remueve un ítem o detalle de la cotización y desvincula la referencia bidireccional.
     *
     * @param detalle el detalle de cotización a remover (no nulo)
     * @throws NullPointerException si {@code detalle} es {@code null}
     */
    public void removeDetalle(DetalleCotizacion detalle) {
        Objects.requireNonNull(detalle, "El detalle de cotización no puede ser null");
        this.detalles.remove(detalle);
        detalle.setCotizacion(null);
    }

    /**
     * Registra un nuevo evento o nota de seguimiento a la cotización y sincroniza la referencia bidireccional.
     *
     * @param seguimiento el registro de seguimiento a asociar (no nulo)
     * @throws NullPointerException si {@code seguimiento} es {@code null}
     */
    public void addSeguimiento(SeguimientoCotizacion seguimiento) {
        Objects.requireNonNull(seguimiento, "El seguimiento de cotización no puede ser null");
        this.seguimientos.add(seguimiento);
        seguimiento.setCotizacion(this);
    }

    /**
     * Remueve un registro de seguimiento de la cotización y desvincula la referencia bidireccional.
     *
     * @param seguimiento el registro de seguimiento a remover (no nulo)
     * @throws NullPointerException si {@code seguimiento} es {@code null}
     */
    public void removeSeguimiento(SeguimientoCotizacion seguimiento) {
        Objects.requireNonNull(seguimiento, "El seguimiento de cotización no puede ser null");
        this.seguimientos.remove(seguimiento);
        seguimiento.setCotizacion(null);
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();

        if (canal == null) {
            canal = CanalCotizacion.FORMULARIO;
        }
        if (estado == null) {
            estado = EstadoCotizacion.NUEVA;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
