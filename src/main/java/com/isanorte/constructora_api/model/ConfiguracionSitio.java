package com.isanorte.constructora_api.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "configuraciones_sitio", uniqueConstraints = {
        @UniqueConstraint(name = "uk_configuracion_sitio_empresa", columnNames = "empresa_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionSitio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 150)
    private String tituloSitio;

    @Column(length = 300)
    private String descripcionSitio;

    @Column(length = 500)
    private String logoUrl;

    @Column(length = 500)
    private String logoBlancoUrl;

    @Column(length = 500)
    private String faviconUrl;

    @Column(length = 20)
    private String colorPrimario;

    @Column(length = 20)
    private String colorSecundario;

    @Column(length = 255)
    private String textoPiePagina;

    @Column(columnDefinition = "TEXT")
    private String scriptsHead;

    @Column(columnDefinition = "TEXT")
    private String scriptsBody;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false, unique = true)
    private Empresa empresa;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(mappedBy = "configuracionSitio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeccionLanding> secciones = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    /**
     * Agrega una sección de landing a la configuración del sitio y establece la
     * referencia bidireccional.
     *
     * @param seccion la sección a asociar (no nula)
     * @throws NullPointerException si {@code seccion} es {@code null}
     */
    public void addSeccion(SeccionLanding seccion) {
        Objects.requireNonNull(seccion, "La sección landing no puede ser null");
        if (seccion.getConfiguracionSitio() != null && seccion.getConfiguracionSitio() != this) {
            throw new IllegalStateException("La sección landing ya pertenece a otra configuración de sitio");
        }
        this.secciones.add(seccion);
        seccion.setConfiguracionSitio(this);
    }

    /**
     * Remueve una sección de landing de la configuración del sitio y desvincula la
     * referencia bidireccional.
     *
     * @param seccion la sección a remover (no nula)
     * @throws NullPointerException si {@code seccion} es {@code null}
     */
    public void removeSeccion(SeccionLanding seccion) {
        Objects.requireNonNull(seccion, "La sección landing no puede ser null");
        if (this.secciones.remove(seccion)) {
            seccion.setConfiguracionSitio(null);
        }
    }

    @PrePersist
    protected void onCreate() {
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
