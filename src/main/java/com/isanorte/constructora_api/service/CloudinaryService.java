package com.isanorte.constructora_api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
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
}
