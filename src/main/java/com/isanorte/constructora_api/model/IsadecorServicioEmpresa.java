package com.isanorte.constructora_api.model;

import java.util.ArrayList;
import java.util.List;

import com.isanorte.constructora_api.util.ServicioPuntosConverter;

import com.isanorte.constructora_api.util.BusinessTime;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

/** Bloque de servicios para empresas de la Home de ISADECOR (contenido administrable). */
@Entity
@Table(name = "isadecor_servicios_empresa")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsadecorServicioEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidad_negocio_id", nullable = false)
    private UnidadNegocio unidadNegocio;

    @Column(nullable = false, length = 80)
    private String etiqueta;

    @Column(nullable = false, length = 160)
    private String titulo;

    @Column(nullable = false, length = 400)
    private String descripcion;

    @Column(nullable = false, length = 60)
    private String textoBoton;

    @Column(nullable = false, length = 300)
    private String enlace;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    /** Puntos del bloque (antes tabla isadecor_servicio_puntos). Para modificarlos, asignar una lista nueva. */
    @Builder.Default
    @Convert(converter = ServicioPuntosConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<IsadecorServicioPunto> puntos = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = BusinessTime.now();
        fechaCreacion = now;
        fechaActualizacion = now;
        if (activo == null) activo = true;
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = BusinessTime.now();
    }
}
