package com.isanorte.constructora_api.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proyectos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_proyecto_slug", columnNames = "slug")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 180)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String slug;

    @Column(length = 150)
    private String cliente;

    @Column(length = 180)
    private String ubicacion;

    @Column(length = 60)
    private String fechaProyecto;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 500)
    private String imagenUrl;

    @Column(length = 300)
    private String imagenAlt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean destacado = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @PositiveOrZero
    @Builder.Default
    @Column(nullable = false)
    private Integer orden = 0;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @ManyToMany
    @JoinTable(name = "proyecto_servicio", joinColumns = @JoinColumn(name = "proyecto_id"), inverseJoinColumns = @JoinColumn(name = "servicio_id"))
    private Set<Servicio> servicios = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    /**
     * Asocia un servicio a este proyecto, sincronizando ambos lados de la relación {@code ManyToMany}.
     *
     * @param servicio el servicio a asociar (no nulo)
     * @throws NullPointerException si {@code servicio} es {@code null}
     */
    public void addServicio(Servicio servicio) {
        Objects.requireNonNull(servicio, "El servicio no puede ser null");
        this.servicios.add(servicio);
        servicio.getProyectos().add(this);
    }

    /**
     * Desvincula un servicio de este proyecto, sincronizando ambos lados de la relación {@code ManyToMany}.
     *
     * @param servicio el servicio a desvincular (no nulo)
     * @throws NullPointerException si {@code servicio} es {@code null}
     */
    public void removeServicio(Servicio servicio) {
        Objects.requireNonNull(servicio, "El servicio no puede ser null");
        if (this.servicios.remove(servicio)) {
            servicio.getProyectos().remove(this);
        }
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();

        if (activo == null) {
            activo = true;
        }
        if (destacado == null) {
            destacado = false;
        }
        if (orden == null) {
            orden = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
