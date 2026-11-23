package com.isanorte.constructora_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Producto fijado al inicio de una sección de productos de la Home. */
@Entity
@DiscriminatorValue("FIJADO")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class IsadecorSeccionFijado extends IsadecorSeccionSeleccion {

    @Builder.Default
    @Column(nullable = false)
    private Integer orden = 0;
}
