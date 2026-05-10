package com.joborchestration.orchestrator.api.dto;

public record ImageCompressionGrantResponse(
        JobResponse job,
        String storageKey,
        String uploadUrl,
        String uploadMethod) {
}
