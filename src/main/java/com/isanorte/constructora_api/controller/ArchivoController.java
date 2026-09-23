package com.isanorte.constructora_api.controller;

import com.isanorte.constructora_api.dto.UploadResponseDto;
import com.isanorte.constructora_api.service.CloudinaryService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/archivos")
@CrossOrigin(origins = "*") // Para desarrollo local temporalmente
public class ArchivoController {

    private final CloudinaryService cloudinaryService;

    public ArchivoController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping(value = "/subir-imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponseDto> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "isanorte/uploads") String folder) {
        try {
            // Validar que sea imagen
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().build();
            }

            Map<String, Object> result = cloudinaryService.uploadFile(file, folder);

            UploadResponseDto response = UploadResponseDto.builder()
                    .url((String) result.get("secure_url"))
                    .publicId((String) result.get("public_id"))
                    .format((String) result.get("format"))
                    .build();

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
