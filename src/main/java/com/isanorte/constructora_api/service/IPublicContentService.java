package com.isanorte.constructora_api.service;

import java.util.List;

import com.isanorte.constructora_api.dto.response.PublicBusinessUnitResponse;
import com.isanorte.constructora_api.dto.response.PublicHomeResponse;
import com.isanorte.constructora_api.dto.response.PublicPageResponse;
import com.isanorte.constructora_api.dto.response.PublicProductCatalogResponse;
import com.isanorte.constructora_api.dto.response.PublicProductDetailResponse;
import com.isanorte.constructora_api.dto.response.PublicProjectResponse;
import com.isanorte.constructora_api.dto.response.PublicSiteResponse;
import com.isanorte.constructora_api.dto.response.ProductoRecomendadoResponse;
import com.isanorte.constructora_api.enums.OrdenCatalogoProducto;
import com.isanorte.constructora_api.enums.TipoPaginaPublica;

public interface IPublicContentService {
    PublicSiteResponse findSite(String clave);
    PublicHomeResponse findHome(String clave);
    PublicPageResponse findPage(String clave, TipoPaginaPublica pagina);
    PublicProjectResponse findPublicProject(String clave, String proyectoSlug);
    PublicBusinessUnitResponse findBusinessUnit(String clave, String slug);
    PublicProductCatalogResponse findProductCatalog(
            String clave, String unidadSlug, int page, int size, String categoriaSlug, String q,
            boolean soloDescuento, boolean soloDisponible, boolean soloDestacados, OrdenCatalogoProducto orden);
    PublicProductDetailResponse findPublicProduct(String clave, String unidadSlug, String productoSlug);
    List<ProductoRecomendadoResponse> findRecommendedProducts(
            String clave, String unidadSlug, String productoSlug);
}
