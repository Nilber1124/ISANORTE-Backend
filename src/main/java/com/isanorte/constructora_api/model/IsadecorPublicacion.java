package com.isanorte.constructora_api.model;

import com.isanorte.constructora_api.enums.OrigenPublicacion;
import com.isanorte.constructora_api.enums.RedPublicacion;
import com.isanorte.constructora_api.util.BusinessTime;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

/** Video de una red social que se muestra en la tienda de ISADECOR. */
@Entity
@Table(name = "isadecor_publicaciones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsadecorPublicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidad_negocio_id", nullable = false)
    private UnidadNegocio unidadNegocio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RedPublicacion red;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(length = 200)
    private String titulo;

    @Column(length = 500)
    private String miniaturaUrl;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OrigenPublicacion origen = OrigenPublicacion.MANUAL;

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
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = BusinessTime.now();
    }
}
