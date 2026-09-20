package com.isanorte.constructora_api.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
@Table(name = "servicios", uniqueConstraints = {
        @UniqueConstraint(name = "uk_servicio_slug", columnNames = "slug")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 180)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String slug;

    @Column(length = 500)
    private String resumen;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 100)
    private String icono;

    @Column(length = 500)
    private String imagenUrl;

    @Column(length = 180)
    private String etiqueta;

    @Column(length = 300)
    private String imagenAlt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean destacado = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer orden = 0;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @ManyToMany(mappedBy = "servicios")
    private Set<Proyecto> proyectos = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC, id ASC")
    private List<BeneficioServicio> beneficios = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    /**
     * Vincula un proyecto a este servicio, sincronizando ambos lados de la relación {@code ManyToMany}.
     *
     * @param proyecto el proyecto a asociar (no nulo)
     * @throws NullPointerException si {@code proyecto} es {@code null}
     */
    public void addProyecto(Proyecto proyecto) {
        Objects.requireNonNull(proyecto, "El proyecto no puede ser null");
        this.proyectos.add(proyecto);
        proyecto.getServicios().add(this);
    }

    /**
     * Desvincula un proyecto de este servicio, sincronizando ambos lados de la relación {@code ManyToMany}.
     *
     * @param proyecto el proyecto a desvincular (no nulo)
     * @throws NullPointerException si {@code proyecto} es {@code null}
     */
    public void removeProyecto(Proyecto proyecto) {
        Objects.requireNonNull(proyecto, "El proyecto no puede ser null");
        if (this.proyectos.remove(proyecto)) {
            proyecto.getServicios().remove(this);
        }
    }

    public void addBeneficio(BeneficioServicio beneficio) {
        Objects.requireNonNull(beneficio, "El beneficio no puede ser null");
        if (beneficio.getServicio() != null && beneficio.getServicio() != this) {
            throw new IllegalStateException("El beneficio ya pertenece a otro servicio");
        }
        beneficios.add(beneficio);
        beneficio.setServicio(this);
    }

    public void removeBeneficio(BeneficioServicio beneficio) {
        Objects.requireNonNull(beneficio, "El beneficio no puede ser null");
        if (beneficios.remove(beneficio)) beneficio.setServicio(null);
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
