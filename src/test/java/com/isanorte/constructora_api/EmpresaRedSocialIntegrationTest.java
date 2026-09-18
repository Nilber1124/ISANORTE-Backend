package com.isanorte.constructora_api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.RedSocialRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmpresaRedSocialIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private RedSocialRepository redSocialRepository;
    @Autowired private ConfiguracionSitioRepository configuracionSitioRepository;
    @Autowired private UnidadNegocioRepository unidadNegocioRepository;

    @Test
    void administraRedSocialYLaExponeEnEmpresa() throws Exception {
        UUID empresaId = createEmpresa("crear-red");
        UUID redId = createRed(empresaId, "Instagram", "https://instagram.com/isanorte", "instagram", 0, false);

        mockMvc.perform(get("/api/empresa/{id}", empresaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redesSociales[0].id").value(redId.toString()))
                .andExpect(jsonPath("$.redesSociales[0].activo").value(false))
                .andExpect(jsonPath("$.redesSociales[0].orden").value(0));

        mockMvc.perform(put("/api/empresa/{empresaId}/redes-sociales/{redId}", empresaId, redId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("nombre", "LinkedIn", "url", "https://linkedin.com/company/isanorte",
                        "icono", "linkedin", "orden", 0, "activo", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(redId.toString()))
                .andExpect(jsonPath("$.nombre").value("LinkedIn"))
                .andExpect(jsonPath("$.activo").value(false))
                .andExpect(jsonPath("$.orden").value(0));

        mockMvc.perform(delete("/api/empresa/{empresaId}/redes-sociales/{redId}", empresaId, redId))
                .andExpect(status().isNoContent());
        assertThat(redSocialRepository.existsById(redId)).isFalse();
        mockMvc.perform(get("/api/empresa/{id}", empresaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redesSociales").isEmpty());
    }

    @Test
    void rechazaEmpresaORedInexistenteYPertenenciaAjena() throws Exception {
        UUID empresaA = createEmpresa("empresa-a");
        UUID empresaB = createEmpresa("empresa-b");
        UUID redB = createRed(empresaB, "Facebook", "https://facebook.com/isanorte", null, 1, true);
        UUID noExiste = UUID.randomUUID();

        mockMvc.perform(post("/api/empresa/{id}/redes-sociales", noExiste)
                .contentType(MediaType.APPLICATION_JSON).content(json(redBody("X", "https://x.test", null, 0, true))))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/empresa/{empresaId}/redes-sociales/{redId}", empresaA, noExiste)
                .contentType(MediaType.APPLICATION_JSON).content(json(redBody("X", "https://x.test", null, 0, true))))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/empresa/{empresaId}/redes-sociales/{redId}", empresaA, redB)
                .contentType(MediaType.APPLICATION_JSON).content(json(redBody("X", "https://x.test", null, 0, true))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(delete("/api/empresa/{empresaId}/redes-sociales/{redId}", empresaA, redB))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validaCamposObligatoriosDeRedSocial() throws Exception {
        UUID empresaId = createEmpresa("validacion-red");
        mockMvc.perform(post("/api/empresa/{id}/redes-sociales", empresaId)
                .contentType(MediaType.APPLICATION_JSON).content("{\"icono\":\"x\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postEmpresaConRedesYPutEmpresaConservanAgregados() throws Exception {
        UUID empresaId = createEmpresaConRedInicial("compatibilidad");
        Empresa empresa = empresaRepository.findById(empresaId).orElseThrow();
        ConfiguracionSitio configuracion = ConfiguracionSitio.builder().tituloSitio("ISANORTE").build();
        empresa.setConfiguracionSitio(configuracion);
        empresaRepository.saveAndFlush(empresa);
        UnidadNegocio unidad = UnidadNegocio.builder().nombre("Unidad compatibilidad " + UUID.randomUUID())
                .slug("unidad-" + UUID.randomUUID()).build();
        unidad.setEmpresa(empresa);
        unidad = unidadNegocioRepository.saveAndFlush(unidad);
        UUID unidadId = unidad.getId();

        mockMvc.perform(put("/api/empresa/{id}", empresaId).contentType(MediaType.APPLICATION_JSON)
                .content(json(empresaBody("actualizada-compatibilidad"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreComercial").value("Comercial actualizada-compatibilidad"))
                .andExpect(jsonPath("$.redesSociales.length()").value(1));

        assertThat(configuracionSitioRepository.count()).isPositive();
        assertThat(unidadNegocioRepository.findById(unidadId).orElseThrow().getEmpresa().getId()).isEqualTo(empresaId);
    }

    private UUID createEmpresa(String suffix) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/empresa").contentType(MediaType.APPLICATION_JSON)
                .content(json(empresaBody(suffix))))
                .andExpect(status().isCreated()).andReturn();
        return id(result);
    }

    private UUID createEmpresaConRedInicial(String suffix) throws Exception {
        Map<String, Object> body = empresaBody(suffix);
        body.put("redesSociales", java.util.List.of(redBody("Inicial", "https://inicial.test", "link", 0, false)));
        MvcResult result = mockMvc.perform(post("/api/empresa").contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.redesSociales.length()").value(1)).andReturn();
        return id(result);
    }

    private UUID createRed(UUID empresaId, String nombre, String url, String icono, int orden, boolean activo)
            throws Exception {
        MvcResult result = mockMvc.perform(post("/api/empresa/{id}/redes-sociales", empresaId)
                .contentType(MediaType.APPLICATION_JSON).content(json(redBody(nombre, url, icono, orden, activo))))
                .andExpect(status().isCreated()).andReturn();
        return id(result);
    }

    private UUID id(MvcResult result) throws Exception {
        return UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
    }

    private Map<String, Object> empresaBody(String suffix) {
        return new java.util.LinkedHashMap<>(Map.of("razonSocial", "Razón " + suffix, "nombreComercial",
                "Comercial " + suffix, "ruc", "R" + UUID.randomUUID().toString().replace("-", "").substring(0, 19)));
    }

    private Map<String, Object> redBody(String nombre, String url, String icono, int orden, boolean activo) {
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("nombre", nombre); body.put("url", url); body.put("icono", icono); body.put("orden", orden);
        body.put("activo", activo);
        return body;
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
