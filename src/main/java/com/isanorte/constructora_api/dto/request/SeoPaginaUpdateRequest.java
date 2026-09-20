package com.isanorte.constructora_api.dto.request;

import com.isanorte.constructora_api.enums.RobotsSeo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SeoPaginaUpdateRequest(
        @NotBlank @Size(max = 180) String title,
        @NotBlank @Size(max = 320) String description,
        @Size(max = 500) String ogImageUrl,
        @NotNull RobotsSeo robots) {
}
