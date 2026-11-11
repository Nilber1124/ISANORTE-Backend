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
public class ArchivoController {

    private final CloudinaryService cloudinaryService;

    public ArchivoController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping(value = "/subir-imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponseDto> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "isanorte/uploads") String folder) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().build();
        }
        return subir(file, folder);
    }

    @PostMapping(value = "/subir-documento", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponseDto> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "isanorte/documentos") String folder) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            return ResponseEntity.badRequest().build();
        }
        return subir(file, folder);
    }

    private ResponseEntity<UploadResponseDto> subir(MultipartFile file, String folder) {
        try {
            Map<String, Object> result = cloudinaryService.uploadFile(file, folder);

            Object bytesValue = result.get("bytes");
            UploadResponseDto response = UploadResponseDto.builder()
                    .url((String) result.get("secure_url"))
                    .publicId((String) result.get("public_id"))
                    .format((String) result.get("format"))
                    .bytes(bytesValue instanceof Number number ? number.longValue() : null)
                    .build();

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
