package com.isanorte.constructora_api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoImagenProyecto;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "imagenes_proyecto")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagenProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String url;

    @Column(length = 150)
    private String titulo;

    @Column(length = 300)
    private String descripcion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoImagenProyecto tipo;

    @Builder.Default
    @Column(nullable = false)
    private Boolean esPrincipal = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer orden = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();

        if (tipo == null) {
            tipo = TipoImagenProyecto.GENERAL;
        }
        if (esPrincipal == null) {
            esPrincipal = false;
        }
        if (orden == null) {
            orden = 0;
        }
    }
}
