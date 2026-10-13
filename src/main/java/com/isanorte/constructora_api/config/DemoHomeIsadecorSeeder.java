package com.isanorte.constructora_api.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.enums.DestinoAnuncio;
import com.isanorte.constructora_api.enums.TipoAccionAnuncio;
import com.isanorte.constructora_api.enums.UbicacionAnuncio;
import com.isanorte.constructora_api.model.Anuncio;
import com.isanorte.constructora_api.repository.AnuncioRepository;

import lombok.RequiredArgsConstructor;

/** Seed opt-in e idempotente del contenido demostrativo del Home ISADECOR. */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.demo-home-isadecor", havingValue = "true")
public class DemoHomeIsadecorSeeder implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoHomeIsadecorSeeder.class);
    private static final String PREFIX = "DEMO-HOME-";
    private final AnuncioRepository anuncioRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        int inserted = 0;
        for (DemoItem item : items()) {
            String tag = PREFIX + item.key();
            if (anuncioRepository.existsByEtiquetaAndDestinoAndUbicacion(
                    tag, DestinoAnuncio.ISADECOR, item.location())) continue;
            anuncioRepository.save(Anuncio.builder()
                    .titulo(item.title()).descripcionResumida(item.description())
                    .contenidoDetallado(item.description()).imagenUrl(item.image())
                    .imagenHoverUrl(item.hoverImage()).etiqueta(tag)
                    .destino(DestinoAnuncio.ISADECOR).ubicacion(item.location())
                    .tipoAccion(item.actionType()).destinoAccion(item.destination())
                    .textoBoton(item.button()).activo(true).orden(item.order()).build());
            inserted++;
        }
        log.info("Seed demo Home ISADECOR finalizado: {} elementos insertados.", inserted);
    }

    private List<DemoItem> items() {
        return List.of(
                item("CINTA-WPC", "Paneles WPC", "Acabados decorativos para muros.", "/images/isadecor-left-1.jpg", null, UbicacionAnuncio.CINTA_HOME, TipoAccionAnuncio.CATEGORIA, "wall-panels", "Explorar", 0),
                item("CINTA-SPC", "Mármol SPC", "Superficies contemporáneas de demostración.", "/images/isadecor-left-2.jpg", null, UbicacionAnuncio.CINTA_HOME, TipoAccionAnuncio.CATEGORIA, "revestimientos", "Explorar", 1),
                item("PROMO-PRINCIPAL", "Texturas que definen el ambiente", "Composición de muestra para presentar colecciones ISADECOR.", "/images/isadecor-fondo.jpg", null, UbicacionAnuncio.PROMOCION_PRINCIPAL, TipoAccionAnuncio.RUTA_INTERNA, "/isadecor/catalogo", "Ver catálogo", 0),
                item("PROMO-SEC-1", "Superficies contemporáneas", "Espacio demostrativo para novedades de acabados.", "/images/isadecor-left-2.jpg", null, UbicacionAnuncio.PROMOCION_SECUNDARIA, TipoAccionAnuncio.RUTA_INTERNA, "/isadecor/catalogo", "Ver colección", 0),
                item("PROMO-SEC-2", "Detalles para cada proyecto", "Presentación visual reemplazable desde administración.", "/images/proyectos-showroom-isadecor.jpg", null, UbicacionAnuncio.PROMOCION_SECUNDARIA, TipoAccionAnuncio.RUTA_INTERNA, "/isadecor/catalogo", "Descubrir", 1),
                item("ANUNCIO-1", "Descubre acabados en detalle", "Colecciones, novedades y asesoría para elegir materiales.", "/images/recorrido-sala.jpg", null, UbicacionAnuncio.ANUNCIOS_HOME, TipoAccionAnuncio.RUTA_INTERNA, "/isadecor/catalogo", "Conocer más", 0),
                item("ANUNCIO-2", "Materiales para cada proyecto", "Soluciones decorativas para espacios residenciales y comerciales.", "/images/recorrido-cocina.jpg", null, UbicacionAnuncio.ANUNCIOS_HOME, TipoAccionAnuncio.RUTA_INTERNA, "/isadecor/catalogo", "Explorar", 1),
                category("CAT-WPC", "Wall Panel WPC", "Textura y calidez para muros.", "/images/isadecor-left-1.jpg", "/images/recorrido-sala.jpg", "wall-panels", 0),
                category("CAT-MARMOL", "Planchas SPC Mármol", "Superficies de gran formato.", "/images/isadecor-left-2.jpg", "/images/recorrido-cocina.jpg", "revestimientos", 1),
                category("CAT-CIELO", "Cielo Raso PVC", "Sistemas prácticos para techos.", "/images/isadecor-top.jpg", "/images/recorrido-bano.jpg", "paneles-decorativos", 2),
                category("CAT-PISOS", "Pisos y Molduras", "Terminaciones para completar el ambiente.", "/images/isadecor-fondo.jpg", "/images/recorrido-dormitorio.jpg", "pisos", 3));
    }

    private DemoItem category(String key, String title, String description, String image,
            String hoverImage, String slug, int order) {
        return item(key, title, description, image, hoverImage, UbicacionAnuncio.CATEGORIA_SUPERIOR,
                TipoAccionAnuncio.CATEGORIA, slug, "Ver categoría", order);
    }

    private DemoItem item(String key, String title, String description, String image,
            String hoverImage, UbicacionAnuncio location, TipoAccionAnuncio actionType,
            String destination, String button, int order) {
        return new DemoItem(key, title, description, image, hoverImage, location,
                actionType, destination, button, order);
    }

    private record DemoItem(String key, String title, String description, String image,
            String hoverImage, UbicacionAnuncio location, TipoAccionAnuncio actionType,
            String destination, String button, int order) { }
}
