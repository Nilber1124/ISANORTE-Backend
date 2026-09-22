package com.isanorte.constructora_api.service.competitor;

import java.util.List;

public interface CompetidorProductoProvider {
    String empresa();
    List<ProductoCompetidor> buscar(ProductoReferencia referencia);
}
