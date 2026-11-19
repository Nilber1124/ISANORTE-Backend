package com.isanorte.constructora_api.model;

import com.isanorte.constructora_api.util.BusinessTime;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

/** Hero de la Home de ISADECOR (contenido administrable). */
@Entity
@Table(name = "isadecor_hero")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsadecorHero {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidad_negocio_id", nullable = false)
    private UnidadNegocio unidadNegocio;

    @Column(nullable = false, length = 160)
    private String titulo;

    @Column(nullable = false, length = 300)
    private String subtitulo;

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
        if (activo == null) activo = true;
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = BusinessTime.now();
    }
}
