package com.joborchestration.orchestrator.api.dto;

import com.joborchestration.jobdefinition.JobState;
import java.time.Instant;
import java.util.Map;

public record JobResponse(
        Long id,
        String jobType,
        JobState state,
        int retries,
        String requestedBy,
        Map<String, Object> params,
        Map<String, Object> outputParams,
        Instant createdAt,
        Instant updatedAt,
        Instant lastStateChangeAt) {
}

