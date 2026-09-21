package com.isanorte.constructora_api;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
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

import com.isanorte.constructora_api.enums.TipoPaginaSeo;
import com.isanorte.constructora_api.enums.TipoPaginaPublica;
import com.isanorte.constructora_api.enums.TipoSeccionLanding;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.ContenidoPagina;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.EstadisticaEmpresa;
import com.isanorte.constructora_api.model.HeroScene;
import com.isanorte.constructora_api.model.Proyecto;
import com.isanorte.constructora_api.model.RedSocial;
import com.isanorte.constructora_api.model.SeccionLanding;
import com.isanorte.constructora_api.model.SeoPagina;
import com.isanorte.constructora_api.model.Servicio;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.ContenidoPaginaRepository;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.EstadisticaEmpresaRepository;
import com.isanorte.constructora_api.repository.ProyectoRepository;
import com.isanorte.constructora_api.repository.RedSocialRepository;
import com.isanorte.constructora_api.repository.SeccionLandingRepository;
import com.isanorte.constructora_api.repository.SeoPaginaRepository;
import com.isanorte.constructora_api.repository.ServicioRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DynamicContentApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private EstadisticaEmpresaRepository estadisticaEmpresaRepository;
    @Autowired private ConfiguracionSitioRepository configuracionRepository;
    @Autowired private SeccionLandingRepository seccionRepository;
    @Autowired private ServicioRepository servicioRepository;
    @Autowired private ProyectoRepository proyectoRepository;
    @Autowired private UnidadNegocioRepository unidadRepository;
    @Autowired private RedSocialRepository redSocialRepository;
    @Autowired private ContenidoPaginaRepository contenidoRepository;
    @Autowired private SeoPaginaRepository seoRepository;

    @Test
    void exponeClaveYCamposSimplesEnContratosExistentes() throws Exception {
        Empresa company = company("campos");
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("empresaId", company.getId()); config.put("clave", "sitio-campos");
        config.put("tituloSitio", "Sitio");
        config.put("secciones", List.of(Map.of("tipo", "HERO", "etiqueta", "Arquitectura",
                "imagenAlt", "Fachada", "orden", 0, "visible", false)));
        mockMvc.perform(post("/api/configuracion-sitio").contentType(MediaType.APPLICATION_JSON).content(json(config)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.clave").value("sitio-campos"))
                .andExpect(jsonPath("$.secciones[0].etiqueta").value("Arquitectura"))
                .andExpect(jsonPath("$.secciones[0].imagenAlt").value("Fachada"))
                .andExpect(jsonPath("$.secciones[0].orden").value(0))
                .andExpect(jsonPath("$.secciones[0].visible").value(false));
    }

    @Test
    void claveMantieneCompatibilidadFallbackEsEditableYUnica() throws Exception {
        Empresa legacy = company("legacy-key");
        Map<String, Object> createLegacy = new LinkedHashMap<>();
        createLegacy.put("empresaId", legacy.getId());
        createLegacy.put("tituloSitio", "Sitio legado");
        UUID legacyId = id(mockMvc.perform(post("/api/configuracion-sitio")
                .contentType(MediaType.APPLICATION_JSON).content(json(createLegacy)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clave").value("site-" + legacy.getId())).andReturn());

        mockMvc.perform(put("/api/configuracion-sitio/{id}", legacyId)
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("tituloSitio", "Sin cambiar clave"))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.clave").value("site-" + legacy.getId()));
        mockMvc.perform(put("/api/configuracion-sitio/{id}", legacyId)
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("clave", "sitio-canonico"))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.clave").value("sitio-canonico"));

        ConfiguracionSitio occupied = site("occupied-key");
        mockMvc.perform(put("/api/configuracion-sitio/{id}", legacyId)
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("clave", occupied.getClave()))))
                .andExpect(status().isConflict());
    }

    @Test
    void crudHeroSceneValidaOwnershipYTipoHero() throws Exception {
        ConfiguracionSitio site = site("scene");
        SeccionLanding hero = section(site, TipoSeccionLanding.HERO, 0, true);
        SeccionLanding otherHero = section(site("scene-other"), TipoSeccionLanding.HERO, 0, true);
        SeccionLanding services = section(site, TipoSeccionLanding.SERVICIOS, 1, true);
        Map<String, Object> body = Map.of("imagenUrl", "/hero.webp", "alt", "Hero", "orden", 0, "activo", true);
        UUID id = id(mockMvc.perform(post("/api/secciones-landing/{id}/escenas", hero.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.orden").value(0)).andReturn());
        mockMvc.perform(put("/api/secciones-landing/{id}/escenas/{child}", hero.getId(), id)
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                        "imagenUrl", "/hero-2.webp", "alt", "Nuevo", "orden", 1, "activo", false))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.activo").value(false));
        mockMvc.perform(put("/api/secciones-landing/{id}/escenas/{child}", otherHero.getId(), id)
                .contentType(MediaType.APPLICATION_JSON).content(json(body))).andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/secciones-landing/{id}/escenas", services.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(body))).andExpect(status().isBadRequest());
        mockMvc.perform(delete("/api/secciones-landing/{id}/escenas/{child}", hero.getId(), id))
                .andExpect(status().isNoContent());
    }

    @Test
    void crudAccionRechazaEnlacePeligrosoYOwnership() throws Exception {
        SeccionLanding hero = section(site("action"), TipoSeccionLanding.HERO, 0, true);
        SeccionLanding other = section(site("action-other"), TipoSeccionLanding.HERO, 0, true);
        Map<String, Object> body = Map.of("texto", "Ver", "enlace", "/nosotros", "orden", 0, "activo", true);
        UUID id = id(mockMvc.perform(post("/api/secciones-landing/{id}/acciones", hero.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(body))).andExpect(status().isCreated()).andReturn());
        mockMvc.perform(put("/api/secciones-landing/{id}/acciones/{child}", hero.getId(), id)
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                        "texto", "Conocer", "enlace", "https://isanorte.com/contacto",
                        "orden", 1, "activo", false))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.texto").value("Conocer"))
                .andExpect(jsonPath("$.activo").value(false));
        mockMvc.perform(post("/api/secciones-landing/{id}/acciones", hero.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                        "texto", "X", "enlace", "javascript:alert(1)", "orden", 1, "activo", true))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/api/secciones-landing/{id}/acciones/{child}", other.getId(), id)
                .contentType(MediaType.APPLICATION_JSON).content(json(body))).andExpect(status().isBadRequest());
        mockMvc.perform(delete("/api/secciones-landing/{id}/acciones/{child}", hero.getId(), id))
                .andExpect(status().isNoContent());
    }

    @Test
    void accionAceptaEsquemasPermitidosYAplicaLimitesActivos() throws Exception {
        SeccionLanding services = section(site("action-links"), TipoSeccionLanding.SERVICIOS, 0, true);
        for (String link : List.of("/servicios", "#contacto", "https://isanorte.com", "mailto:ventas@isanorte.com",
                "tel:+51999999999")) {
            mockMvc.perform(post("/api/secciones-landing/{id}/acciones", services.getId())
                    .contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                            "texto", "Abrir", "enlace", link, "orden", 0, "activo", false))))
                    .andExpect(status().isCreated());
        }
        for (String link : List.of("//example.com", "data:text/html,x", "vbscript:msgbox(1)", "https://")) {
            mockMvc.perform(post("/api/secciones-landing/{id}/acciones", services.getId())
                    .contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                            "texto", "Abrir", "enlace", link, "orden", 0, "activo", false))))
                    .andExpect(status().isBadRequest());
        }

        SeccionLanding cta = section(site("action-cta"), TipoSeccionLanding.CTA, 0, true);
        Map<String, Object> active = Map.of("texto", "Primaria", "enlace", "/contacto", "orden", 0, "activo", true);
        mockMvc.perform(post("/api/secciones-landing/{id}/acciones", cta.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(active))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/secciones-landing/{id}/acciones", cta.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(active))).andExpect(status().isBadRequest());
    }

    @Test
    void respuestasLandingAnidanHijosEnOrdenDeterminista() throws Exception {
        SeccionLanding hero = section(site("nested-order"), TipoSeccionLanding.HERO, 0, true);
        Map<String, Object> sceneLate = Map.of("imagenUrl", "/late.webp", "orden", 2, "activo", true);
        Map<String, Object> sceneFirst = Map.of("imagenUrl", "/first.webp", "orden", 0, "activo", true);
        mockMvc.perform(post("/api/secciones-landing/{id}/escenas", hero.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(sceneLate))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/secciones-landing/{id}/escenas", hero.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(sceneFirst))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/secciones-landing/{id}/acciones", hero.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                        "texto", "Segunda", "enlace", "/dos", "orden", 2, "activo", true))))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/secciones-landing/{id}/acciones", hero.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                        "texto", "Primera", "enlace", "/uno", "orden", 0, "activo", true))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/secciones-landing/{id}", hero.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.escenas[0].imagenUrl").value("/first.webp"))
                .andExpect(jsonPath("$.acciones[0].texto").value("Primera"));
    }

    @Test
    void crudBeneficioValidaOwnership() throws Exception {
        Servicio service = service("benefit-a", true, true, 0);
        Servicio other = service("benefit-b", true, false, 1);
        Map<String, Object> body = Map.of("texto", "Garantía", "orden", 0, "activo", true);
        UUID id = id(mockMvc.perform(post("/api/servicios/{id}/beneficios", service.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(body))).andExpect(status().isCreated()).andReturn());
        mockMvc.perform(put("/api/servicios/{id}/beneficios/{child}", service.getId(), id)
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                        "texto", "Garantía ampliada", "orden", 1, "activo", false))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.activo").value(false));
        mockMvc.perform(delete("/api/servicios/{id}/beneficios/{child}", other.getId(), id))
                .andExpect(status().isBadRequest());
        mockMvc.perform(delete("/api/servicios/{id}/beneficios/{child}", service.getId(), id))
                .andExpect(status().isNoContent());
    }

    @Test
    void crudRecursoValidaAltEditorialYOwnership() throws Exception {
        Empresa company = company("resource");
        UnidadNegocio unit = unit(company, "resource-a", true, false, 0);
        UnidadNegocio other = unit(company, "resource-b", true, false, 1);
        Map<String, Object> valid = Map.of("tipo", "IMAGEN_EDITORIAL", "url", "/editorial.webp",
                "alt", "Sala terminada", "etiqueta", "Galería", "orden", 0, "activo", true);
        UUID id = id(mockMvc.perform(post("/api/unidades-negocio/{id}/recursos", unit.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(valid))).andExpect(status().isCreated()).andReturn());
        mockMvc.perform(post("/api/unidades-negocio/{id}/recursos", unit.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                        "tipo", "IMAGEN_EDITORIAL", "url", "/sin-alt.webp", "orden", 1, "activo", true))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/api/unidades-negocio/{id}/recursos/{child}", unit.getId(), id)
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                        "tipo", "CATALOGO", "url", "/catalogo.pdf", "etiqueta", "Descargar",
                        "orden", 1, "activo", false))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.tipo").value("CATALOGO"))
                .andExpect(jsonPath("$.activo").value(false));
        mockMvc.perform(delete("/api/unidades-negocio/{id}/recursos/{child}", other.getId(), id))
                .andExpect(status().isBadRequest());
        mockMvc.perform(delete("/api/unidades-negocio/{id}/recursos/{child}", unit.getId(), id))
                .andExpect(status().isNoContent());
    }

    @Test
    void crudEstadisticaValidaOwnership() throws Exception {
        Empresa company = company("stats-a");
        Empresa other = company("stats-b");
        Map<String, Object> body = Map.of("valor", 120, "prefijo", "+", "etiqueta", "Proyectos",
                "orden", 0, "activo", false);
        UUID id = id(mockMvc.perform(post("/api/empresa/{id}/estadisticas", company.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.activo").value(false)).andReturn());
        mockMvc.perform(put("/api/empresa/{id}/estadisticas/{child}", company.getId(), id)
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                        "valor", 121, "etiqueta", "Proyectos", "orden", 1, "activo", true))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.valor").value(121));
        mockMvc.perform(delete("/api/empresa/{id}/estadisticas/{child}", other.getId(), id))
                .andExpect(status().isBadRequest());
        mockMvc.perform(delete("/api/empresa/{id}/estadisticas/{child}", company.getId(), id))
                .andExpect(status().isNoContent());
    }

    @Test
    void contenidoPaginaCreaActualizaPreservaTagsYRechazaDuplicado() throws Exception {
        ConfiguracionSitio site = site("content-api");
        Map<String, Object> create = new LinkedHashMap<>();
        create.put("configuracionSitioId", site.getId()); create.put("pagina", "NOSOTROS");
        create.put("titulo", "Nosotros"); create.put("activo", true);
        create.put("tags", List.of("Arquitectura", "Construcción"));
        UUID id = id(mockMvc.perform(post("/api/contenidos-pagina").contentType(MediaType.APPLICATION_JSON)
                .content(json(create))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.tags[0]").value("Arquitectura")).andReturn());
        mockMvc.perform(put("/api/contenidos-pagina/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("titulo", "Quiénes somos", "activo", false,
                        "tags", List.of("Obra Civil", "Acabados")))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.pagina").value("NOSOTROS"))
                .andExpect(jsonPath("$.configuracionSitioId").value(site.getId().toString()))
                .andExpect(jsonPath("$.tags[0]").value("Obra Civil"));
        mockMvc.perform(post("/api/contenidos-pagina").contentType(MediaType.APPLICATION_JSON).content(json(create)))
                .andExpect(status().isConflict());

        Map<String, Object> clearTags = new LinkedHashMap<>();
        clearTags.put("titulo", "Sin etiquetas"); clearTags.put("activo", true); clearTags.put("tags", null);
        mockMvc.perform(put("/api/contenidos-pagina/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(json(clearTags))).andExpect(status().isOk()).andExpect(jsonPath("$.tags.length()").value(0));
    }

    @Test
    void seoValidaTiposCrossSiteDuplicadosYActualizaSinReparent() throws Exception {
        ConfiguracionSitio site = site("seo-api");
        ConfiguracionSitio otherSite = site("seo-other");
        UnidadNegocio unit = unit(site.getEmpresa(), "seo-unit", true, false, 0);
        UnidadNegocio foreign = unit(otherSite.getEmpresa(), "seo-foreign", true, false, 0);
        Map<String, Object> home = seoBody(site.getId(), "HOME", null);
        UUID id = id(mockMvc.perform(post("/api/seo-paginas").contentType(MediaType.APPLICATION_JSON).content(json(home)))
                .andExpect(status().isCreated()).andReturn());
        mockMvc.perform(post("/api/seo-paginas").contentType(MediaType.APPLICATION_JSON).content(json(home)))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/seo-paginas").contentType(MediaType.APPLICATION_JSON)
                .content(json(seoBody(site.getId(), "UNIDAD_NEGOCIO", null)))).andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/seo-paginas").contentType(MediaType.APPLICATION_JSON)
                .content(json(seoBody(site.getId(), "NOSOTROS", unit.getId())))).andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/seo-paginas").contentType(MediaType.APPLICATION_JSON)
                .content(json(seoBody(site.getId(), "UNIDAD_NEGOCIO", foreign.getId())))).andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/seo-paginas").contentType(MediaType.APPLICATION_JSON)
                .content(json(seoBody(site.getId(), "UNIDAD_NEGOCIO", unit.getId())))).andExpect(status().isCreated());
        mockMvc.perform(put("/api/seo-paginas/{id}", id).contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                "title", "Home nueva", "description", "Nueva descripción", "robots", "NOINDEX_FOLLOW"))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.tipoPagina").value("HOME"))
                .andExpect(jsonPath("$.robots").value("NOINDEX_FOLLOW"));
    }

    @Test
    void contactoPublicoYBandejaAdministrativa() throws Exception {
        Map<String, Object> body = Map.of("nombre", "Ana", "email", "ana@example.com", "telefono", "999999999",
                "mensaje", "Necesito información");
        UUID id = id(mockMvc.perform(post("/api/publico/contacto").contentType(MediaType.APPLICATION_JSON)
                .content(json(body))).andExpect(status().isCreated()).andExpect(jsonPath("$.estado").value("NUEVA"))
                .andExpect(jsonPath("$.email").doesNotExist()).andReturn());
        mockMvc.perform(post("/api/publico/contacto").contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("nombre", "Ana", "email", "incorrecto", "telefono", "1", "mensaje", "X"))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/publico/contacto").contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("nombre", "Ana", "email", "ana@example.com", "telefono", "1"))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/solicitudes-contacto").param("estado", "NUEVA"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(id.toString()));
        mockMvc.perform(get("/api/solicitudes-contacto"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(id.toString()));
        mockMvc.perform(get("/api/solicitudes-contacto/{id}", id)).andExpect(status().isOk());
        mockMvc.perform(patch("/api/solicitudes-contacto/{id}/estado", id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"RESPONDIDA\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("RESPONDIDA"));
        mockMvc.perform(get("/api/solicitudes-contacto/{id}", UUID.randomUUID())).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/solicitudes-contacto").param("estado", "DESCONOCIDA"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void sitioPublicoFiltraDatosYNoExponeRucNiScripts() throws Exception {
        ConfiguracionSitio site = site("public-site");
        site.setScriptsHead("secret"); configuracionRepository.saveAndFlush(site);
        social(site.getEmpresa(), "Segunda", true, 2); social(site.getEmpresa(), "Primera", true, 0);
        social(site.getEmpresa(), "Oculta", false, 0);
        unit(site.getEmpresa(), "unit-second", true, false, 2);
        unit(site.getEmpresa(), "unit-active", true, false, 0);
        unit(site.getEmpresa(), "unit-hidden", false, false, 0);
        mockMvc.perform(get("/api/publico/sitios/{key}", site.getClave()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.redes.length()").value(2))
                .andExpect(jsonPath("$.redes[0].nombre").value("Primera"))
                .andExpect(jsonPath("$.unidades.length()").value(2))
                .andExpect(jsonPath("$.unidades[0].slug").value("unit-active"))
                .andExpect(jsonPath("$.empresa.ruc").doesNotExist())
                .andExpect(jsonPath("$.empresa.mision").doesNotExist())
                .andExpect(jsonPath("$.scriptsHead").doesNotExist());
        mockMvc.perform(get("/api/publico/sitios/no-existe")).andExpect(status().isNotFound());
    }

    @Test
    void homePublicaFiltraPorSitioVisibilidadYEstados() throws Exception {
        ConfiguracionSitio site = site("home-public");
        ConfiguracionSitio other = site("home-other");
        SeccionLanding visible = section(site, TipoSeccionLanding.HERO, 0, true);
        section(site, TipoSeccionLanding.SERVICIOS, 1, false);
        section(other, TipoSeccionLanding.HERO, 0, true);
        visible.addEscena(HeroScene.builder().imagenUrl("/active.webp").orden(0).activo(true).build());
        visible.addEscena(HeroScene.builder().imagenUrl("/hidden.webp").orden(1).activo(false).build());
        seccionRepository.saveAndFlush(visible);
        mockMvc.perform(post("/api/secciones-landing/{id}/acciones", visible.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                        "texto", "Activa", "enlace", "/contacto", "orden", 0, "activo", true))))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/secciones-landing/{id}/acciones", visible.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                        "texto", "Oculta", "enlace", "/oculta", "orden", 1, "activo", false))))
                .andExpect(status().isCreated());
        service("home-active", true, true, 0); service("home-hidden", true, false, 1);
        project("project-active", true, true, 0); project("project-hidden", false, true, 1);
        UnidadNegocio highlighted = unit(site.getEmpresa(), "highlighted", true, true, 0);
        mockMvc.perform(post("/api/unidades-negocio/{id}/recursos", highlighted.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                        "tipo", "IMAGEN_FONDO", "url", "/active.webp", "orden", 0, "activo", true))))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/unidades-negocio/{id}/recursos", highlighted.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of(
                        "tipo", "IMAGEN_FONDO", "url", "/hidden.webp", "orden", 1, "activo", false))))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/publico/sitios/{key}/home", site.getClave()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.secciones.length()").value(1))
                .andExpect(jsonPath("$.secciones[0].orden").value(0))
                .andExpect(jsonPath("$.secciones[0].escenas.length()").value(1))
                .andExpect(jsonPath("$.secciones[0].acciones.length()").value(1))
                .andExpect(jsonPath("$.servicios.length()").value(1))
                .andExpect(jsonPath("$.proyectos.length()").value(1))
                .andExpect(jsonPath("$.unidadDestacada.slug").value("highlighted"))
                .andExpect(jsonPath("$.unidadDestacada.recursos.length()").value(1));
    }

    @Test
    void paginaYUnidadPublicasRespetanSitioActividadYSeo() throws Exception {
        ConfiguracionSitio site = site("page-public");
        ConfiguracionSitio other = site("page-other");
        ContenidoPagina content = ContenidoPagina.builder().pagina(com.isanorte.constructora_api.enums.TipoPaginaPublica.NOSOTROS)
                .titulo("Nosotros").activo(true).configuracionSitio(site).tags(List.of("Arquitectura")).build();
        contenidoRepository.saveAndFlush(content);
        seoRepository.saveAndFlush(SeoPagina.builder().tipoPagina(TipoPaginaSeo.NOSOTROS).title("Nosotros SEO")
                .description("Descripción").configuracionSitio(site).build());
        UnidadNegocio unit = unit(site.getEmpresa(), "public-unit", true, false, 0);
        seoRepository.saveAndFlush(SeoPagina.builder().tipoPagina(TipoPaginaSeo.UNIDAD_NEGOCIO).title("Unidad SEO")
                .description("Descripción").configuracionSitio(site).unidadNegocio(unit).build());
        UnidadNegocio foreign = unit(other.getEmpresa(), "foreign-unit", true, false, 0);
        UnidadNegocio inactive = unit(site.getEmpresa(), "inactive-unit", false, false, 1);
        mockMvc.perform(get("/api/publico/sitios/{key}/paginas/NOSOTROS", site.getClave()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.contenido.titulo").value("Nosotros"))
                .andExpect(jsonPath("$.seo.title").value("Nosotros SEO"));
        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{slug}", site.getClave(), unit.getSlug()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.seo.title").value("Unidad SEO"));
        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{slug}", site.getClave(), foreign.getSlug()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{slug}", site.getClave(), inactive.getSlug()))
                .andExpect(status().isNotFound());
        content.setActivo(false); contenidoRepository.saveAndFlush(content);
        mockMvc.perform(get("/api/publico/sitios/{key}/paginas/NOSOTROS", site.getClave()))
                .andExpect(status().isNotFound());
    }

    @Test
    void paginaNosotrosPublicaExponeEmpresaDelSitioYEstadisticasActivasOrdenadas() throws Exception {
        ConfiguracionSitio site = site("about-company");
        ConfiguracionSitio other = site("about-company-other");
        Empresa company = site.getEmpresa();
        company.setMision("Misión pública");
        company.setVision("Visión pública");
        company.setValores("Valores públicos");
        empresaRepository.saveAndFlush(company);
        contenidoRepository.saveAndFlush(ContenidoPagina.builder().pagina(TipoPaginaPublica.NOSOTROS)
                .titulo("Nosotros").activo(true).configuracionSitio(site).build());
        statistic(company, BigDecimal.ZERO, null, null, "CERO", 0, true);
        statistic(company, new BigDecimal("15.50"), "+", "años", "EXPERIENCIA", 1, true);
        statistic(company, new BigDecimal("98"), null, "%", "SATISFACCIÓN", 2, true);
        statistic(company, new BigDecimal("100"), null, "%", "DISEÑO", 3, true);
        statistic(company, new BigDecimal("999"), null, null, "INACTIVA", 0, false);
        statistic(other.getEmpresa(), new BigDecimal("777"), null, null, "AJENA", 0, true);

        mockMvc.perform(get("/api/publico/sitios/{key}/paginas/NOSOTROS", site.getClave()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empresa.mision").value("Misión pública"))
                .andExpect(jsonPath("$.empresa.vision").value("Visión pública"))
                .andExpect(jsonPath("$.empresa.valores").value("Valores públicos"))
                .andExpect(jsonPath("$.empresa.estadisticas.length()").value(4))
                .andExpect(jsonPath("$.empresa.estadisticas[0].valor").value(0))
                .andExpect(jsonPath("$.empresa.estadisticas[0].prefijo").value(nullValue()))
                .andExpect(jsonPath("$.empresa.estadisticas[0].sufijo").value(nullValue()))
                .andExpect(jsonPath("$.empresa.estadisticas[0].etiqueta").value("CERO"))
                .andExpect(jsonPath("$.empresa.estadisticas[0].orden").value(0))
                .andExpect(jsonPath("$.empresa.estadisticas[1].valor").value(15.50))
                .andExpect(jsonPath("$.empresa.estadisticas[3].etiqueta").value("DISEÑO"))
                .andExpect(jsonPath("$.empresa.estadisticas[0].id").doesNotExist())
                .andExpect(jsonPath("$.empresa.estadisticas[0].activo").doesNotExist());
    }

    @Test
    void otrasPaginasPublicasNoRecibenElAgregadoCorporativoDeNosotros() throws Exception {
        ConfiguracionSitio site = site("page-without-company");
        for (TipoPaginaPublica page : List.of(
                TipoPaginaPublica.SERVICIOS, TipoPaginaPublica.PROYECTOS, TipoPaginaPublica.CONTACTO)) {
            contenidoRepository.saveAndFlush(ContenidoPagina.builder().pagina(page).titulo(page.name())
                    .activo(true).configuracionSitio(site).build());
            mockMvc.perform(get("/api/publico/sitios/{key}/paginas/{page}", site.getClave(), page.name()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contenido.pagina").value(page.name()))
                    .andExpect(jsonPath("$.empresa").value(nullValue()));
        }
    }

    private Empresa company(String seed) {
        return empresaRepository.saveAndFlush(Empresa.builder().razonSocial("Razón " + seed)
                .nombreComercial("Comercial " + seed).ruc("R" + random(19)).build());
    }

    private ConfiguracionSitio site(String seed) {
        Empresa company = company(seed);
        ConfiguracionSitio site = ConfiguracionSitio.builder().clave("site-" + seed + "-" + random(6))
                .tituloSitio("Sitio " + seed).empresa(company).build();
        company.setConfiguracionSitio(site);
        return configuracionRepository.saveAndFlush(site);
    }

    private SeccionLanding section(ConfiguracionSitio site, TipoSeccionLanding type, int order, boolean visible) {
        return seccionRepository.saveAndFlush(SeccionLanding.builder().configuracionSitio(site).tipo(type)
                .titulo(type.name()).orden(order).visible(visible).build());
    }

    private Servicio service(String seed, boolean active, boolean featured, int order) {
        return servicioRepository.saveAndFlush(Servicio.builder().nombre(seed).slug(seed + "-" + random(5))
                .descripcion("Descripción").activo(active).destacado(featured).orden(order).build());
    }

    private Proyecto project(String seed, boolean active, boolean featured, int order) {
        return proyectoRepository.saveAndFlush(Proyecto.builder().nombre(seed).slug(seed + "-" + random(5))
                .descripcion("Descripción").activo(active).destacado(featured).orden(order).build());
    }

    private UnidadNegocio unit(Empresa company, String slug, boolean active, boolean featured, int order) {
        return unidadRepository.saveAndFlush(UnidadNegocio.builder().nombre(slug).slug(slug)
                .empresa(company).activo(active).destacado(featured).orden(order).build());
    }

    private void social(Empresa company, String name, boolean active, int order) {
        redSocialRepository.saveAndFlush(RedSocial.builder().nombre(name).url("https://example.com/" + random(5))
                .empresa(company).activo(active).orden(order).build());
    }

    private void statistic(Empresa company, BigDecimal value, String prefix, String suffix, String label,
            int order, boolean active) {
        estadisticaEmpresaRepository.saveAndFlush(EstadisticaEmpresa.builder().empresa(company).valor(value)
                .prefijo(prefix).sufijo(suffix).etiqueta(label).orden(order).activo(active).build());
    }

    private Map<String, Object> seoBody(UUID siteId, String type, UUID unitId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("configuracionSitioId", siteId); body.put("tipoPagina", type);
        body.put("title", "Título"); body.put("description", "Descripción");
        body.put("robots", "INDEX_FOLLOW"); body.put("unidadNegocioId", unitId);
        return body;
    }

    private UUID id(MvcResult result) throws Exception {
        return UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
    }

    private String json(Object value) throws Exception { return objectMapper.writeValueAsString(value); }
    private String random(int length) { return UUID.randomUUID().toString().replace("-", "").substring(0, length); }
}
