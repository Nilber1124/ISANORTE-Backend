package com.isanorte.constructora_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPublicacion;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
@Table(name = "productos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_producto_sku", columnNames = "sku"),
        @UniqueConstraint(name = "uk_producto_slug", columnNames = "slug")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String sku;

    @NotBlank
    @Column(nullable = false, length = 180)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 220)
    private String slug;

    @Column(length = 500)
    private String resumen;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @PositiveOrZero
    @Column(precision = 12, scale = 2)
    private BigDecimal precioBase;

    @PositiveOrZero
    @Column(precision = 12, scale = 2)
    private BigDecimal precioAnterior;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoDisponibilidad disponibilidad;

    @Builder.Default
    @Column(nullable = false)
    private Boolean destacado = false;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPublicacion estado;

    @Column(length = 180)
    private String tituloSeo;

    @Column(length = 320)
    private String descripcionSeo;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidad_negocio_id", nullable = false)
    private UnidadNegocio unidadNegocio;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @ManyToMany
    @JoinTable(name = "categoria_producto", joinColumns = @JoinColumn(name = "producto_id"), inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    private Set<CategoriaProducto> categorias = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VarianteProducto> variantes = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImagenProducto> imagenes = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EspecificacionProducto> especificaciones = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoProducto> documentos = new ArrayList<>();

    @OneToOne(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private ConfiguracionCalculo configuracionCalculo;

    /**
     * Asocia una categoría a este producto, sincronizando ambos lados de la relación {@code ManyToMany}.
     *
     * @param categoria la categoría a vincular (no nula)
     * @throws NullPointerException si {@code categoria} es {@code null}
     */
    public void addCategoria(CategoriaProducto categoria) {
        Objects.requireNonNull(categoria, "La categoría no puede ser null");
        this.categorias.add(categoria);
        categoria.getProductos().add(this);
    }

    /**
     * Desvincula una categoría de este producto, sincronizando ambos lados de la relación {@code ManyToMany}.
     *
     * @param categoria la categoría a desvincular (no nula)
     * @throws NullPointerException si {@code categoria} es {@code null}
     */
    public void removeCategoria(CategoriaProducto categoria) {
        Objects.requireNonNull(categoria, "La categoría no puede ser null");
        if (this.categorias.remove(categoria)) {
            categoria.getProductos().remove(this);
        }
    }

    /**
     * Agrega una variante al producto y establece la referencia bidireccional correspondiente.
     *
     * @param variante la variante de producto a asociar (no nula)
     * @throws NullPointerException si {@code variante} es {@code null}
     */
    public void addVariante(VarianteProducto variante) {
        Objects.requireNonNull(variante, "La variante no puede ser null");
        if (variante.getProducto() != null && variante.getProducto() != this) {
            throw new IllegalStateException("La variante ya pertenece a otro producto");
        }
        this.variantes.add(variante);
        variante.setProducto(this);
    }

    /**
     * Remueve una variante del producto y desvincula su referencia bidireccional.
     *
     * @param variante la variante a remover (no nula)
     * @throws NullPointerException si {@code variante} es {@code null}
     */
    public void removeVariante(VarianteProducto variante) {
        Objects.requireNonNull(variante, "La variante no puede ser null");
        if (this.variantes.remove(variante)) {
            variante.setProducto(null);
        }
    }

    /**
     * Agrega una imagen a la galería del producto y establece la referencia bidireccional correspondiente.
     *
     * @param imagen la imagen a asociar (no nula)
     * @throws NullPointerException si {@code imagen} es {@code null}
     */
    public void addImagen(ImagenProducto imagen) {
        Objects.requireNonNull(imagen, "La imagen del producto no puede ser null");
        if (imagen.getProducto() != null && imagen.getProducto() != this) {
            throw new IllegalStateException("La imagen ya pertenece a otro producto");
        }
        this.imagenes.add(imagen);
        imagen.setProducto(this);
    }

    /**
     * Remueve una imagen de la galería del producto y desvincula su referencia bidireccional.
     *
     * @param imagen la imagen a remover (no nula)
     * @throws NullPointerException si {@code imagen} es {@code null}
     */
    public void removeImagen(ImagenProducto imagen) {
        Objects.requireNonNull(imagen, "La imagen del producto no puede ser null");
        if (this.imagenes.remove(imagen)) {
            imagen.setProducto(null);
        }
    }

    /**
     * Agrega una especificación técnica al producto y establece la referencia bidireccional correspondiente.
     *
     * @param especificacion la especificación a asociar (no nula)
     * @throws NullPointerException si {@code especificacion} es {@code null}
     */
    public void addEspecificacion(EspecificacionProducto especificacion) {
        Objects.requireNonNull(especificacion, "La especificación no puede ser null");
        if (especificacion.getProducto() != null && especificacion.getProducto() != this) {
            throw new IllegalStateException("La especificación ya pertenece a otro producto");
        }
        this.especificaciones.add(especificacion);
        especificacion.setProducto(this);
    }

    /**
     * Remueve una especificación técnica del producto y desvincula su referencia bidireccional.
     *
     * @param especificacion la especificación a remover (no nula)
     * @throws NullPointerException si {@code especificacion} es {@code null}
     */
    public void removeEspecificacion(EspecificacionProducto especificacion) {
        Objects.requireNonNull(especificacion, "La especificación no puede ser null");
        if (this.especificaciones.remove(especificacion)) {
            especificacion.setProducto(null);
        }
    }

    /**
     * Agrega un documento técnico al producto y establece la referencia bidireccional correspondiente.
     *
     * @param documento el documento a asociar (no nula)
     * @throws NullPointerException si {@code documento} es {@code null}
     */
    public void addDocumento(DocumentoProducto documento) {
        Objects.requireNonNull(documento, "El documento no puede ser null");
        if (documento.getProducto() != null && documento.getProducto() != this) {
            throw new IllegalStateException("El documento ya pertenece a otro producto");
        }
        this.documentos.add(documento);
        documento.setProducto(this);
    }

    /**
     * Remueve un documento técnico del producto y desvincula su referencia bidireccional.
     *
     * @param documento el documento a remover (no nula)
     * @throws NullPointerException si {@code documento} es {@code null}
     */
    public void removeDocumento(DocumentoProducto documento) {
        Objects.requireNonNull(documento, "El documento no puede ser null");
        if (this.documentos.remove(documento)) {
            documento.setProducto(null);
        }
    }

    /**
     * Asigna la configuración de cálculo para este producto en una relación {@code OneToOne},
     * desvinculando de forma segura la instancia anterior en caso de reemplazo o eliminación.
     *
     * @param nuevaConfiguracion la nueva configuración de cálculo o {@code null} para desvincular
     */
    public void setConfiguracionCalculo(ConfiguracionCalculo nuevaConfiguracion) {
        if (nuevaConfiguracion != null
                && nuevaConfiguracion.getProducto() != null
                && nuevaConfiguracion.getProducto() != this) {
            throw new IllegalStateException("La configuración de cálculo ya pertenece a otro producto");
        }
        if (this.configuracionCalculo != null) {
            this.configuracionCalculo.setProducto(null);
        }
        this.configuracionCalculo = nuevaConfiguracion;
        if (nuevaConfiguracion != null) {
            nuevaConfiguracion.setProducto(this);
        }
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();

        if (destacado == null) {
            destacado = false;
        }

        if (estado == null) {
            estado = EstadoPublicacion.BORRADOR;
        }

        if (disponibilidad == null) {
            disponibilidad = EstadoDisponibilidad.CONSULTAR;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
