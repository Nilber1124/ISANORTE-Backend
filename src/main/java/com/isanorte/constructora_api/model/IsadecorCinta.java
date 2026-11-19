package com.isanorte.constructora_api.model;

import com.isanorte.constructora_api.util.BusinessTime;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

/** Aviso de la cinta de la Home de ISADECOR (contenido administrable). */
@Entity
@Table(name = "isadecor_cinta")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsadecorCinta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidad_negocio_id", nullable = false)
    private UnidadNegocio unidadNegocio;

    @Column(nullable = false, length = 160)
    private String texto;

    @Column(nullable = true, length = 300)
    private String enlace;

    @Builder.Default
    @Column(nullable = false)
    private Integer orden = 0;

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
        if (orden == null) orden = 0;
        if (activo == null) activo = true;
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = BusinessTime.now();
    }
}
