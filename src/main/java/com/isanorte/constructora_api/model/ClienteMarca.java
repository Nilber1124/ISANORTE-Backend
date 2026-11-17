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

/** Cliente o marca: contenido administrable del sitio público. */
@Entity
@Table(name = "clientes_marca")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteMarca {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = true, length = 150)
    private String nombre;

    @Column(nullable = false, length = 500)
    private String logoUrl;

    @Column(nullable = false, length = 300)
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
        if (activo == null) activo = true;
        if (orden == null) orden = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = BusinessTime.now();
    }
}
