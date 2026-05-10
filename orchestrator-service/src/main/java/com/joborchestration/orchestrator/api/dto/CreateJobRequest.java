package com.joborchestration.orchestrator.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record CreateJobRequest(
        @NotBlank String jobType,
        @NotNull Map<String, Object> params) {
}

