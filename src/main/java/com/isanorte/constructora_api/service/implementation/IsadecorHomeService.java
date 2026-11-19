package com.isanorte.constructora_api.service.implementation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse;
import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.Acabado;
import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.Campana;
import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.Categoria;
import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.Cinta;
import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.Hero;
import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.Imagen;
import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.ProductoCard;
import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.Promocion;
import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.Resena;
import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.SeccionProductos;
import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.Servicios;
import com.isanorte.constructora_api.enums.ClaveSeccionProductos;
import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.enums.EstadoStock;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.ImagenProducto;
import com.isanorte.constructora_api.model.IsadecorSeccionExcluido;
import com.isanorte.constructora_api.model.IsadecorSeccionFijado;
import com.isanorte.constructora_api.model.IsadecorSeccionProductos;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.repository.CotizacionRepository;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.IsadecorCampanaRepository;
import com.isanorte.constructora_api.repository.IsadecorCintaRepository;
import com.isanorte.constructora_api.repository.IsadecorHeroRepository;
import com.isanorte.constructora_api.repository.IsadecorPromocionRepository;
import com.isanorte.constructora_api.repository.IsadecorSeccionExcluidoRepository;
import com.isanorte.constructora_api.repository.IsadecorSeccionFijadoRepository;
import com.isanorte.constructora_api.repository.IsadecorSeccionProductosRepository;
import com.isanorte.constructora_api.repository.IsadecorServicioEmpresaRepository;
import com.isanorte.constructora_api.repository.IsadecorServicioPuntoRepository;
import com.isanorte.constructora_api.repository.PedidoRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.ResenaProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.repository.projection.DemandaProducto;
import com.isanorte.constructora_api.repository.projection.ResumenResena;
import com.isanorte.constructora_api.service.IIsadecorHomeService;
import com.isanorte.constructora_api.util.BusinessTime;
import com.isanorte.constructora_api.util.DisponibilidadStock;

import lombok.RequiredArgsConstructor;

/** Arma la Home pública de una unidad de negocio con el contenido editable y las secciones por demanda. */
@Service
@RequiredArgsConstructor
public class IsadecorHomeService implements IIsadecorHomeService {

    static final int MAX_PROMOCIONES = 3;
    static final int MAX_RESENAS = 6;

    private final ConfiguracionSitioRepository configuracionRepository;
    private final UnidadNegocioRepository unidadRepository;
    private final IsadecorHeroRepository heroRepository;
    private final IsadecorCintaRepository cintaRepository;
    private final IsadecorPromocionRepository promocionRepository;
    private final IsadecorCampanaRepository campanaRepository;
    private final IsadecorServicioEmpresaRepository servicioRepository;
    private final IsadecorServicioPuntoRepository puntoRepository;
    private final IsadecorSeccionProductosRepository seccionRepository;
    private final IsadecorSeccionFijadoRepository fijadoRepository;
    private final IsadecorSeccionExcluidoRepository excluidoRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final CotizacionRepository cotizacionRepository;
    private final ResenaProductoRepository resenaRepository;

    @Override
    @Transactional(readOnly = true)
    public IsadecorHomeResponse findHome(String siteKey, String unitSlug) {
        var site = configuracionRepository.findByClave(siteKey)
                .orElseThrow(() -> new ModelNotFoundException("Sitio público no encontrado con clave: " + siteKey));
        UnidadNegocio unit = unidadRepository.findByEmpresaIdAndSlugAndActivoTrue(site.getEmpresa().getId(), unitSlug)
                .orElseThrow(() -> new ModelNotFoundException("Unidad pública activa no encontrada con slug: " + unitSlug));
        UUID unitId = unit.getId();

        Hero hero = heroRepository.findByUnidadNegocioIdAndActivoTrueOrderByFechaCreacionAscIdAsc(unitId).stream()
                .findFirst().map(h -> new Hero(h.getTitulo(), h.getSubtitulo())).orElse(null);

        List<Cinta> cinta = cintaRepository.findByUnidadNegocioIdAndActivoTrueOrderByOrdenAscIdAsc(unitId).stream()
                .map(c -> new Cinta(c.getTexto(), c.getEnlace())).toList();

        Campana campana = campanaRepository.findByUnidadNegocioIdAndActivoTrueOrderByFechaCreacionAscIdAsc(unitId)
                .stream().findFirst()
                .map(c -> new Campana(c.getEtiqueta(), c.getTitulo(), c.getDescripcion(), c.getTextoBoton(),
                        c.getEnlace(), c.getImagenUrl()))
                .orElse(null);

        Servicios servicios = servicioRepository.findByUnidadNegocioIdAndActivoTrueOrderByFechaCreacionAscIdAsc(unitId)
                .stream().findFirst()
                .map(s -> new Servicios(s.getEtiqueta(), s.getTitulo(), s.getDescripcion(),
                        puntoRepository.findByServicioIdOrderByOrdenAscIdAsc(s.getId()).stream()
                                .map(p -> p.getTexto()).toList(),
                        s.getTextoBoton(), s.getEnlace()))
                .orElse(null);

        Map<UUID, ResumenResena> resumenes = new HashMap<>();
        List<Producto> publicados = productoRepository
                .findByUnidadNegocioIdAndEstadoOrderByNombreAscIdAsc(unitId, EstadoPublicacion.PUBLICADO);

        List<IsadecorSeccionProductos> secciones = new ArrayList<>();
        for (ClaveSeccionProductos clave : ClaveSeccionProductos.values()) {
            seccionRepository.findByUnidadNegocioIdAndClaveAndActivoTrue(unitId, clave).ifPresent(secciones::add);
        }
        Map<UUID, Long> demanda = demanda(secciones);

        Map<ClaveSeccionProductos, List<Producto>> elegidos = new LinkedHashMap<>();
        for (IsadecorSeccionProductos seccion : secciones) {
            elegidos.put(seccion.getClave(), seleccionar(seccion, publicados, demanda));
        }

        List<IsadecorPromocionView> promos = promocionesVigentes(unitId);
        Set<UUID> ids = new java.util.HashSet<>();
        elegidos.values().forEach(l -> l.forEach(p -> ids.add(p.getId())));
        promos.forEach(p -> ids.add(p.producto().getId()));
        if (!ids.isEmpty()) {
            resenaRepository.resumenPorProductos(ids).forEach(r -> resumenes.put(r.getProductoId(), r));
        }

        List<Promocion> promociones = promos.stream()
                .map(p -> new Promocion(p.tipo(), p.titulo(), p.imagenUrl(), p.fechaFin(),
                        card(p.producto(), resumenes)))
                .toList();

        SeccionProductos tienda = seccionRespuesta(secciones, elegidos, ClaveSeccionProductos.TIENDA, resumenes);
        SeccionProductos facil = seccionRespuesta(secciones, elegidos, ClaveSeccionProductos.FACILES, resumenes);

        List<Resena> resenas = resenaRepository
                .findByMostrarEnHomeTrueAndProductoUnidadNegocioIdOrderByOrdenHomeAscFechaCreacionDesc(unitId).stream()
                .filter(r -> r.getProducto().getEstado() == EstadoPublicacion.PUBLICADO)
                .limit(MAX_RESENAS)
                .map(r -> new Resena(r.getNombreCliente(), r.getCalificacion(), r.getComentario(),
                        r.getCompraVerificada(), r.getProducto().getNombre(), r.getProducto().getSlug()))
                .toList();

        return new IsadecorHomeResponse(hero, cinta, promociones, campana, tienda, facil, servicios, resenas);
    }

    private record IsadecorPromocionView(com.isanorte.constructora_api.enums.TipoPromocion tipo, String titulo,
            String imagenUrl, LocalDate fechaFin, Producto producto) {
    }

    private List<IsadecorPromocionView> promocionesVigentes(UUID unitId) {
        LocalDate hoy = BusinessTime.today();
        return promocionRepository.findByUnidadNegocioIdAndActivoTrueOrderByOrdenAscIdAsc(unitId).stream()
                .filter(p -> p.getFechaFin() == null || !p.getFechaFin().isBefore(hoy))
                .filter(p -> p.getProducto().getEstado() == EstadoPublicacion.PUBLICADO
                        && p.getProducto().getDisponibilidad() != EstadoDisponibilidad.AGOTADO)
                .sorted(Comparator.comparing((com.isanorte.constructora_api.model.IsadecorPromocion p) ->
                        !Boolean.TRUE.equals(p.getPrincipal())))
                .limit(MAX_PROMOCIONES)
                .map(p -> new IsadecorPromocionView(p.getTipo(), p.getTitulo(), p.getImagenUrl(), p.getFechaFin(),
                        p.getProducto()))
                .toList();
    }

    private Map<UUID, Long> demanda(List<IsadecorSeccionProductos> secciones) {
        int ventana = secciones.stream().map(IsadecorSeccionProductos::getVentanaDias)
                .filter(v -> v != null && v > 0).max(Integer::compare).orElse(30);
        LocalDateTime desde = BusinessTime.now().minusDays(ventana);
        Map<UUID, Long> total = new HashMap<>();
        for (DemandaProducto d : pedidoRepository.demandaDesde(desde)) {
            total.merge(d.getProductoId(), d.getCantidad(), Long::sum);
        }
        for (DemandaProducto d : cotizacionRepository.demandaDesde(desde)) {
            total.merge(d.getProductoId(), d.getCantidad(), Long::sum);
        }
        return total;
    }

    List<Producto> seleccionar(IsadecorSeccionProductos seccion, List<Producto> publicados, Map<UUID, Long> demanda) {
        Set<UUID> excluidos = excluidoRepository.findBySeccionId(seccion.getId()).stream()
                .map(IsadecorSeccionExcluido::getProducto).map(Producto::getId).collect(Collectors.toSet());
        boolean soloFaciles = seccion.getClave() == ClaveSeccionProductos.FACILES;
        List<Producto> candidatos = publicados.stream()
                .filter(p -> !excluidos.contains(p.getId()))
                .filter(p -> p.getDisponibilidad() != EstadoDisponibilidad.AGOTADO)
                .filter(p -> !soloFaciles || Boolean.TRUE.equals(p.getFacilInstalacion()))
                .toList();
        Map<UUID, Producto> porId = candidatos.stream()
                .collect(Collectors.toMap(Producto::getId, p -> p, (a, b) -> a, LinkedHashMap::new));

        Map<UUID, Producto> resultado = new LinkedHashMap<>();
        var modo = seccion.getModo();
        if (modo != com.isanorte.constructora_api.enums.ModoSeccionProductos.AUTOMATICO) {
            for (IsadecorSeccionFijado f : fijadoRepository.findBySeccionIdOrderByOrdenAscIdAsc(seccion.getId())) {
                Producto p = porId.get(f.getProducto().getId());
                if (p != null) {
                    resultado.putIfAbsent(p.getId(), p);
                }
            }
        }
        if (modo != com.isanorte.constructora_api.enums.ModoSeccionProductos.MANUAL) {
            candidatos.stream()
                    .sorted(Comparator
                            .comparing((Producto p) -> demanda.getOrDefault(p.getId(), 0L)).reversed()
                            .thenComparing(p -> !Boolean.TRUE.equals(p.getDestacado()))
                            .thenComparing(p -> p.getOrdenDestacado() == null ? 0 : p.getOrdenDestacado())
                            .thenComparing(Producto::getNombre))
                    .forEach(p -> resultado.putIfAbsent(p.getId(), p));
        }
        int limite = seccion.getLimite() == null ? 8 : seccion.getLimite();
        return resultado.values().stream().limit(limite).toList();
    }

    private SeccionProductos seccionRespuesta(List<IsadecorSeccionProductos> secciones,
            Map<ClaveSeccionProductos, List<Producto>> elegidos, ClaveSeccionProductos clave,
            Map<UUID, ResumenResena> resumenes) {
        return secciones.stream().filter(s -> s.getClave() == clave).findFirst()
                .map(s -> new SeccionProductos(s.getTitulo(), s.getTextoEnlace(), s.getCategoriaEnlace(),
                        elegidos.get(clave).stream().map(p -> card(p, resumenes)).toList()))
                .orElse(null);
    }

    ProductoCard card(Producto p, Map<UUID, ResumenResena> resumenes) {
        ImagenProducto img = p.getImagenes().stream()
                .sorted(Comparator.comparing((ImagenProducto i) -> !Boolean.TRUE.equals(i.getEsPrincipal()))
                        .thenComparing(i -> i.getOrden() == null ? 0 : i.getOrden()))
                .findFirst().orElse(null);
        List<Categoria> categorias = p.getCategorias().stream()
                .sorted(Comparator.comparing(c -> c.getNombre()))
                .map(c -> new Categoria(c.getNombre(), c.getSlug())).toList();
        Collection<VarianteProducto> variantes = p.getVariantes();
        List<Acabado> acabados = variantes.stream()
                .sorted(Comparator.comparing(v -> v.getOrden() == null ? 0 : v.getOrden()))
                .map(v -> new Acabado(v.getNombre(), v.getColorHex(),
                        v.getStockActual() != null && v.getStockActual() <= 0))
                .toList();
        ResumenResena r = resumenes.get(p.getId());
        var stock = DisponibilidadStock.resumen(p);
        Integer quedan = stock.estado() == EstadoStock.QUEDAN ? stock.quedan() : null;
        return new ProductoCard(p.getSlug(), p.getNombre(), categorias,
                img == null ? null : new Imagen(img.getUrl(), img.getAltText()),
                p.getPrecioBase(), p.getPrecioAnterior(), p.getDescuentoPorcentaje(),
                p.getUnidadVenta(), p.getModalidad(), p.getPlazoEntrega(),
                r == null ? null : r.getPromedio(), r == null ? 0L : r.getCantidad(),
                acabados, stock.estado(), quedan);
    }
}
