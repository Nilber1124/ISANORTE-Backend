package com.isanorte.constructora_api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CuentaClienteIntegrationTest {

    private static final String PASSWORD = "claveSegura123";

    @Autowired private MockMvc mockMvc;

    @Test
    @DisplayName("Registro crea la cuenta, normaliza el correo y devuelve token de cliente")
    void registroDevuelveTokenDeCliente() throws Exception {
        String email = "Cliente." + random() + "@Mail.com";

        mockMvc.perform(post("/api/publico/cuenta/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.cliente.email").value(email.toLowerCase()));
    }

    @Test
    @DisplayName("Registrar un correo ya existente responde 409")
    void registroDuplicadoResponde409() throws Exception {
        String email = "dup." + random() + "@mail.com";
        registrar(email);

        mockMvc.perform(post("/api/publico/cuenta/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson(email)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Registro sin aceptar el tratamiento de datos responde 400")
    void registroSinConsentimientoResponde400() throws Exception {
        String json = """
                {"nombre":"Ana","email":"sin.consent.%s@mail.com","password":"%s","aceptaTratamientoDatos":false}
                """.formatted(random(), PASSWORD);

        mockMvc.perform(post("/api/publico/cuenta/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login con credenciales correctas devuelve token y login incorrecto responde 401")
    void loginValidaCredenciales() throws Exception {
        String email = "login." + random() + "@mail.com";
        registrar(email);

        mockMvc.perform(post("/api/publico/cuenta/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        mockMvc.perform(post("/api/publico/cuenta/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"otraClave999\"}".formatted(email)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("/api/publico/cuenta/me exige token de cliente y lo devuelve con el token válido")
    void meRequiereSesionDeCliente() throws Exception {
        String email = "me." + random() + "@mail.com";
        String token = registrar(email);

        mockMvc.perform(get("/api/publico/cuenta/me"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/publico/cuenta/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email.toLowerCase()));
    }

    @Test
    @DisplayName("Cotizar y comparar precios sin sesión de cliente responde 401")
    void accionesGatedRequierenCuenta() throws Exception {
        mockMvc.perform(post("/api/publico/sitios/isanorte/unidades/isadecor/cotizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/publico/sitios/isanorte/unidades/isadecor/productos/x/comparar-precio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/publico/sitios/isanorte/unidades/isadecor/productos/x/comparacion-competidores"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Un token de cliente no puede acceder a endpoints administrativos")
    void tokenClienteNoAccedeAAdministracion() throws Exception {
        String token = registrar("admin." + random() + "@mail.com");

        mockMvc.perform(get("/api/categorias").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("El carrito de una cuenta nueva está vacío y se guarda ligado al cliente")
    void carritoSeGuardaPorCliente() throws Exception {
        String token = registrar("carrito." + random() + "@mail.com");

        mockMvc.perform(get("/api/publico/cuenta/carrito").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0));

        String item = """
                {"productoSlug":"piso-spc","productoNombre":"Piso SPC","productoSku":"SPC-01",
                 "variante":null,"cantidad":2,"precioUnitario":45.50}
                """;
        String body = "{\"items\":[" + item + "," + item + "]}";

        mockMvc.perform(put("/api/publico/cuenta/carrito").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].cantidad").value(4));

        mockMvc.perform(get("/api/publico/cuenta/carrito").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productoSlug").value("piso-spc"))
                .andExpect(jsonPath("$.items[0].cantidad").value(4));
    }

    @Test
    @DisplayName("El carrito de cuenta rechaza peticiones sin sesión de cliente")
    void carritoRequiereSesion() throws Exception {
        mockMvc.perform(get("/api/publico/cuenta/carrito"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/publico/cuenta/carrito")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"items\":[]}"))
                .andExpect(status().isUnauthorized());
    }

    private String registrar(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/publico/cuenta/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson(email)))
                .andExpect(status().isCreated())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        int start = body.indexOf("\"token\":\"") + 9;
        return body.substring(start, body.indexOf('"', start));
    }

    private static String registroJson(String email) {
        return """
                {"nombre":"Ana","apellido":"Quispe","email":"%s","password":"%s",
                 "telefono":"999888777","aceptaTratamientoDatos":true}
                """.formatted(email, PASSWORD);
    }

    private static String random() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
