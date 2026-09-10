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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "administradores", uniqueConstraints = {
        @UniqueConstraint(name = "uk_administrador_email", columnNames = "email")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 120)
    private String apellido;

    @NotBlank
    @Email
    @Column(nullable = false, length = 150)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 30)
    private String telefono;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    private LocalDateTime ultimoAcceso;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @ManyToMany
    @JoinTable(name = "administrador_rol", joinColumns = @JoinColumn(name = "administrador_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    /**
     * Asigna un rol a este administrador, sincronizando ambos lados de la relación
     * {@code ManyToMany}.
     *
     * @param rol el rol a asignar (no nulo)
     * @throws NullPointerException si {@code rol} es {@code null}
     */
    public void addRol(Rol rol) {
        Objects.requireNonNull(rol, "El rol no puede ser null");
        this.roles.add(rol);
        rol.getAdministradores().add(this);
    }

    /**
     * Desasigna un rol de este administrador, sincronizando ambos lados de la
     * relación {@code ManyToMany}.
     *
     * @param rol el rol a desasignar (no nulo)
     * @throws NullPointerException si {@code rol} es {@code null}
     */
    public void removeRol(Rol rol) {
        Objects.requireNonNull(rol, "El rol no puede ser null");
        this.roles.remove(rol);
        rol.getAdministradores().remove(this);
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
