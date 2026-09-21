package com.isanorte.constructora_api.service;

import com.isanorte.constructora_api.dto.response.PublicBusinessUnitResponse;
import com.isanorte.constructora_api.dto.response.PublicHomeResponse;
import com.isanorte.constructora_api.dto.response.PublicPageResponse;
import com.isanorte.constructora_api.dto.response.PublicProductCatalogResponse;
import com.isanorte.constructora_api.dto.response.PublicProductDetailResponse;
import com.isanorte.constructora_api.dto.response.PublicSiteResponse;
import com.isanorte.constructora_api.enums.TipoPaginaPublica;

public interface IPublicContentService {
    PublicSiteResponse findSite(String clave);
    PublicHomeResponse findHome(String clave);
    PublicPageResponse findPage(String clave, TipoPaginaPublica pagina);
    PublicBusinessUnitResponse findBusinessUnit(String clave, String slug);
    PublicProductCatalogResponse findProductCatalog(String clave, String unidadSlug);
    PublicProductDetailResponse findPublicProduct(String clave, String unidadSlug, String productoSlug);
}
