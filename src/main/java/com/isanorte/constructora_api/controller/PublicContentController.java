package com.isanorte.constructora_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.SolicitudContactoCreateRequest;
import com.isanorte.constructora_api.dto.response.PublicBusinessUnitResponse;
import com.isanorte.constructora_api.dto.response.PublicHomeResponse;
import com.isanorte.constructora_api.dto.response.PublicPageResponse;
import com.isanorte.constructora_api.dto.response.PublicProductCatalogResponse;
import com.isanorte.constructora_api.dto.response.PublicProductDetailResponse;
import com.isanorte.constructora_api.dto.response.PublicSiteResponse;
import com.isanorte.constructora_api.dto.response.SolicitudContactoPublicResponse;
import com.isanorte.constructora_api.enums.TipoPaginaPublica;
import com.isanorte.constructora_api.service.IPublicContentService;
import com.isanorte.constructora_api.service.IComparacionPrecioService;
import com.isanorte.constructora_api.service.IComparacionCompetidoresService;
import com.isanorte.constructora_api.service.ICotizacionService;
import com.isanorte.constructora_api.dto.request.ComparacionPrecioRequest;
import com.isanorte.constructora_api.dto.request.PublicCotizacionRequest;
import com.isanorte.constructora_api.dto.response.ComparacionPrecioResponse;
import com.isanorte.constructora_api.dto.response.ComparacionCompetidoresResponse;
import com.isanorte.constructora_api.dto.response.PublicCotizacionResponse;
import com.isanorte.constructora_api.service.ISolicitudContactoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/publico")
@RequiredArgsConstructor
public class PublicContentController {
    private final IPublicContentService publicContentService;
    private final ISolicitudContactoService contactoService;
    private final IComparacionPrecioService comparacionPrecioService;
    private final IComparacionCompetidoresService comparacionCompetidoresService;
    private final ICotizacionService cotizacionService;

    @GetMapping("/sitios/{clave}/unidades/{unidadSlug}/productos/{productoSlug}/comparacion-competidores")
    public ResponseEntity<ComparacionCompetidoresResponse> compararCompetidores(
            @PathVariable String clave, @PathVariable String unidadSlug, @PathVariable String productoSlug) {
        return ResponseEntity.ok().cacheControl(org.springframework.http.CacheControl.noStore())
                .body(comparacionCompetidoresService.comparar(clave, unidadSlug, productoSlug));
    }

    @PostMapping("/sitios/{clave}/unidades/{unidadSlug}/productos/{productoSlug}/comparar-precio")
    public ResponseEntity<ComparacionPrecioResponse> compararPrecio(
            @PathVariable String clave, @PathVariable String unidadSlug, @PathVariable String productoSlug,
            @Valid @RequestBody ComparacionPrecioRequest request) {
        return ResponseEntity.ok().cacheControl(org.springframework.http.CacheControl.noStore())
                .body(comparacionPrecioService.comparar(clave, unidadSlug, productoSlug, request));
    }

    @PostMapping("/contacto")
    public ResponseEntity<SolicitudContactoPublicResponse> createContact(
            @Valid @RequestBody SolicitudContactoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactoService.createPublic(request));
    }

    @GetMapping("/sitios/{clave}")
    public ResponseEntity<PublicSiteResponse> findSite(@PathVariable String clave) {
        return ResponseEntity.ok(publicContentService.findSite(clave));
    }

    @GetMapping("/sitios/{clave}/home")
    public ResponseEntity<PublicHomeResponse> findHome(@PathVariable String clave) {
        return ResponseEntity.ok(publicContentService.findHome(clave));
    }

    @GetMapping("/sitios/{clave}/paginas/{pagina}")
    public ResponseEntity<PublicPageResponse> findPage(@PathVariable String clave,
            @PathVariable TipoPaginaPublica pagina) {
        return ResponseEntity.ok(publicContentService.findPage(clave, pagina));
    }

    @GetMapping("/sitios/{clave}/unidades/{slug}")
    public ResponseEntity<PublicBusinessUnitResponse> findBusinessUnit(
            @PathVariable String clave, @PathVariable String slug) {
        return ResponseEntity.ok(publicContentService.findBusinessUnit(clave, slug));
    }

    @GetMapping("/sitios/{clave}/unidades/{unidadSlug}/catalogo")
    public ResponseEntity<PublicProductCatalogResponse> findProductCatalog(
            @PathVariable String clave, @PathVariable String unidadSlug) {
        return ResponseEntity.ok(publicContentService.findProductCatalog(clave, unidadSlug));
    }

    @GetMapping("/sitios/{clave}/unidades/{unidadSlug}/productos/{productoSlug}")
    public ResponseEntity<PublicProductDetailResponse> findPublicProduct(
            @PathVariable String clave, @PathVariable String unidadSlug, @PathVariable String productoSlug) {
        return ResponseEntity.ok(publicContentService.findPublicProduct(clave, unidadSlug, productoSlug));
    }

    @PostMapping("/sitios/{siteKey}/unidades/{unitSlug}/cotizaciones")
    public ResponseEntity<PublicCotizacionResponse> createPublicCotizacion(
            @PathVariable String siteKey,
            @PathVariable String unitSlug,
            @Valid @RequestBody PublicCotizacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cotizacionService.createPublic(siteKey, unitSlug, request));
    }
}
