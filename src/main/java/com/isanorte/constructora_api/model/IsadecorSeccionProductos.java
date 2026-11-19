package com.isanorte.constructora_api.model;

import com.isanorte.constructora_api.enums.ClaveSeccionProductos;
import com.isanorte.constructora_api.enums.ModoSeccionProductos;
import com.isanorte.constructora_api.util.BusinessTime;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

/** Sección de productos de la Home de ISADECOR (contenido administrable). */
@Entity
@Table(name = "isadecor_secciones_productos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsadecorSeccionProductos {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidad_negocio_id", nullable = false)
    private UnidadNegocio unidadNegocio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClaveSeccionProductos clave;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(nullable = false, length = 80)
    private String textoEnlace;

    @Column(nullable = true, length = 150)
    private String categoriaEnlace;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModoSeccionProductos modo;

    @Builder.Default
    @Column(nullable = false)
    private Integer ventanaDias = 30;

    @Builder.Default
    @Column(nullable = false)
    private Integer limite = 8;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = BusinessTime.now();
        fechaCreacion = now;
        fechaActualizacion = now;
        if (ventanaDias == null) ventanaDias = 0;
        if (limite == null) limite = 0;
        if (activo == null) activo = true;
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = BusinessTime.now();
    }
}
