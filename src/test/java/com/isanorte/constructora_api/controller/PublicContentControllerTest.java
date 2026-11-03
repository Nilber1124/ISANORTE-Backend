package com.isanorte.constructora_api.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.isanorte.constructora_api.dto.response.PublicProductCatalogResponse;
import com.isanorte.constructora_api.dto.response.PublicProjectResponse;
import com.isanorte.constructora_api.enums.OrdenCatalogoProducto;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.service.IComparacionCompetidoresService;
import com.isanorte.constructora_api.service.IComparacionPrecioService;
import com.isanorte.constructora_api.service.ICotizacionService;
import com.isanorte.constructora_api.service.IPedidoService;
import com.isanorte.constructora_api.service.IPublicContentService;
import com.isanorte.constructora_api.service.IResenaProductoService;
import com.isanorte.constructora_api.service.ISolicitudContactoService;

class PublicContentControllerTest {

    @Test
    void retornaProyectoPublicoDelegandoEnElContratoDelServicio() {
        IPublicContentService service = mock(IPublicContentService.class);
        PublicProjectResponse project = new PublicProjectResponse(
                "Proyecto", "proyecto", "Descripcion", "Lima", "2026", 1, List.of(), List.of());
        when(service.findPublicProject("isanorte", "proyecto")).thenReturn(project);
        PublicContentController controller = controllerWith(service);

        var response = controller.findPublicProject("isanorte", "proyecto");

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(project, response.getBody());
        verify(service).findPublicProject("isanorte", "proyecto");
    }

    @Test
    void propagaErrorCuandoElProyectoPublicoNoExiste() {
        IPublicContentService service = mock(IPublicContentService.class);
        ModelNotFoundException expected = new ModelNotFoundException("Proyecto no encontrado");
        when(service.findPublicProject("isanorte", "inexistente")).thenThrow(expected);
        PublicContentController controller = controllerWith(service);

        ModelNotFoundException actual = assertThrows(ModelNotFoundException.class,
                () -> controller.findPublicProject("isanorte", "inexistente"));

        assertSame(expected, actual);
        verify(service).findPublicProject("isanorte", "inexistente");
    }

    @Test
    void retornaCatalogoDelegandoEnElServicioConLosParametrosRecibidos() {
        IPublicContentService service = mock(IPublicContentService.class);
        PublicProductCatalogResponse catalog = new PublicProductCatalogResponse(
                new PublicProductCatalogResponse.Unidad("Unidad", "unidad"), List.of(),
                new PublicProductCatalogResponse.Pagina(List.of(), 0, 12, 0, 0));
        when(service.findProductCatalog("isanorte", "unidad", 0, 12, "colectivo", "muro", true, true, true,
                OrdenCatalogoProducto.PRECIO_ASC))
                .thenReturn(catalog);
        PublicContentController controller = controllerWith(service);

        var response = controller.findProductCatalog(
                "isanorte", "unidad", 0, 12, "colectivo", "muro", true, true, true, OrdenCatalogoProducto.PRECIO_ASC);

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(catalog, response.getBody());
        verify(service).findProductCatalog(
                "isanorte", "unidad", 0, 12, "colectivo", "muro", true, true, true, OrdenCatalogoProducto.PRECIO_ASC);
    }

    private PublicContentController controllerWith(IPublicContentService service) {
        return new PublicContentController(service, mock(ISolicitudContactoService.class),
                mock(IComparacionPrecioService.class), mock(IComparacionCompetidoresService.class),
                mock(ICotizacionService.class), mock(IPedidoService.class), mock(IResenaProductoService.class));
    }
}
