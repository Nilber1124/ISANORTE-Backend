package com.isanorte.constructora_api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map<String, Object> uploadFile(MultipartFile file, String folder) throws IOException {
        String filename = file.getOriginalFilename();
        String publicId = UUID.randomUUID().toString();
        if (filename != null && filename.contains(".")) {
            publicId = filename.substring(0, filename.lastIndexOf('.')) + "_" + publicId;
        }

        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,
                "public_id", publicId,
                "overwrite", true,
                "resource_type", "auto"
        );

        return cloudinary.uploader().upload(file.getBytes(), options);
    }

    public Map<String, Object> deleteFile(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    /**
     * Extrae el public_id de Cloudinary a partir de una URL segura.
     * Retorna vacío si la URL no pertenece a Cloudinary.
     */
    public Optional<String> extractPublicId(String url) {
        if (url == null || !url.contains("res.cloudinary.com")) return Optional.empty();
        int uploadIdx = url.indexOf("/upload/");
        if (uploadIdx < 0) return Optional.empty();
        String after = url.substring(uploadIdx + "/upload/".length());
        after = after.replaceFirst("^v\\d+/", "");
        int lastDot = after.lastIndexOf('.');
        if (lastDot > 0) after = after.substring(0, lastDot);
        return after.isBlank() ? Optional.empty() : Optional.of(after);
    }
}
