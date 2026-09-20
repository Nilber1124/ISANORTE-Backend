package com.isanorte.constructora_api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.enums.EstadoSolicitudContacto;
import com.isanorte.constructora_api.enums.RobotsSeo;
import com.isanorte.constructora_api.enums.TipoImagenProyecto;
import com.isanorte.constructora_api.enums.TipoPaginaPublica;
import com.isanorte.constructora_api.enums.TipoPaginaSeo;
import com.isanorte.constructora_api.enums.TipoRecursoUnidadNegocio;
import com.isanorte.constructora_api.enums.TipoSeccionLanding;
import com.isanorte.constructora_api.model.AccionLanding;
import com.isanorte.constructora_api.model.BeneficioServicio;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.ContenidoPagina;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.EstadisticaEmpresa;
import com.isanorte.constructora_api.model.HeroScene;
import com.isanorte.constructora_api.model.ImagenProyecto;
import com.isanorte.constructora_api.model.Proyecto;
import com.isanorte.constructora_api.model.RecursoUnidadNegocio;
import com.isanorte.constructora_api.model.SeccionLanding;
import com.isanorte.constructora_api.model.SeoPagina;
import com.isanorte.constructora_api.model.Servicio;
import com.isanorte.constructora_api.model.SolicitudContacto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.AccionLandingRepository;
import com.isanorte.constructora_api.repository.BeneficioServicioRepository;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.ContenidoPaginaRepository;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.EstadisticaEmpresaRepository;
import com.isanorte.constructora_api.repository.HeroSceneRepository;
import com.isanorte.constructora_api.repository.ProyectoRepository;
import com.isanorte.constructora_api.repository.RecursoUnidadNegocioRepository;
import com.isanorte.constructora_api.repository.SeoPaginaRepository;
import com.isanorte.constructora_api.repository.ServicioRepository;
import com.isanorte.constructora_api.repository.SolicitudContactoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DynamicContentPersistenceIntegrationTest {

    @Autowired private Flyway flyway;
    @Autowired private EntityManager entityManager;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private ConfiguracionSitioRepository configuracionSitioRepository;
    @Autowired private HeroSceneRepository heroSceneRepository;
    @Autowired private AccionLandingRepository accionLandingRepository;
    @Autowired private UnidadNegocioRepository unidadNegocioRepository;
    @Autowired private RecursoUnidadNegocioRepository recursoUnidadNegocioRepository;
    @Autowired private ServicioRepository servicioRepository;
    @Autowired private BeneficioServicioRepository beneficioServicioRepository;
    @Autowired private ProyectoRepository proyectoRepository;
    @Autowired private EstadisticaEmpresaRepository estadisticaEmpresaRepository;
    @Autowired private ContenidoPaginaRepository contenidoPaginaRepository;
    @Autowired private SeoPaginaRepository seoPaginaRepository;
    @Autowired private SolicitudContactoRepository solicitudContactoRepository;

    @Test
    void flywayAplicaBaselineYEvolucionAntesDeValidarJpa() {
        assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("2");
        assertThat(flyway.info().applied())
                .extracting(info -> info.getVersion().getVersion())
                .contains("1", "2");
    }

    @Test
    void persisteEscenasYAccionesConOwnershipYOrphanRemoval() {
        ConfiguracionSitio sitio = persistedSite("landing");
        SeccionLanding hero = SeccionLanding.builder().tipo(TipoSeccionLanding.HERO).build();
        HeroScene escena = HeroScene.builder().imagenUrl("/hero.webp").alt("Edificio").build();
        AccionLanding accion = AccionLanding.builder().texto("Conócenos").enlace("/nosotros").build();
        hero.addEscena(escena);
        hero.addAccion(accion);
        sitio.addSeccion(hero);
        entityManager.persist(hero);
        heroSceneRepository.saveAndFlush(escena);
        accionLandingRepository.saveAndFlush(accion);

        UUID escenaId = escena.getId();
        UUID accionId = accion.getId();
        assertThat(escena.getSeccionLanding()).isSameAs(hero);
        assertThat(accion.getSeccionLanding()).isSameAs(hero);
        assertThat(escena.getOrden()).isZero();
        assertThat(accion.getOrden()).isZero();

        hero.removeEscena(escena);
        hero.removeAccion(accion);
        entityManager.flush();
        entityManager.clear();
        assertThat(heroSceneRepository.existsById(escenaId)).isFalse();
        assertThat(accionLandingRepository.existsById(accionId)).isFalse();
    }

    @Test
    void persisteUnidadDestacadaYRecursosConOrphanRemoval() {
        Empresa empresa = persistedCompany("unidad");
        UnidadNegocio unidad = UnidadNegocio.builder()
                .nombre("ISADECOR").slug("isadecor-test").empresa(empresa).destacado(true).build();
        RecursoUnidadNegocio recurso = RecursoUnidadNegocio.builder()
                .tipo(TipoRecursoUnidadNegocio.IMAGEN_EDITORIAL)
                .url("/unidad.webp").alt("Interior terminado").build();
        unidad.addRecurso(recurso);
        unidadNegocioRepository.saveAndFlush(unidad);

        UUID recursoId = recurso.getId();
        assertThat(unidad.getDestacado()).isTrue();
        assertThat(unidadNegocioRepository.existsByEmpresaIdAndActivoTrueAndDestacadoTrue(empresa.getId())).isTrue();
        assertThat(recurso.getOrden()).isZero();
        assertThat(recurso.getUnidadNegocio()).isSameAs(unidad);

        unidad.removeRecurso(recurso);
        unidadNegocioRepository.saveAndFlush(unidad);
        entityManager.clear();
        assertThat(recursoUnidadNegocioRepository.existsById(recursoId)).isFalse();
    }

    @Test
    void persisteBeneficioServicioYAplicaOrphanRemoval() {
        Servicio servicio = Servicio.builder()
                .nombre("Construcción").slug("construccion-test").descripcion("Descripción").build();
        BeneficioServicio beneficio = BeneficioServicio.builder().texto("Supervisión especializada").build();
        servicio.addBeneficio(beneficio);
        servicioRepository.saveAndFlush(servicio);

        UUID beneficioId = beneficio.getId();
        assertThat(beneficioServicioRepository.findById(beneficioId)).get()
                .extracting(BeneficioServicio::getOrden).isEqualTo(0);

        servicio.removeBeneficio(beneficio);
        servicioRepository.saveAndFlush(servicio);
        entityManager.clear();
        assertThat(beneficioServicioRepository.existsById(beneficioId)).isFalse();
    }

    @Test
    void persisteOrdenDeProyectoYAltDeImagen() {
        Proyecto proyecto = Proyecto.builder()
                .nombre("Proyecto Norte").slug("proyecto-norte-test").descripcion("Descripción")
                .orden(4).build();
        ImagenProyecto imagen = ImagenProyecto.builder()
                .url("/proyecto.webp").alt("Fachada del proyecto")
                .tipo(TipoImagenProyecto.GENERAL).build();
        proyecto.addImagen(imagen);
        UUID proyectoId = proyectoRepository.saveAndFlush(proyecto).getId();
        entityManager.clear();

        Proyecto guardado = proyectoRepository.findById(proyectoId).orElseThrow();
        assertThat(guardado.getOrden()).isEqualTo(4);
        assertThat(guardado.getImagenes()).singleElement()
                .extracting(ImagenProyecto::getAlt).isEqualTo("Fachada del proyecto");
    }

    @Test
    void persisteEstadisticaConOrdenCeroYAplicaOrphanRemoval() {
        Empresa empresa = persistedCompany("estadistica");
        EstadisticaEmpresa estadistica = EstadisticaEmpresa.builder()
                .valor(new BigDecimal("120")).prefijo("+").etiqueta("Proyectos").build();
        empresa.addEstadistica(estadistica);
        estadisticaEmpresaRepository.saveAndFlush(estadistica);

        UUID id = estadistica.getId();
        assertThat(estadistica.getOrden()).isZero();
        empresa.removeEstadistica(estadistica);
        entityManager.flush();
        entityManager.clear();
        assertThat(estadisticaEmpresaRepository.existsById(id)).isFalse();
    }

    @Test
    void conservaOrdenDeTagsDeContenidoPagina() {
        ConfiguracionSitio sitio = persistedSite("contenido");
        assertThat(sitio.getClave()).isEqualTo("isanorte-contenido");
        assertThat(configuracionSitioRepository.findByClave("isanorte-contenido")).contains(sitio);
        ContenidoPagina contenido = ContenidoPagina.builder()
                .pagina(TipoPaginaPublica.NOSOTROS).titulo("Nosotros")
                .tags(List.of("Arquitectura", "Construcción", "Obra Civil", "Acabados"))
                .configuracionSitio(sitio).build();
        UUID id = contenidoPaginaRepository.saveAndFlush(contenido).getId();
        entityManager.clear();

        assertThat(contenidoPaginaRepository.findById(id).orElseThrow().getTags())
                .containsExactly("Arquitectura", "Construcción", "Obra Civil", "Acabados");
    }

    @Test
    void impideContenidoDuplicadoParaSitioYPagina() {
        ConfiguracionSitio sitio = persistedSite("contenido-unico");
        contenidoPaginaRepository.saveAndFlush(ContenidoPagina.builder()
                .pagina(TipoPaginaPublica.CONTACTO).configuracionSitio(sitio).build());

        assertThatThrownBy(() -> contenidoPaginaRepository.saveAndFlush(ContenidoPagina.builder()
                .pagina(TipoPaginaPublica.CONTACTO).configuracionSitio(sitio).build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void normalizaEImpideClaveDeSitioDuplicada() {
        persistedSite("clave-unica");
        Empresa otraEmpresa = persistedCompany("otra-clave");

        assertThatThrownBy(() -> configuracionSitioRepository.saveAndFlush(ConfiguracionSitio.builder()
                .clave("  ISANORTE CLAVE ÚNICA  ").empresa(otraEmpresa).build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void persisteSeoEImpideUnidadEnPaginaEstatica() {
        ConfiguracionSitio sitio = persistedSite("seo");
        Empresa empresa = sitio.getEmpresa();
        UnidadNegocio unidad = unidadNegocioRepository.saveAndFlush(UnidadNegocio.builder()
                .nombre("Unidad SEO").slug("unidad-seo-test").empresa(empresa).build());
        SeoPagina seoUnidad = SeoPagina.builder()
                .tipoPagina(TipoPaginaSeo.UNIDAD_NEGOCIO).title("Unidad")
                .description("Descripción SEO").robots(RobotsSeo.INDEX_FOLLOW)
                .configuracionSitio(sitio).unidadNegocio(unidad).build();
        UUID id = seoPaginaRepository.saveAndFlush(seoUnidad).getId();
        assertThat(seoPaginaRepository.findById(id)).get()
                .extracting(SeoPagina::getUnidadNegocio).isEqualTo(unidad);

        assertThatThrownBy(() -> seoPaginaRepository.saveAndFlush(SeoPagina.builder()
                .tipoPagina(TipoPaginaSeo.HOME).title("Home").description("Descripción")
                .configuracionSitio(sitio).unidadNegocio(unidad).build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void persisteSolicitudContactoConEstado() {
        SolicitudContacto solicitud = SolicitudContacto.builder()
                .nombre("Ana Pérez").email("ana@example.com").telefono("999999999")
                .empresa("Empresa demo").mensaje("Quiero información").build();
        UUID id = solicitudContactoRepository.saveAndFlush(solicitud).getId();
        entityManager.clear();

        SolicitudContacto guardada = solicitudContactoRepository.findById(id).orElseThrow();
        assertThat(guardada.getEstado()).isEqualTo(EstadoSolicitudContacto.NUEVA);
        assertThat(guardada.getFechaCreacion()).isNotNull();
    }

    private ConfiguracionSitio persistedSite(String seed) {
        Empresa empresa = persistedCompany(seed);
        ConfiguracionSitio sitio = ConfiguracionSitio.builder()
                .clave("ISANORTE " + seed).empresa(empresa).build();
        empresa.setConfiguracionSitio(sitio);
        return configuracionSitioRepository.saveAndFlush(sitio);
    }

    private Empresa persistedCompany(String seed) {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return empresaRepository.saveAndFlush(Empresa.builder()
                .razonSocial("ISANORTE " + seed)
                .nombreComercial("ISANORTE " + seed)
                .ruc(suffix)
                .build());
    }
}
