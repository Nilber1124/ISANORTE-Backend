package com.isanorte.constructora_api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class VentaApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private UnidadNegocioRepository unidadRepository;
    @Autowired private ProductoRepository productoRepository;

    private Producto producto(int stock) {
        Empresa empresa = empresaRepository.saveAndFlush(Empresa.builder()
                .razonSocial("Razón " + random(6)).nombreComercial("Comercial " + random(6)).ruc("R" + random(19)).build());
        UnidadNegocio unidad = unidadRepository.saveAndFlush(UnidadNegocio.builder()
                .nombre("Unidad " + random(6)).slug("unidad-" + random(6)).empresa(empresa).activo(true).orden(0).build());
        Producto producto = Producto.builder()
                .sku("SKU-" + random(6)).nombre("Panel " + random(4)).slug("panel-" + random(6)).descripcion("Panel de prueba")
                .disponibilidad(EstadoDisponibilidad.DISPONIBLE).estado(EstadoPublicacion.PUBLICADO)
                .precioBase(new BigDecimal("12.50")).stockActual(stock).build();
        unidad.addProducto(producto);
        return productoRepository.saveAndFlush(producto);
    }

    private Map<String, Object> venta(List<Map<String, Object>> detalles) {
        return Map.of("canal", "WHATSAPP", "fechaVenta", "2026-11-22", "clienteNombre", "Ana Pérez", "clienteTelefono", "999111222",
                "tipoDocumento", "DNI", "documento", "12345678", "metodoPago", "YAPE", "pagado", true,
                "notas", "Pidió por WhatsApp", "detalles", detalles);
    }

    private Map<String, Object> linea(UUID productoId, String nombre, int cantidad, String precio) {
        Map<String, Object> linea = new java.util.HashMap<>();
        linea.put("productoId", productoId);
        linea.put("nombre", nombre);
        linea.put("cantidad", cantidad);
        linea.put("precioUnitario", new BigDecimal(precio));
        return linea;
    }

    private MvcResult registrar(Map<String, Object> body, int status) throws Exception {
        return mockMvc.perform(post("/api/ventas").with(jwt().authorities(() -> "ROLE_ADMINISTRADOR"))
                .contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().is(status)).andReturn();
    }

    @Test
    void registraLaVentaDescuentaElStockYDejaSuMovimiento() throws Exception {
        Producto panel = producto(10);

        MvcResult creada = registrar(venta(List.of(linea(panel.getId(), "Panel", 3, "12.50"), linea(null, "Instalación", 1, "80"))), 201);

        String respuesta = creada.getResponse().getContentAsString();
        assertEquals(0, new BigDecimal("117.50").compareTo(new BigDecimal(objectMapper.readTree(respuesta).get("total").asText())));
        mockMvcGet("/api/ventas").andExpect(jsonPath("$[0].canal").value("WHATSAPP"))
                .andExpect(jsonPath("$[0].stockDescontado").value(true))
                .andExpect(jsonPath("$[0].detalles.length()").value(2));
        assertEquals(7, productoRepository.findById(panel.getId()).orElseThrow().getStockActual());
        mockMvcGet("/api/productos/" + panel.getId() + "/movimientos-stock")
                .andExpect(jsonPath("$[0].tipo").value("SALIDA"))
                .andExpect(jsonPath("$[0].cantidad").value(3));
    }

    @Test
    void anularDevuelveElStockYReactivarLoDescuentaOtraVez() throws Exception {
        Producto panel = producto(10);
        MvcResult creada = registrar(venta(List.of(linea(panel.getId(), "Panel", 4, "12.50"))), 201);
        String id = objectMapper.readTree(creada.getResponse().getContentAsString()).get("id").asText();
        assertEquals(6, productoRepository.findById(panel.getId()).orElseThrow().getStockActual());

        patchAnulada(id, true).andExpect(jsonPath("$.anulada").value(true)).andExpect(jsonPath("$.stockDescontado").value(false));
        assertEquals(10, productoRepository.findById(panel.getId()).orElseThrow().getStockActual());

        patchAnulada(id, false).andExpect(jsonPath("$.anulada").value(false)).andExpect(jsonPath("$.stockDescontado").value(true));
        assertEquals(6, productoRepository.findById(panel.getId()).orElseThrow().getStockActual());
    }

    @Test
    void rechazaLoInvalidoYNoDejaNadaGuardado() throws Exception {
        Producto panel = producto(2);
        // Más de lo que hay: stock insuficiente, la venta no se guarda.
        registrar(venta(List.of(linea(panel.getId(), "Panel", 5, "12.50"))), 400);
        assertEquals(2, productoRepository.findById(panel.getId()).orElseThrow().getStockActual());
        // Sin líneas.
        registrar(venta(List.of()), 400);
        // DNI mal escrito.
        Map<String, Object> dniMalo = new java.util.HashMap<>(venta(List.of(linea(null, "Algo", 1, "10"))));
        dniMalo.put("documento", "123");
        registrar(dniMalo, 400);
        mockMvcGet("/api/ventas").andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void soloElAdministradorPuedeVerYRegistrarVentas() throws Exception {
        mockMvc.perform(post("/api/ventas").contentType(MediaType.APPLICATION_JSON)
                .content(json(venta(List.of(linea(null, "Algo", 1, "10"))))))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/ventas")).andExpect(status().isUnauthorized());
        mockMvcGet("/api/ventas/" + UUID.randomUUID()).andExpect(status().isNotFound());
    }

    private org.springframework.test.web.servlet.ResultActions mockMvcGet(String url) throws Exception {
        return mockMvc.perform(get(url).with(jwt().authorities(() -> "ROLE_ADMINISTRADOR")));
    }

    private org.springframework.test.web.servlet.ResultActions patchAnulada(String id, boolean anulada) throws Exception {
        return mockMvc.perform(patch("/api/ventas/{id}/anulada", id).with(jwt().authorities(() -> "ROLE_ADMINISTRADOR"))
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("anulada", anulada))))
                .andExpect(status().isOk());
    }

    private String json(Object value) throws Exception { return objectMapper.writeValueAsString(value); }
    private String random(int length) { return UUID.randomUUID().toString().replace("-", "").substring(0, length); }
}
