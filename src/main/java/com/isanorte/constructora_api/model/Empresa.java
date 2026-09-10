package com.isanorte.constructora_api.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "empresa", uniqueConstraints = {
        @UniqueConstraint(name = "uk_empresa_ruc", columnNames = "ruc")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 180)
    private String razonSocial;

    @NotBlank
    @Column(nullable = false, length = 180)
    private String nombreComercial;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String ruc;

    @Column(length = 255)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 30)
    private String telefono;

    @Column(length = 30)
    private String telefonoSecundario;

    @Column(length = 120)
    private String email;

    @Column(length = 120)
    private String emailVentas;

    @Column(length = 30)
    private String whatsapp;

    @Column(length = 180)
    private String horarioAtencion;

    @Column(columnDefinition = "TEXT")
    private String mision;

    @Column(columnDefinition = "TEXT")
    private String vision;

    @Column(columnDefinition = "TEXT")
    private String valores;

    @Column(columnDefinition = "TEXT")
    private String resumenNosotros;

    @OneToOne(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private ConfiguracionSitio configuracionSitio;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RedSocial> redesSociales = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(mappedBy = "empresa")
    private List<UnidadNegocio> unidadesNegocio = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    /**
     * Asigna la configuración del sitio para esta empresa en una relación {@code OneToOne},
     * desvinculando de forma segura la instancia anterior en memoria si existiese.
     *
     * @param nuevaConfiguracion la nueva configuración del sitio o {@code null} para desvincular
     */
    public void setConfiguracionSitio(ConfiguracionSitio nuevaConfiguracion) {
        if (this.configuracionSitio != null) {
            this.configuracionSitio.setEmpresa(null);
        }
        this.configuracionSitio = nuevaConfiguracion;
        if (nuevaConfiguracion != null) {
            nuevaConfiguracion.setEmpresa(this);
        }
    }

    /**
     * Agrega una red social institucional y establece la relación bidireccional.
     *
     * @param redSocial la red social a asociar (no nula)
     * @throws NullPointerException si {@code redSocial} es {@code null}
     */
    public void addRedSocial(RedSocial redSocial) {
        Objects.requireNonNull(redSocial, "La red social no puede ser null");
        this.redesSociales.add(redSocial);
        redSocial.setEmpresa(this);
    }

    /**
     * Remueve una red social institucional y desvincula la relación bidireccional.
     *
     * @param redSocial la red social a remover (no nula)
     * @throws NullPointerException si {@code redSocial} es {@code null}
     */
    public void removeRedSocial(RedSocial redSocial) {
        Objects.requireNonNull(redSocial, "La red social no puede ser null");
        this.redesSociales.remove(redSocial);
        redSocial.setEmpresa(null);
    }

    /**
     * Agrega una unidad de negocio a la empresa y establece la relación bidireccional.
     *
     * @param unidadNegocio la unidad de negocio a asociar (no nula)
     * @throws NullPointerException si {@code unidadNegocio} es {@code null}
     */
    public void addUnidadNegocio(UnidadNegocio unidadNegocio) {
        Objects.requireNonNull(unidadNegocio, "La unidad de negocio no puede ser null");
        this.unidadesNegocio.add(unidadNegocio);
        unidadNegocio.setEmpresa(this);
    }

    /**
     * Remueve una unidad de negocio de la empresa y desvincula la relación bidireccional.
     *
     * @param unidadNegocio la unidad de negocio a remover (no nula)
     * @throws NullPointerException si {@code unidadNegocio} es {@code null}
     */
    public void removeUnidadNegocio(UnidadNegocio unidadNegocio) {
        Objects.requireNonNull(unidadNegocio, "La unidad de negocio no puede ser null");
        this.unidadesNegocio.remove(unidadNegocio);
        unidadNegocio.setEmpresa(null);
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
