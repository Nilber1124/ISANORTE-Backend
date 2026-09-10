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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(name = "uk_rol_nombre", columnNames = "nombre")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @ManyToMany
    @JoinTable(name = "rol_permiso", joinColumns = @JoinColumn(name = "rol_id"), inverseJoinColumns = @JoinColumn(name = "permiso_id"))
    private Set<Permiso> permisos = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @ManyToMany(mappedBy = "roles")
    private Set<Administrador> administradores = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    /**
     * Asocia un permiso a este rol, sincronizando ambos lados de la relación {@code ManyToMany}.
     *
     * @param permiso el permiso a asociar (no nulo)
     * @throws NullPointerException si {@code permiso} es {@code null}
     */
    public void addPermiso(Permiso permiso) {
        Objects.requireNonNull(permiso, "El permiso no puede ser null");
        this.permisos.add(permiso);
        permiso.getRoles().add(this);
    }

    /**
     * Desvincula un permiso de este rol, sincronizando ambos lados de la relación {@code ManyToMany}.
     *
     * @param permiso el permiso a desvincular (no nulo)
     * @throws NullPointerException si {@code permiso} es {@code null}
     */
    public void removePermiso(Permiso permiso) {
        Objects.requireNonNull(permiso, "El permiso no puede ser null");
        this.permisos.remove(permiso);
        permiso.getRoles().remove(this);
    }

    /**
     * Asocia un administrador a este rol, sincronizando ambos lados de la relación {@code ManyToMany}.
     *
     * @param administrador el administrador a asociar (no nulo)
     * @throws NullPointerException si {@code administrador} es {@code null}
     */
    public void addAdministrador(Administrador administrador) {
        Objects.requireNonNull(administrador, "El administrador no puede ser null");
        this.administradores.add(administrador);
        administrador.getRoles().add(this);
    }

    /**
     * Desvincula un administrador de este rol, sincronizando ambos lados de la relación {@code ManyToMany}.
     *
     * @param administrador el administrador a desvincular (no nulo)
     * @throws NullPointerException si {@code administrador} es {@code null}
     */
    public void removeAdministrador(Administrador administrador) {
        Objects.requireNonNull(administrador, "El administrador no puede ser null");
        this.administradores.remove(administrador);
        administrador.getRoles().remove(this);
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();

        if (activo == null) {
            activo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
