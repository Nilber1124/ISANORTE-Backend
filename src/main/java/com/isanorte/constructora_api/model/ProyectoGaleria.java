package com.isanorte.constructora_api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.isanorte.constructora_api.util.BusinessTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Imagen de galería: contenido administrable del sitio público. */
@Entity
@Table(name = "proyecto_galeria")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoGaleria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = true, length = 200)
    private String proyectoSlug;

    @Column(nullable = true, length = 500)
    private String url;

    @Column(nullable = false, length = 300)
    private String alt;

    @Column(nullable = true, length = 20)
    private String etiqueta;

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
        if (activo == null) activo = true;
        if (orden == null) orden = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = BusinessTime.now();
    }
}
