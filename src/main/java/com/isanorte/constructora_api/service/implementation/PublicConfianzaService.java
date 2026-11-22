package com.isanorte.constructora_api.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.response.PublicConfianzaResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.CredencialRepository;
import com.isanorte.constructora_api.repository.FaqRepository;
import com.isanorte.constructora_api.repository.GarantiaRepository;
import com.isanorte.constructora_api.repository.MiembroEquipoRepository;
import com.isanorte.constructora_api.repository.PasoProcesoRepository;
import com.isanorte.constructora_api.repository.PoliticaRepository;
import com.isanorte.constructora_api.repository.ProyectoGaleriaRepository;
import com.isanorte.constructora_api.repository.TestimonioRepository;
import com.isanorte.constructora_api.service.IPublicConfianzaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicConfianzaService implements IPublicConfianzaService {

    private final ConfiguracionSitioRepository configuracionRepository;
    private final FaqRepository faqRepository;
    private final MiembroEquipoRepository equipoRepository;
    private final CredencialRepository credencialRepository;
    private final TestimonioRepository testimonioRepository;
    private final GarantiaRepository garantiaRepository;
    private final PasoProcesoRepository pasoRepository;
    private final ProyectoGaleriaRepository galeriaRepository;
    private final PoliticaRepository politicaRepository;

    @Override
    @Transactional(readOnly = true)
    public PublicConfianzaResponse findConfianza(String clave) {
        configuracionRepository.findByClave(clave)
                .orElseThrow(() -> new ModelNotFoundException("Sitio público no encontrado con clave: " + clave));

        return new PublicConfianzaResponse(
                faqRepository.findByActivoTrueOrderByOrdenAscIdAsc().stream()
                        .map(item -> new PublicConfianzaResponse.Faq(
                                item.getPregunta(), item.getRespuesta(), item.getAmbito()))
                        .toList(),
                equipoRepository.findByActivoTrueOrderByOrdenAscIdAsc().stream()
                        .map(item -> new PublicConfianzaResponse.Miembro(
                                item.getNombre(), item.getCargo(), item.getEspecialidad(),
                                item.getBiografia(), item.getFotoUrl(), item.getFotoAlt()))
                        .toList(),
                credencialRepository.findByActivoTrueOrderByOrdenAscIdAsc().stream()
                        .map(item -> new PublicConfianzaResponse.Credencial(
                                item.getNombre(), item.getEntidad(), item.getTipo(), item.getCodigo(),
                                item.getAnio(), item.getImagenUrl(), item.getImagenAlt()))
                        .toList(),
                testimonioRepository.findByActivoTrueOrderByOrdenAscIdAsc().stream()
                        .map(item -> new PublicConfianzaResponse.Testimonio(
                                item.getAutor(), item.getCargo(), item.getTexto(), item.getProyectoSlug(),
                                item.getFotoUrl(), item.getFotoAlt()))
                        .toList(),
                garantiaRepository.findByActivoTrueOrderByOrdenAscIdAsc().stream()
                        .map(item -> new PublicConfianzaResponse.Garantia(
                                item.getId(), item.getTitulo(), item.getTexto(), Boolean.TRUE.equals(item.getEnHero())))
                        .toList(),
                pasoRepository.findByActivoTrueOrderByOrdenAscIdAsc().stream()
                        .map(item -> new PublicConfianzaResponse.Paso(item.getTitulo(), item.getTexto()))
                        .toList(),
                galeriaRepository.findByActivoTrueOrderByOrdenAscIdAsc().stream()
                        .map(item -> new PublicConfianzaResponse.ImagenProyecto(
                                item.getProyectoSlug(), item.getUrl(), item.getAlt(), item.getEtiqueta()))
                        .toList(),
                politicaRepository.findByActivoTrueOrderByOrdenAscIdAsc().stream()
                        .map(item -> new PublicConfianzaResponse.Politica(
                                item.getSlug(), item.getTitulo(), item.getContenido()))
                        .toList());
    }
}
