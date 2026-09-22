package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.Size;

// Empty/malformed URLs are domain outcomes (INVALID_URL). Oversized bodies are HTTP 400.
public record ComparacionPrecioRequest(@Size(max = 2048) String urlExterna) {}

