package com.joborchestration.orchestrator.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateImageCompressionRequest(
        @NotBlank String filename,
        @NotBlank String type) {
}
