package com.isanorte.constructora_api.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "unidades_negocio", uniqueConstraints = {
        @UniqueConstraint(name = "uk_unidad_negocio_nombre", columnNames = "nombre"),
        @UniqueConstraint(name = "uk_unidad_negocio_slug", columnNames = "slug")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnidadNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 100)
    private String icono;

    @Column(length = 500)
    private String imagenUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Builder.Default
    @Column(nullable = false)
    private Integer orden = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(mappedBy = "unidadNegocio")
    private List<Producto> productos = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    /**
     * Agrega un producto a esta unidad de negocio y sincroniza la relación bidireccional.
     *
     * @param producto el producto a asociar (no nulo)
     * @throws NullPointerException si {@code producto} es {@code null}
     */
    public void addProducto(Producto producto) {
        Objects.requireNonNull(producto, "El producto no puede ser null");
        if (producto.getUnidadNegocio() != null && producto.getUnidadNegocio() != this) {
            throw new IllegalStateException("El producto ya pertenece a otra unidad de negocio");
        }
        producto.cambiarUnidadNegocio(this);
    }

    void agregarProductoReferencia(Producto producto) {
        Objects.requireNonNull(producto, "El producto no puede ser null");
        this.productos.add(producto);
    }

    void removerProductoReferencia(Producto producto) {
        Objects.requireNonNull(producto, "El producto no puede ser null");
        this.productos.remove(producto);
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();

        if (activo == null) {
            activo = true;
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
