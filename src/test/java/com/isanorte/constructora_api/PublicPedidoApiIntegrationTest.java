package com.isanorte.constructora_api;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import com.isanorte.constructora_api.dto.request.ClienteRegistroRequest;
import com.isanorte.constructora_api.dto.request.EstadoPedidoRequest;
import com.isanorte.constructora_api.dto.request.PublicPedidoRequest;
import com.isanorte.constructora_api.dto.request.PublicPedidoRequest.DetallePublicoRequest;
import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPedido;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.Pedido;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.service.IClienteAuthService;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.PedidoRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

/**
 * Sin {@code @Transactional} a nivel de clase a propósito: {@code PedidoService.createPublic} usa
 * {@code Propagation.NOT_SUPPORTED} (ver esa clase) para garantizar que cada reintento ante un
 * choque de concurrencia abra su propia transacción real, nunca la del request anterior. Envolver
 * este test en una transacción de rollback automático suspendería esa transacción y los datos de
 * fixture (aún sin commit) quedarían invisibles para la llamada real al servicio, dando 404.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PublicPedidoApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private ConfiguracionSitioRepository configuracionRepository;
    @Autowired private UnidadNegocioRepository unidadRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private IClienteAuthService clienteAuthService;

    @Test
    void creaPedidoPublicoRevalidandoPrecioYDescontandoStock() throws Exception {
        ConfiguracionSitio site = site("order1");
        UnidadNegocio unit = unit(site.getEmpresa(), "isadecor", true);
        Producto product = product(unit, "cemento-saco", EstadoPublicacion.PUBLICADO, new BigDecimal("30.00"), 10);

        PublicPedidoRequest request = new PublicPedidoRequest(
                null, "Casa", "Av. Siempre Viva 123", "Miraflores", "Lima", "Portón azul", "987654321",
                null, null, List.of(new DetallePublicoRequest(product.getSlug(), null, 4)));

        var result = mockMvc.perform(post("/api/publico/sitios/{siteKey}/unidades/{unitSlug}/pedidos",
                site.getClave(), unit.getSlug())
                .header("Authorization", "Bearer " + clienteToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo", startsWith("PED-")))
                .andExpect(jsonPath("$.estado").value("PENDIENTE_PAGO"))
                .andExpect(jsonPath("$.direccionCalle").value("Av. Siempre Viva 123"))
                .andExpect(jsonPath("$.costoEnvio").value(10.00))
                .andExpect(jsonPath("$.totalPedido").value(130.00))
                .andExpect(jsonPath("$.detalles.length()").value(1))
                .andExpect(jsonPath("$.detalles[0].precioUnitario").value(30.00))
                .andExpect(jsonPath("$.detalles[0].subtotal").value(120.00))
                .andReturn();

        String codigo = objectMapper.readTree(result.getResponse().getContentAsString()).get("codigo").asText();
        Pedido saved = pedidoRepository.findByCodigo(codigo).orElseThrow();
        assertEquals(new BigDecimal("130.00"), saved.getTotalPedido());
        assertTrue(saved.getStockDescontado());
        assertNotNull(saved.getCliente());

        Producto refreshed = productoRepository.findById(product.getId()).orElseThrow();
        assertEquals(6, refreshed.getStockActual());
    }

    @Test
    void rechazaPedidoSiElStockEsInsuficiente() throws Exception {
        ConfiguracionSitio site = site("order-stock");
        UnidadNegocio unit = unit(site.getEmpresa(), "isadecor", true);
        Producto product = product(unit, "fierro-corrugado", EstadoPublicacion.PUBLICADO, new BigDecimal("50.00"), 2);

        PublicPedidoRequest request = new PublicPedidoRequest(
                null, null, "Calle 1", "Distrito", "Ciudad", null, "987654321",
                null, null, List.of(new DetallePublicoRequest(product.getSlug(), null, 5)));

        mockMvc.perform(post("/api/publico/sitios/{siteKey}/unidades/{unitSlug}/pedidos",
                site.getClave(), unit.getSlug())
                .header("Authorization", "Bearer " + clienteToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        Producto unchanged = productoRepository.findById(product.getId()).orElseThrow();
        assertEquals(2, unchanged.getStockActual());
    }

    @Test
    void rechazaPedidoSinDireccionGuardadaNiCamposEnLinea() throws Exception {
        ConfiguracionSitio site = site("order-no-address");
        UnidadNegocio unit = unit(site.getEmpresa(), "isadecor", true);
        Producto product = product(unit, "yeso-bolsa", EstadoPublicacion.PUBLICADO, new BigDecimal("20.00"), null);

        PublicPedidoRequest request = new PublicPedidoRequest(
                null, null, null, null, null, null, null,
                null, null, List.of(new DetallePublicoRequest(product.getSlug(), null, 1)));

        mockMvc.perform(post("/api/publico/sitios/{siteKey}/unidades/{unitSlug}/pedidos",
                site.getClave(), unit.getSlug())
                .header("Authorization", "Bearer " + clienteToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rechazaPedidoSiVarianteNoPerteneceAlProducto() throws Exception {
        ConfiguracionSitio site = site("order-invalid-var");
        UnidadNegocio unit = unit(site.getEmpresa(), "isadecor", true);
        Producto product = product(unit, "tubo-pvc", EstadoPublicacion.PUBLICADO, new BigDecimal("15.00"), null);

        PublicPedidoRequest request = new PublicPedidoRequest(
                null, null, "Calle 1", "Distrito", "Ciudad", null, "987654321",
                null, null, List.of(new DetallePublicoRequest(product.getSlug(), "SKU-INEXISTENTE", 1)));

        mockMvc.perform(post("/api/publico/sitios/{siteKey}/unidades/{unitSlug}/pedidos",
                site.getClave(), unit.getSlug())
                .header("Authorization", "Bearer " + clienteToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelarUnPedidoConStockDescontadoLoRestituyeYBloqueaNuevaTransicion() throws Exception {
        ConfiguracionSitio site = site("order-cancel");
        UnidadNegocio unit = unit(site.getEmpresa(), "isadecor", true);
        Producto product = product(unit, "ladrillo-king-kong", EstadoPublicacion.PUBLICADO, new BigDecimal("1.50"), 100);

        PublicPedidoRequest request = new PublicPedidoRequest(
                null, null, "Calle 1", "Distrito", "Ciudad", null, "987654321",
                null, null, List.of(new DetallePublicoRequest(product.getSlug(), null, 30)));

        var result = mockMvc.perform(post("/api/publico/sitios/{siteKey}/unidades/{unitSlug}/pedidos",
                site.getClave(), unit.getSlug())
                .header("Authorization", "Bearer " + clienteToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String codigo = objectMapper.readTree(result.getResponse().getContentAsString()).get("codigo").asText();
        Pedido saved = pedidoRepository.findByCodigo(codigo).orElseThrow();
        assertEquals(70, productoRepository.findById(product.getId()).orElseThrow().getStockActual());

        mockMvc.perform(patch("/api/pedidos/{id}/estado", saved.getId())
                .with(jwt().authorities(() -> "ROLE_ADMINISTRADOR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new EstadoPedidoRequest(EstadoPedido.CANCELADO))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));

        assertEquals(100, productoRepository.findById(product.getId()).orElseThrow().getStockActual());

        mockMvc.perform(patch("/api/pedidos/{id}/estado", saved.getId())
                .with(jwt().authorities(() -> "ROLE_ADMINISTRADOR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new EstadoPedidoRequest(EstadoPedido.EN_PREPARACION))))
                .andExpect(status().isConflict());
    }

    @Test
    void recojoEnTiendaNoCobraEnvioYElPagoSimuladoMarcaElPedidoComoPagadoYAvisaAlAdmin() throws Exception {
        ConfiguracionSitio site = site("order-pago");
        UnidadNegocio unit = unit(site.getEmpresa(), "isadecor", true);
        Producto product = product(unit, "pintura", EstadoPublicacion.PUBLICADO, new BigDecimal("50.00"), 10);
        String token = clienteToken();

        PublicPedidoRequest request = new PublicPedidoRequest(null, null, null, null, null, null, "987654321",
                null, null, com.isanorte.constructora_api.enums.MetodoEntrega.RECOJO_TIENDA,
                List.of(new DetallePublicoRequest(product.getSlug(), null, 2)));

        var result = mockMvc.perform(post("/api/publico/sitios/{siteKey}/unidades/{unitSlug}/pedidos",
                site.getClave(), unit.getSlug())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.metodoEntrega").value("RECOJO_TIENDA"))
                .andExpect(jsonPath("$.costoEnvio").value(0))
                .andExpect(jsonPath("$.totalPedido").value(100.00))
                .andReturn();
        String codigo = objectMapper.readTree(result.getResponse().getContentAsString()).get("codigo").asText();

        // Un pago rechazado deja el pedido pendiente.
        mockMvc.perform(post("/api/publico/cuenta/pedidos/{codigo}/pago", codigo)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"RECHAZADO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aprobado").value(false))
                .andExpect(jsonPath("$.pedido.estado").value("PENDIENTE_PAGO"));

        mockMvc.perform(post("/api/publico/cuenta/pedidos/{codigo}/pago", codigo)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"APROBADO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aprobado").value(true))
                .andExpect(jsonPath("$.pedido.estado").value("PAGADO_ONLINE"))
                .andExpect(jsonPath("$.pedido.referenciaPago", startsWith("SIM-")));

        // Ya pagado: no se cobra dos veces.
        mockMvc.perform(post("/api/publico/cuenta/pedidos/{codigo}/pago", codigo)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"APROBADO\"}"))
                .andExpect(status().isConflict());

        // Otro cliente no puede pagar un pedido ajeno.
        mockMvc.perform(post("/api/publico/cuenta/pedidos/{codigo}/pago", codigo)
                .header("Authorization", "Bearer " + clienteToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"APROBADO\"}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/notificaciones").with(jwt().authorities(() -> "ROLE_ADMINISTRADOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[?(@.titulo == 'Pedido nuevo: " + codigo + "')]").isNotEmpty())
                .andExpect(jsonPath("$.items[?(@.titulo == 'Pago recibido: " + codigo + "')]").isNotEmpty());
    }

    private ConfiguracionSitio site(String seed) {
        Empresa company = company(seed);
        ConfiguracionSitio site = ConfiguracionSitio.builder()
                .clave("site-order-" + seed + "-" + random(6))
                .tituloSitio("Sitio " + seed)
                .empresa(company)
                .build();
        company.setConfiguracionSitio(site);
        return configuracionRepository.saveAndFlush(site);
    }

    private Empresa company(String seed) {
        return empresaRepository.saveAndFlush(Empresa.builder()
                .razonSocial("Razón " + seed)
                .nombreComercial("Comercial " + seed)
                .ruc("R" + random(19))
                .build());
    }

    private UnidadNegocio unit(Empresa company, String seed, boolean active) {
        return unidadRepository.saveAndFlush(UnidadNegocio.builder()
                .nombre("Unidad " + seed + " " + random(4))
                .slug(seed + "-" + random(4))
                .empresa(company)
                .activo(active)
                .orden(0)
                .build());
    }

    private Producto product(UnidadNegocio unit, String seed, EstadoPublicacion state, BigDecimal precio,
            Integer stock) {
        Producto product = Producto.builder()
                .sku("SKU-" + seed + "-" + random(5))
                .nombre("Producto " + seed + " " + random(4))
                .slug(seed + "-" + random(5))
                .descripcion("Descripción para pedido")
                .disponibilidad(EstadoDisponibilidad.DISPONIBLE)
                .estado(state)
                .precioBase(precio)
                .stockActual(stock)
                .build();
        unit.addProducto(product);
        return productoRepository.saveAndFlush(product);
    }

    private String random(int length) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, length);
    }

    private String clienteToken() {
        return clienteAuthService.registrar(new ClienteRegistroRequest(
                "Cliente", "Prueba", "cliente." + random(8) + "@example.com", "claveSegura123", null, true)).token();
    }

}
