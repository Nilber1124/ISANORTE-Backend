package com.isanorte.constructora_api.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoSeccionLanding;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "secciones_landing")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeccionLanding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoSeccionLanding tipo;

    @Column(length = 180)
    private String titulo;

    @Column(length = 180)
    private String etiqueta;

    @Column(length = 255)
    private String subtitulo;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    @Column(length = 500)
    private String imagenUrl;

    @Column(length = 300)
    private String imagenAlt;

    @Column(length = 80)
    private String textoBoton;

    @Column(length = 300)
    private String enlaceBoton;

    @Builder.Default
    @Column(nullable = false)
    private Integer orden = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean visible = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "configuracion_sitio_id", nullable = false)
    private ConfiguracionSitio configuracionSitio;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(mappedBy = "seccionLanding", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HeroScene> escenas = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(mappedBy = "seccionLanding", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccionLanding> acciones = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    public void addEscena(HeroScene escena) {
        Objects.requireNonNull(escena, "La escena no puede ser null");
        if (escena.getSeccionLanding() != null && escena.getSeccionLanding() != this) {
            throw new IllegalStateException("La escena ya pertenece a otra sección");
        }
        escenas.add(escena);
        escena.setSeccionLanding(this);
    }

    public void removeEscena(HeroScene escena) {
        Objects.requireNonNull(escena, "La escena no puede ser null");
        if (escenas.remove(escena)) escena.setSeccionLanding(null);
    }

    public void addAccion(AccionLanding accion) {
        Objects.requireNonNull(accion, "La acción no puede ser null");
        if (accion.getSeccionLanding() != null && accion.getSeccionLanding() != this) {
            throw new IllegalStateException("La acción ya pertenece a otra sección");
        }
        acciones.add(accion);
        accion.setSeccionLanding(this);
    }

    public void removeAccion(AccionLanding accion) {
        Objects.requireNonNull(accion, "La acción no puede ser null");
        if (acciones.remove(accion)) accion.setSeccionLanding(null);
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();

        if (orden == null) {
            orden = 0;
        }
        if (visible == null) {
            visible = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
