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

/** Credencial: contenido administrable del sitio público. */
@Entity
@Table(name = "credenciales")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credencial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = true, length = 200)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String entidad;

    @Column(nullable = true, length = 30)
    private String tipo;

    @Column(nullable = false, length = 80)
    private String codigo;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false, length = 500)
    private String imagenUrl;

    @Column(nullable = false, length = 300)
    private String imagenAlt;

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
