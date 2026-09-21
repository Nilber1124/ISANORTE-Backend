package com.isanorte.constructora_api.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.isanorte.constructora_api.dto.response.PublicProductCalculationResponse;
import com.isanorte.constructora_api.dto.response.PublicProductCardResponse;
import com.isanorte.constructora_api.dto.response.PublicProductCategoryResponse;
import com.isanorte.constructora_api.dto.response.PublicProductDetailResponse;
import com.isanorte.constructora_api.dto.response.PublicProductDocumentResponse;
import com.isanorte.constructora_api.dto.response.PublicProductImageResponse;
import com.isanorte.constructora_api.dto.response.PublicProductSpecificationResponse;
import com.isanorte.constructora_api.dto.response.PublicProductVariantResponse;
import com.isanorte.constructora_api.model.CategoriaProducto;
import com.isanorte.constructora_api.model.ConfiguracionCalculo;
import com.isanorte.constructora_api.model.DocumentoProducto;
import com.isanorte.constructora_api.model.EspecificacionProducto;
import com.isanorte.constructora_api.model.ImagenProducto;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.VarianteProducto;

/** Maps the product aggregate to the deliberately small public Product API contracts. */
@Component
public class PublicProductMapper {

    public PublicProductCardResponse toCard(
            Producto product,
            ImagenProducto primaryImage,
            List<PublicProductCategoryResponse> categories) {
        return new PublicProductCardResponse(
                product.getNombre(), product.getSku(), product.getSlug(), product.getResumen(),
                product.getDescripcion(), product.getPrecioBase(), product.getPrecioAnterior(),
                product.getDescuentoPorcentaje(), product.getDisponibilidad(), toImage(primaryImage), categories);
    }

    public PublicProductDetailResponse toDetail(
            Producto product,
            List<PublicProductCategoryResponse> categories,
            List<PublicProductImageResponse> images,
            List<PublicProductVariantResponse> variants,
            List<PublicProductSpecificationResponse> specifications,
            List<PublicProductDocumentResponse> documents) {
        return new PublicProductDetailResponse(
                product.getNombre(), product.getSku(), product.getSlug(), product.getResumen(),
                product.getDescripcion(), product.getPrecioBase(), product.getPrecioAnterior(),
                product.getDescuentoPorcentaje(), product.getDisponibilidad(), categories, images, variants,
                specifications, documents, toCalculation(product.getConfiguracionCalculo()));
    }

    public PublicProductCategoryResponse toCategory(CategoriaProducto category) {
        return new PublicProductCategoryResponse(category.getNombre(), category.getSlug());
    }

    public PublicProductImageResponse toImage(ImagenProducto image) {
        return image == null ? null : new PublicProductImageResponse(
                image.getUrl(), image.getAltText(), image.getEsPrincipal(), image.getOrden());
    }

    public PublicProductVariantResponse toVariant(VarianteProducto variant) {
        return new PublicProductVariantResponse(
                variant.getSku(), variant.getNombre(), variant.getDescripcion(), variant.getPrecio(),
                variant.getDisponible(), variant.getImagenUrl(), variant.getOrden());
    }

    public PublicProductSpecificationResponse toSpecification(EspecificacionProducto specification) {
        return new PublicProductSpecificationResponse(
                specification.getClave(), specification.getValor(), specification.getGrupo(), specification.getOrden());
    }

    public PublicProductDocumentResponse toDocument(DocumentoProducto document) {
        return new PublicProductDocumentResponse(
                document.getTitulo(), document.getUrl(), document.getTipo(), document.getFormato(),
                document.getTamanoBytes());
    }

    public PublicProductCalculationResponse toCalculation(ConfiguracionCalculo calculation) {
        return calculation == null ? null : new PublicProductCalculationResponse(
                calculation.getHabilitada(), calculation.getEtiquetaEntrada(), calculation.getUnidadEntrada(),
                calculation.getCoberturaPorUnidad(), calculation.getUnidadVenta(), calculation.getTextoAyuda());
    }
}
