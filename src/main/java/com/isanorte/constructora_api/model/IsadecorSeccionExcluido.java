package com.isanorte.constructora_api.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** Producto excluido de una sección de productos de la Home. */
@Entity
@DiscriminatorValue("EXCLUIDO")
@SuperBuilder
@NoArgsConstructor
public class IsadecorSeccionExcluido extends IsadecorSeccionSeleccion {
}
