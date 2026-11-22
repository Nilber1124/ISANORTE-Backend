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

/** Textos de cabecera de un bloque de la Home (frase pequeña, título y subtítulo), identificado por su clave. */
@Entity
@Table(name = "textos_bloque")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextoBloque {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 60, unique = true)
    private String clave;

    @Column(length = 150)
    private String etiqueta;

    @Column(length = 300)
    private String titulo;

    @Column(length = 600)
    private String subtitulo;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaActualizacion = BusinessTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = BusinessTime.now();
    }
}
