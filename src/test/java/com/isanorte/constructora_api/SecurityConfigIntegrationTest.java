package com.isanorte.constructora_api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityConfigIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtEncoder jwtEncoder;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private ConfiguracionSitioRepository configuracionRepository;
    @Autowired private UnidadNegocioRepository unidadRepository;

    @Test
    @DisplayName("Rutas públicas bajo /api/publico/** permiten acceso anónimo sin cabecera Authorization")
    void publicCatalogAllowsAnonymousAccess() throws Exception {
        Empresa company = empresaRepository.saveAndFlush(
                Empresa.builder().razonSocial("Razón Test").nombreComercial("Comercial Test").ruc("R" + random(10)).build());
        ConfiguracionSitio site = configuracionRepository.saveAndFlush(
                ConfiguracionSitio.builder().clave("isanorte-sec-test").tituloSitio("Sitio Test").empresa(company).build());
        company.setConfiguracionSitio(site);
        empresaRepository.saveAndFlush(company);

        unidadRepository.saveAndFlush(
                UnidadNegocio.builder().nombre("ISADECOR").slug("isadecor-sec-test").empresa(company).activo(true).orden(0).build());

        mockMvc.perform(get("/api/publico/sitios/{clave}/unidades/{unidad}/catalogo", "isanorte-sec-test", "isadecor-sec-test"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Actuator health es público y responde 200 sin autenticación")
    void actuatorHealthIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint administrativo /api/categorias deniega acceso anónimo con 401")
    void adminCategoriesDeniesAnonymousAccess() throws Exception {
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Consulta pública de anuncios permite acceso anónimo")
    void publicAnnouncementsAllowAnonymousAccess() throws Exception {
        mockMvc.perform(get("/api/publico/anuncios/ISANORTE"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administración de anuncios deniega acceso anónimo")
    void adminAnnouncementsDenyAnonymousAccess() throws Exception {
        mockMvc.perform(get("/api/anuncios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Administración de anuncios acepta JWT de administrador")
    void adminAnnouncementsAllowAdminAccess() throws Exception {
        Instant now = Instant.now();
        String token = createToken("admin@isanorte.com", Set.of("ADMINISTRADOR"), now, now.plusSeconds(3600));

        mockMvc.perform(get("/api/anuncios").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint /api/categorias/activas bajo /api/** continúa protegido y devuelve 401 si no hay token")
    void activeCategoriesDeniesAnonymousAccess() throws Exception {
        mockMvc.perform(get("/api/categorias/activas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Petición administrativa con token malformado/inválido devuelve 401 Unauthorized")
    void adminRequestWithMalformedTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/categorias")
                .header("Authorization", "Bearer token-invalido-o-malformado"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Petición administrativa con JWT expirado devuelve 401 Unauthorized")
    void adminRequestWithExpiredTokenReturns401() throws Exception {
        Instant past = Instant.now().minusSeconds(7200);
        String expiredToken = createToken("admin@isanorte.com", Set.of("ADMINISTRADOR"), past.minusSeconds(3600), past);

        mockMvc.perform(get("/api/categorias")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Petición administrativa con JWT válido y rol ADMINISTRADOR devuelve 200 OK")
    void adminRequestWithValidAdminTokenReturns200() throws Exception {
        Instant now = Instant.now();
        String validAdminToken = createToken("admin@isanorte.com", Set.of("ADMINISTRADOR"), now, now.plusSeconds(3600));

        mockMvc.perform(get("/api/categorias")
                .header("Authorization", "Bearer " + validAdminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Petición administrativa con JWT válido pero sin rol ADMINISTRADOR devuelve 403 Forbidden")
    void adminRequestWithNonAdminRoleReturns403() throws Exception {
        Instant now = Instant.now();
        String nonAdminToken = createToken("user@isanorte.com", Set.of("CLIENTE"), now, now.plusSeconds(3600));

        mockMvc.perform(get("/api/categorias")
                .header("Authorization", "Bearer " + nonAdminToken))
                .andExpect(status().isForbidden());
    }

    private String createToken(String subject, Set<String> roles, Instant issuedAt, Instant expiresAt) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("isanorte-api")
                .subject(subject)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("roles", roles)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private String random(int length) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, length);
    }
}
