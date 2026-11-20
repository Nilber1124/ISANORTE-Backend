package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.dto.response.PublicProductCatalogAcabadoResponse;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.model.VarianteProducto;

@Repository
public interface VarianteProductoRepository extends IGenericRepository<VarianteProducto, UUID> {

    /** Acabados distintos de los productos publicados de una unidad, con su color y la cantidad de productos. */
    @Query("""
            SELECT new com.isanorte.constructora_api.dto.response.PublicProductCatalogAcabadoResponse(
                v.nombre, MAX(v.colorHex), COUNT(DISTINCT p.id))
            FROM VarianteProducto v JOIN v.producto p
            WHERE p.unidadNegocio.id = :unidadId AND p.estado = :estado
              AND (v.disponible IS NULL OR v.disponible = true)
            GROUP BY v.nombre
            ORDER BY v.nombre ASC
            """)
    List<PublicProductCatalogAcabadoResponse> findPublicAcabados(
            @Param("unidadId") UUID unidadId, @Param("estado") EstadoPublicacion estado);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID id);
}
