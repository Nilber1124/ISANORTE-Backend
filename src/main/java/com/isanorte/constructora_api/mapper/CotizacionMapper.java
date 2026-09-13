package com.isanorte.constructora_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.request.CotizacionRequest;
import com.isanorte.constructora_api.dto.response.CotizacionResponse;
import com.isanorte.constructora_api.model.Cotizacion;
import com.isanorte.constructora_api.model.DetalleCotizacion;
import com.isanorte.constructora_api.model.SeguimientoCotizacion;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CotizacionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codigo", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "totalEstimado", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    @Mapping(target = "seguimientos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Cotizacion toEntity(CotizacionRequest request);

    CotizacionResponse toResponse(Cotizacion cotizacion);

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "varianteId", source = "variante.id")
    CotizacionResponse.DetalleResponse toDetalleResponse(DetalleCotizacion detalle);

    @Mapping(target = "administradorId", source = "administrador.id")
    @Mapping(target = "administradorNombre", source = "administrador.nombre")
    CotizacionResponse.SeguimientoResponse toSeguimientoResponse(SeguimientoCotizacion seguimiento);
}
