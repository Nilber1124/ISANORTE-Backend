package com.isanorte.constructora_api.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "categorias_producto", uniqueConstraints = {
        @UniqueConstraint(name = "uk_categoria_producto_slug", columnNames = "slug")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaProducto {

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

    @Column(length = 500)
    private String imagenUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Builder.Default
    @Column(nullable = false)
    private Integer orden = 0;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_negocio_id")
    private UnidadNegocio unidadNegocio;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @ManyToMany(mappedBy = "categorias")
    private Set<Producto> productos = new HashSet<>();

    /**
     * Vincula un producto a esta categoría, sincronizando ambos lados de la
     * relación {@code ManyToMany}.
     *
     * @param producto el producto a asociar (no nulo)
     * @throws NullPointerException si {@code producto} es {@code null}
     */
    public void addProducto(Producto producto) {
        Objects.requireNonNull(producto, "El producto no puede ser null");
        this.productos.add(producto);
        producto.getCategorias().add(this);
    }

    /**
     * Desvincula un producto de esta categoría, sincronizando ambos lados de la
     * relación {@code ManyToMany}.
     *
     * @param producto el producto a desvincular (no nulo)
     * @throws NullPointerException si {@code producto} es {@code null}
     */
    public void removeProducto(Producto producto) {
        Objects.requireNonNull(producto, "El producto no puede ser null");
        this.productos.remove(producto);
        producto.getCategorias().remove(this);
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
