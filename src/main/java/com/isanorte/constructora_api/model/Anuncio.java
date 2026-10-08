package com.isanorte.constructora_api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.isanorte.constructora_api.enums.DestinoAnuncio;
import com.isanorte.constructora_api.enums.TipoAccionAnuncio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Table(name = "anuncios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 160)
    private String titulo;

    @Column(nullable = false, length = 320)
    private String descripcionResumida;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenidoDetallado;

    @Column(columnDefinition = "TEXT")
    private String condiciones;

    @Column(nullable = false, length = 500)
    private String imagenUrl;

    @Column(nullable = false, length = 60)
    private String etiqueta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DestinoAnuncio destino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoAccionAnuncio tipoAccion;

    @Column(nullable = false, length = 500)
    private String destinoAccion;

    @Column(nullable = false, length = 80)
    private String textoBoton;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Builder.Default
    @Column(nullable = false)
    private Integer orden = 0;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        fechaCreacion = now;
        fechaActualizacion = now;
        if (activo == null) activo = true;
        if (orden == null) orden = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}

