package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotNull;

public record VisibleRequest(@NotNull Boolean visible) {
}
